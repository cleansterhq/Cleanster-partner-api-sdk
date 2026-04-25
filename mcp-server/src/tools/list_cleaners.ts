import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'list_cleaners';

export const description =
  'List available cleaners. Filter by region or check availability on a specific date.';

export const inputSchema = z.object({
  region: z
    .string()
    .optional()
    .describe('Filter cleaners by geographic region or city name'),
  available_on: z
    .string()
    .optional()
    .describe('Return only cleaners available on this date, ISO format YYYY-MM-DD'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.listCleaners(params);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
