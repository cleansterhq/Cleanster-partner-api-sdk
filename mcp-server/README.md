# Cleanster MCP Server

A production-ready [Model Context Protocol (MCP)](https://modelcontextprotocol.io) server for the Cleanster Partner API. Lets AI assistants like **Claude** interact with the Cleanster platform — booking jobs, managing properties, assigning cleaners, and more — through natural language.

---

## Overview

| Item | Detail |
|---|---|
| Protocol | MCP 1.0 (Model Context Protocol) |
| Language | TypeScript (ESM, Node 20+) |
| Transport | HTTP/SSE (default) or stdio (Claude Desktop) |
| Auth | Bearer token (API key); OAuth 2.0 + PKCE seam included |
| Tools | 11 (6 read-only, 5 write) |
| Rate Limit | 60 requests / minute / token |
| Tests | 44 (Vitest, no real API calls) |

---

## Tools

### Read operations

| Tool | Description |
|---|---|
| `list_bookings` | List bookings filtered by property, status, or date range |
| `get_booking` | Get full details for a single booking |
| `list_properties` | List registered properties, filter by type |
| `get_property` | Get full property details including cleaners and iCal |
| `list_cleaners` | List cleaners by region or availability date |
| `get_payout_records` | Get payout records for a date range |

### Write operations

| Tool | Description |
|---|---|
| `create_booking` | Create a new cleaning booking |
| `cancel_booking` | Cancel a booking with an optional reason |
| `reschedule_booking` | Move a booking to a new date/time |
| `assign_crew` | Assign one or more cleaners to a booking |
| `update_checklist` | Set checklist items and required flags for a booking |

---

## Local Setup

### 1. Install dependencies

```bash
cd mcp-server
npm install
# or: pnpm install
```

### 2. Configure environment

```bash
cp .env.example .env
```

Edit `.env`:

```env
CLEANSTER_API_BASE_URL=https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public
MCP_SERVER_PORT=3100
MCP_TRANSPORT=http
LOG_LEVEL=info
LOG_PRETTY=true
```

### 3. Start in development mode

```bash
npm run dev
```

The server starts at `http://localhost:3100`.

```
Health:  http://localhost:3100/health
SSE:     http://localhost:3100/sse        (Authorization: Bearer <key>)
Message: http://localhost:3100/message?sessionId=<id>
```

### 4. Run tests

```bash
npm test
```

### 5. Build for production

```bash
npm run build
npm start
```

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `CLEANSTER_API_BASE_URL` | *(required)* | Base URL of the Cleanster Partner REST API |
| `MCP_SERVER_PORT` | `3100` | Port for the HTTP/SSE server |
| `MCP_TRANSPORT` | `http` | `http` (Express/SSE) or `stdio` (Claude Desktop) |
| `LOG_LEVEL` | `info` | Pino log level: `trace` `debug` `info` `warn` `error` |
| `LOG_PRETTY` | `true` | Pretty-print logs (set `false` in production) |
| `CLEANSTER_API_KEY` | — | Required when `MCP_TRANSPORT=stdio` |

---

## Connecting from Claude Desktop

### Option A — stdio (recommended for local use)

Add this to your `claude_desktop_config.json` (typically at `~/Library/Application Support/Claude/claude_desktop_config.json` on macOS):

```json
{
  "mcpServers": {
    "cleanster": {
      "command": "node",
      "args": ["/absolute/path/to/mcp-server/dist/index.js"],
      "env": {
        "MCP_TRANSPORT": "stdio",
        "CLEANSTER_API_KEY": "your-api-key-here",
        "CLEANSTER_API_BASE_URL": "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public",
        "LOG_LEVEL": "error"
      }
    }
  }
}
```

Build the server first:
```bash
npm run build
```

### Option B — HTTP/SSE (remote or shared deployment)

```json
{
  "mcpServers": {
    "cleanster": {
      "url": "http://localhost:3100/sse",
      "headers": {
        "Authorization": "Bearer your-api-key-here"
      }
    }
  }
}
```

---

## Adding a New Tool

Each tool is self-contained in `src/tools/<tool_name>.ts`. Adding a new one takes three steps:

### 1. Create `src/tools/my_new_tool.ts`

```typescript
import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'my_new_tool';

export const description =
  'One-line description of what this tool does — the AI reads this.';

export const inputSchema = z.object({
  some_param: z.string().describe('Description of this parameter'),
  optional_param: z.number().optional().describe('An optional number'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.someMethod(params.some_param);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
```

### 2. Add the API method to `src/api/cleanster.ts`

```typescript
async someMethod(param: string): Promise<unknown> {
  const res = await this.http.get(`/v1/some-endpoint/${param}`);
  return res.data;
}
```

And add the endpoint constant to `src/api/endpoints.ts`:
```typescript
SOME_ENDPOINT: (id: string) => `/v1/some-endpoint/${id}`,
```

### 3. Register in `src/server.ts`

```typescript
import * as myNewTool from './tools/my_new_tool.js';

// Add to the TOOLS array:
const TOOLS = [
  // ...existing tools...
  myNewTool,
];
```

### 4. Write a test in `tests/my_new_tool.test.ts`

Follow the pattern in any existing test file — mock the API client, test the handler directly, no real HTTP calls.

---

## Architecture

```
Claude / AI client
       │
       │  MCP protocol (JSON-RPC 2.0)
       ▼
┌─────────────────────────────────────┐
│         Cleanster MCP Server        │
│                                     │
│  index.ts ──► StdioTransport        │
│           └──► SSETransport         │
│                  (Express)          │
│                                     │
│  src/server.ts                      │
│    McpServer + 11 tools registered  │
│                                     │
│  src/auth/                          │
│    Bearer token validation          │
│    [TODO: OAuth 2.0 + PKCE seam]    │
│                                     │
│  src/api/cleanster.ts               │
│    Axios wrapper (1 method/endpoint)│
└─────────────────────────────────────┘
       │
       │  HTTPS (Bearer token)
       ▼
Cleanster Partner REST API
```

### OAuth 2.0 seam

The `TODO` marker in `src/auth/token.ts` shows exactly where the OAuth token verifier will plug in. All tool handlers receive the `CleansterApiClient` (already constructed with the validated token) — no handler code changes are needed when OAuth is added.

---

## Rate Limiting

HTTP mode enforces **60 requests per minute per bearer token** using `express-rate-limit`. Responses include standard `RateLimit-*` headers. Exceeded requests receive HTTP 429.

Stdio mode has no rate limiting (tokens are provided via env, not per-request headers).

---

## Logging

[Pino](https://getpino.io) is used for structured JSON logging. Bearer tokens are redacted in all log output. In development (`LOG_PRETTY=true`), logs are pretty-printed via `pino-pretty`. In production, set `LOG_PRETTY=false` for JSON output compatible with log aggregators (Datadog, CloudWatch, etc.).

---

## Project Structure

```
mcp-server/
├── src/
│   ├── api/
│   │   ├── cleanster.ts       ← Cleanster REST API client (axios)
│   │   └── endpoints.ts       ← API endpoint constants (with TODO confirmations)
│   ├── auth/
│   │   ├── token.ts           ← Token validation + OAuth seam (TODO)
│   │   └── middleware.ts      ← Express auth middleware
│   ├── tools/                 ← One file per MCP tool
│   │   ├── list_bookings.ts
│   │   ├── get_booking.ts
│   │   ├── list_properties.ts
│   │   ├── get_property.ts
│   │   ├── list_cleaners.ts
│   │   ├── get_payout_records.ts
│   │   ├── create_booking.ts
│   │   ├── cancel_booking.ts
│   │   ├── reschedule_booking.ts
│   │   ├── assign_crew.ts
│   │   └── update_checklist.ts
│   ├── server.ts              ← McpServer bootstrap + tool registration
│   └── index.ts               ← Entry point, transport selection
├── tests/                     ← Vitest unit tests (mocked API, no real calls)
├── .env.example
├── package.json
├── tsconfig.json
└── README.md
```

---

## License

MIT
