import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'reschedule_booking';

export const description =
  'Move an existing booking to a new date and time. The assigned cleaner is notified automatically.';

export const inputSchema = z.object({
  booking_id: z
    .string()
    .describe('ID of the booking to reschedule'),
  new_scheduled_at: z
    .string()
    .describe('New start date and time, ISO 8601 format (e.g. 2025-08-01T10:00:00Z)'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.rescheduleBooking(params.booking_id, {
    new_scheduled_at: params.new_scheduled_at,
  });
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
