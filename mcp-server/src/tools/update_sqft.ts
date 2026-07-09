import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'update_sqft';

export const description =
  'Update the total square footage recorded for a booking.';

export const inputSchema = z.object({
  booking_id: z.string().describe('ID of the booking to update'),
  total_sq_ft: z.number().describe('New total square footage for the property being cleaned'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.updateSqft(params.booking_id, params.total_sq_ft);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
