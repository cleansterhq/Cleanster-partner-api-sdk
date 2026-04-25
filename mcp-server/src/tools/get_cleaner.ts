import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'get_cleaner';

export const description =
  'Get full profile details for a single cleaner by their ID.';

export const inputSchema = z.object({
  cleaner_id: z
    .string()
    .describe('ID of the cleaner to retrieve'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.getCleaner(params.cleaner_id);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
