import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'cancel_booking';

export const description =
  'Cancel an existing booking. Provide an optional reason that will be recorded against the booking.';

export const inputSchema = z.object({
  booking_id: z
    .string()
    .describe('ID of the booking to cancel'),
  reason: z
    .string()
    .optional()
    .describe('Optional reason for cancellation, visible to the assigned cleaner'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.cancelBooking(params.booking_id, params.reason);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
