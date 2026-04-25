import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'assign_crew';

export const description =
  'Assign one or more cleaners to a booking. Replaces any previously assigned crew.';

export const inputSchema = z.object({
  booking_id: z
    .string()
    .describe('ID of the booking to assign cleaners to'),
  cleaner_ids: z
    .array(z.string())
    .min(1)
    .describe('Array of cleaner IDs to assign. Must contain at least one ID.'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.assignCrew(params.booking_id, {
    cleaner_ids: params.cleaner_ids,
  });
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
