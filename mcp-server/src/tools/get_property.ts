import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'get_property';

export const description =
  'Get full details for a single property by its ID, including address, type, assigned cleaners, and iCal links.';

export const inputSchema = z.object({
  property_id: z.string().describe('The unique ID of the property to retrieve'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.getProperty(params.property_id);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
