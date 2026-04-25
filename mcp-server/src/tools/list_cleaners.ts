import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'list_cleaners';

export const description =
  'List cleaners on the Cleanster platform. Optionally filter by status (active/inactive/pending) or search by name or email.';

export const inputSchema = z.object({
  status: z
    .enum(['active', 'inactive', 'pending'])
    .optional()
    .describe("Filter by cleaner status: 'active', 'inactive', or 'pending'"),
  search: z
    .string()
    .optional()
    .describe('Partial match against cleaner name or email address'),
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
