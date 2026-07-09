import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'list_bookings';

export const description =
  'List cleaning bookings by status (COMPLETED, CANCELLED, or UPCOMING), paginated. Returns a Spring-style page object: { number, size, totalPages, totalElements, content: [...] }.';

export const inputSchema = z.object({
  status: z
    .enum(['COMPLETED', 'CANCELLED', 'UPCOMING'])
    .describe('Filter by booking status. The real API requires this - there is no "all statuses" option.'),
  pageNo: z
    .number()
    .int()
    .min(1)
    .default(1)
    .describe('Page number, 1-based. Defaults to 1.'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.listBookings(params);
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
