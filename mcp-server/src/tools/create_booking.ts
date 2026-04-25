import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'create_booking';

export const description =
  'Create a new cleaning booking for a property. Returns the created booking with its ID and scheduled time.';

export const inputSchema = z.object({
  property_id: z
    .string()
    .describe('ID of the property to be cleaned'),
  service_type: z
    .enum(['apartment', 'house', 'str', 'commercial', 'chores', 'handyman'])
    .describe('Type of cleaning service to perform'),
  scheduled_at: z
    .string()
    .describe('Start date and time for the booking, ISO 8601 format (e.g. 2025-07-15T09:00:00Z)'),
  notes: z
    .string()
    .optional()
    .describe('Optional instructions for the cleaner'),
  checklist_id: z
    .string()
    .optional()
    .describe('Optional ID of a saved checklist to attach to this booking'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.createBooking(params);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
