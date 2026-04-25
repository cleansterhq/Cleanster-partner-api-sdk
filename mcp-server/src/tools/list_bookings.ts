import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'list_bookings';

export const description =
  'List cleaning bookings. Filter by property, status, or date range. Returns up to 100 results.';

export const inputSchema = z.object({
  property_id: z.string().optional().describe('Filter bookings for a specific property ID'),
  status: z
    .enum(['scheduled', 'in_progress', 'completed', 'cancelled'])
    .optional()
    .describe('Filter by booking status'),
  date_from: z
    .string()
    .optional()
    .describe('Start of date range, ISO format YYYY-MM-DD'),
  date_to: z
    .string()
    .optional()
    .describe('End of date range, ISO format YYYY-MM-DD'),
  limit: z
    .number()
    .int()
    .min(1)
    .max(100)
    .default(20)
    .describe('Maximum number of results to return (1-100, default 20)'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.listBookings(params);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
