import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'list_services';

export const description =
  'List all cleaning service types available on the partner account (e.g. Residential, Airbnb, Office). ' +
  'Service types are account-specific, not a fixed set - use this to discover valid service IDs before ' +
  'looking up plans or subcategories. No authentication required.';

export const inputSchema = z.object({});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  _params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.getServices();
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
