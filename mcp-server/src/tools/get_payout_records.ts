import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'get_payout_records';

export const description =
  'Retrieve payout records for cleaners within a date range. Optionally filter by a specific cleaner.';

export const inputSchema = z.object({
  cleaner_id: z
    .string()
    .optional()
    .describe('Filter payout records for a specific cleaner ID. Omit to return all cleaners.'),
  date_from: z
    .string()
    .describe('Start of the date range, ISO format YYYY-MM-DD'),
  date_to: z
    .string()
    .describe('End of the date range, ISO format YYYY-MM-DD'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.getPayoutRecords(params);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
