import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'get_booking';

export const description =
  'Get full details for a single booking by its ID, including status, assigned cleaner, property, and schedule.';

export const inputSchema = z.object({
  booking_id: z.string().describe('The unique ID of the booking to retrieve'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.getBooking(params.booking_id);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
