/**
 * MCP Server bootstrap.
 *
 * Builds a fully-configured McpServer instance with all 11 Cleanster tools
 * registered. The server is transport-agnostic — the caller (index.ts) wires
 * it to either StdioServerTransport or SSEServerTransport.
 */

import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import type { ZodRawShape } from 'zod';
import { CleansterApiClient } from './api/cleanster.js';

import * as listBookings from './tools/list_bookings.js';
import * as getBooking from './tools/get_booking.js';
import * as listProperties from './tools/list_properties.js';
import * as getProperty from './tools/get_property.js';
import * as listCleaners from './tools/list_cleaners.js';
import * as getCleaner from './tools/get_cleaner.js';
import * as createBooking from './tools/create_booking.js';
import * as cancelBooking from './tools/cancel_booking.js';
import * as rescheduleBooking from './tools/reschedule_booking.js';
import * as assignCrew from './tools/assign_crew.js';
import * as assignChecklist from './tools/update_checklist.js';

/**
 * The contract every tool module must satisfy.
 * `inputSchema` is a z.object(); we only need its `.shape` at registration time.
 */
interface ToolModule {
  name: string;
  description: string;
  inputSchema: { shape: ZodRawShape };
  handler: (
    params: Record<string, unknown>,
    api: CleansterApiClient,
  ) => Promise<{ content: Array<{ type: 'text'; text: string }> }>;
}

const TOOLS: ToolModule[] = [
  listBookings,
  getBooking,
  listProperties,
  getProperty,
  listCleaners,
  getCleaner,
  createBooking,
  cancelBooking,
  rescheduleBooking,
  assignCrew,
  assignChecklist,
] as ToolModule[];

/**
 * Build and return a new McpServer instance with a dedicated API client.
 *
 * @param accessKey  The Cleanster partner access key (CLEANSTER_ACCESS_KEY env var).
 * @param token      Optional user-level auth token (per-session or CLEANSTER_TOKEN env var).
 *
 * Create one server per connection so each client gets its own isolated auth context.
 */
export function buildMcpServer(accessKey: string, token?: string): McpServer {
  const api = new CleansterApiClient(accessKey, token);
  const server = new McpServer({
    name: 'cleanster',
    version: '1.0.0',
  });

  for (const tool of TOOLS) {
    server.tool(
      tool.name,
      tool.description,
      tool.inputSchema.shape,
      async (params) => tool.handler(params as Record<string, unknown>, api),
    );
  }

  return server;
}
