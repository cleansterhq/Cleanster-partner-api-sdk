/**
 * Cleanster MCP Server — entry point.
 *
 * Transport selection:
 *   MCP_TRANSPORT=stdio  →  StdioServerTransport (Claude Desktop, direct pipe)
 *   MCP_TRANSPORT=http   →  SSEServerTransport over Express (default, remote access)
 */

import 'dotenv/config';
import express from 'express';
import rateLimit from 'express-rate-limit';
import pino from 'pino';
import { SSEServerTransport } from '@modelcontextprotocol/sdk/server/sse.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import { buildMcpServer } from './server.js';
import { requireAuth } from './auth/middleware.js';

const log = pino({
  level: process.env['LOG_LEVEL'] ?? 'info',
  ...(process.env['LOG_PRETTY'] === 'true'
    ? { transport: { target: 'pino-pretty', options: { colorize: true } } }
    : {}),
  redact: {
    paths: ['req.headers.authorization', 'headers.authorization'],
    censor: '[REDACTED]',
  },
});

// ── Stdio transport (Claude Desktop) ─────────────────────────────────────────

async function startStdio(): Promise<void> {
  const accessKey = process.env['CLEANSTER_ACCESS_KEY'] ?? process.env['CLEANSTER_API_KEY'] ?? '';
  const token = process.env['CLEANSTER_TOKEN'] ?? '';
  if (!accessKey) {
    log.warn('CLEANSTER_ACCESS_KEY not set — requests to the Cleanster API will fail');
  }
  const server = buildMcpServer(accessKey, token);
  const transport = new StdioServerTransport();
  await server.connect(transport);
  log.info('Cleanster MCP server running on stdio');
}

// ── HTTP/SSE transport (Express) ─────────────────────────────────────────────

async function startHttp(): Promise<void> {
  const port = parseInt(process.env['MCP_SERVER_PORT'] ?? '8000', 10);
  const app = express();

  app.use(express.json());

  // ── Rate limiter — 60 requests per minute per bearer token ────────────────
  const limiter = rateLimit({
    windowMs: 60 * 1000,
    max: 60,
    standardHeaders: true,
    legacyHeaders: false,
    keyGenerator: (req) =>
      (req.headers['authorization'] ?? req.ip ?? 'anonymous').toString(),
    message: { error: 'Too Many Requests', message: 'Rate limit: 60 requests per minute.' },
  });

  app.use(limiter);

  // ── Request logger ─────────────────────────────────────────────────────────
  app.use((req, _res, next) => {
    log.info({ method: req.method, url: req.url }, 'incoming request');
    next();
  });

  // ── Health check ───────────────────────────────────────────────────────────
  app.get('/health', (_req, res) => {
    res.json({ status: 'ok', service: 'cleanster-mcp-server', version: '1.0.0' });
  });

  // ── Active SSE transports, keyed by sessionId ─────────────────────────────
  const transports = new Map<string, SSEServerTransport>();

  // ── SSE endpoint — establish MCP connection ────────────────────────────────
  app.get('/sse', requireAuth, (req, res) => {
    const accessKey = process.env['CLEANSTER_ACCESS_KEY'] ?? process.env['CLEANSTER_API_KEY'] ?? '';
    const token = req.tokenInfo!.token;
    const server = buildMcpServer(accessKey, token);
    const transport = new SSEServerTransport('/message', res);

    transports.set(transport.sessionId, transport);
    log.info({ sessionId: transport.sessionId }, 'SSE session opened');

    server.connect(transport).catch((err: unknown) => {
      log.error({ err }, 'MCP server connection error');
    });

    req.on('close', () => {
      transports.delete(transport.sessionId);
      log.info({ sessionId: transport.sessionId }, 'SSE session closed');
    });
  });

  // ── Message endpoint — receive tool call POSTs ────────────────────────────
  app.post('/message', requireAuth, async (req, res) => {
    const sessionId = req.query['sessionId'] as string | undefined;
    if (!sessionId) {
      res.status(400).json({ error: 'Missing sessionId query parameter' });
      return;
    }
    const transport = transports.get(sessionId);
    if (!transport) {
      res.status(404).json({ error: 'Session not found or expired' });
      return;
    }
    try {
      await transport.handlePostMessage(req, res);
    } catch (err) {
      log.error({ err, sessionId }, 'Error handling MCP message');
      if (!res.headersSent) {
        res.status(500).json({ error: 'Internal server error' });
      }
    }
  });

  app.listen(port, () => {
    log.info({ port }, `Cleanster MCP server listening (HTTP/SSE)`);
    log.info(`  Health:  http://localhost:${port}/health`);
    log.info(`  SSE:     http://localhost:${port}/sse`);
    log.info(`  Message: http://localhost:${port}/message?sessionId=<id>`);
  });
}

// ── Entry ─────────────────────────────────────────────────────────────────────

const transport = process.env['MCP_TRANSPORT'] ?? 'http';

if (transport === 'stdio') {
  startStdio().catch((err) => {
    console.error(err);
    process.exit(1);
  });
} else {
  startHttp().catch((err) => {
    console.error(err);
    process.exit(1);
  });
}
