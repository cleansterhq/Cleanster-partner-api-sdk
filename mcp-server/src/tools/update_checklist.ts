import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'assign_checklist';

export const description =
  'Assign an existing saved checklist to a booking. The cleaner will follow this checklist during the clean.';

export const inputSchema = z.object({
  booking_id: z
    .string()
    .describe('ID of the booking to assign the checklist to'),
  checklist_id: z
    .string()
    .describe('ID of the saved checklist to assign to this booking'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.assignChecklist(params.booking_id, {
    checklist_id: params.checklist_id,
  });
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
