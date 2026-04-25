/**
 * MCP Server bootstrap.
 *
 * Builds a fully-configured McpServer instance with all 11 Cleanster tools
 * registered. The server is transport-agnostic — the caller (index.ts) wires
 * it to either StdioServerTransport or SSEServerTransport.
 */

import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { CleansterApiClient } from './api/cleanster.js';

import * as listBookings from './tools/list_bookings.js';
import * as getBooking from './tools/get_booking.js';
import * as listProperties from './tools/list_properties.js';
import * as getProperty from './tools/get_property.js';
import * as listCleaners from './tools/list_cleaners.js';
import * as getPayoutRecords from './tools/get_payout_records.js';
import * as createBooking from './tools/create_booking.js';
import * as cancelBooking from './tools/cancel_booking.js';
import * as rescheduleBooking from './tools/reschedule_booking.js';
import * as assignCrew from './tools/assign_crew.js';
import * as updateChecklist from './tools/update_checklist.js';

type ToolModule = {
  name: string;
  description: string;
  inputSchema: { shape: Record<string, import('zod').ZodTypeAny> };
  handler: (params: Record<string, unknown>, api: CleansterApiClient) => Promise<{
    content: Array<{ type: 'text'; text: string }>;
  }>;
};

const TOOLS: ToolModule[] = [
  listBookings,
  getBooking,
  listProperties,
  getProperty,
  listCleaners,
  getPayoutRecords,
  createBooking,
  cancelBooking,
  rescheduleBooking,
  assignCrew,
  updateChecklist,
] as unknown as ToolModule[];

/**
 * Build and return a new McpServer instance with a dedicated API client for
 * the supplied bearer token. Create one server per connection so each client
 * gets its own isolated auth context.
 */
export function buildMcpServer(token: string): McpServer {
  const api = new CleansterApiClient(token);
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
