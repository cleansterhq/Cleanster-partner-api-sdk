import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'list_properties';

export const description =
  'List cleaning properties registered on the account. Filter by property type.';

export const inputSchema = z.object({
  account_id: z
    .string()
    .optional()
    .describe('Filter properties belonging to a specific account ID'),
  property_type: z
    .enum(['apartment', 'house', 'str', 'commercial'])
    .optional()
    .describe('Filter by property type: apartment, house, str (short-term rental), or commercial'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.listProperties(params);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
