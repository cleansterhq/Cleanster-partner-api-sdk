import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/update_task.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_RESPONSE = {
  status: 200,
  data: {
    booking_id: 'bk_001',
    tasks: [{ id: 'task_1', quantity: 3 }],
  },
};

describe('update_task tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      updateTask: vi.fn().mockResolvedValue(MOCK_RESPONSE),
    };
  });

  it('calls api.updateTask with booking_id and tasks', async () => {
    const params = inputSchema.parse({
      booking_id: 'bk_001',
      tasks: [{ id: 'task_1', quantity: 3 }],
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.updateTask).toHaveBeenCalledWith('bk_001', {
      tasks: [{ id: 'task_1', quantity: 3 }],
    });
  });

  it('returns response in content', async () => {
    const params = inputSchema.parse({
      booking_id: 'bk_001',
      tasks: [{ id: 'task_1', quantity: 3 }],
    });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.booking_id).toBe('bk_001');
  });

  it('requires booking_id', () => {
    expect(() => inputSchema.parse({ tasks: [{ id: 'task_1', quantity: 3 }] })).toThrow();
  });

  it('requires tasks', () => {
    expect(() => inputSchema.parse({ booking_id: 'bk_001' })).toThrow();
  });
});
