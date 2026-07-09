import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'update_task';

export const description =
  'Update task quantities for a booking (e.g. number of bedrooms/bathrooms to clean, extra add-ons).';

export const inputSchema = z.object({
  booking_id: z.string().describe('ID of the booking to update tasks for'),
  tasks: z
    .array(
      z.object({
        id: z.string().describe('ID of the task to update'),
        quantity: z.number().int().describe('New quantity for this task'),
      }),
    )
    .describe('List of task quantity updates to apply'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.updateTask(params.booking_id, { tasks: params.tasks });
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
