import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'update_checklist';

export const description =
  'Update the checklist items for a booking. Each item has a task description and a required flag. Required items block booking completion if unchecked.';

export const inputSchema = z.object({
  booking_id: z
    .string()
    .describe('ID of the booking whose checklist should be updated'),
  cleaner_id: z
    .string()
    .describe('ID of the cleaner the checklist is assigned to'),
  checklist_items: z
    .array(
      z.object({
        task: z.string().describe('Description of the cleaning task'),
        required: z
          .boolean()
          .describe('If true, the cleaner must complete this task before closing the booking'),
      }),
    )
    .min(1)
    .describe('Array of checklist items to set for this booking'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.updateChecklist(params.booking_id, {
    cleaner_id: params.cleaner_id,
    checklist_items: params.checklist_items,
  });
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
