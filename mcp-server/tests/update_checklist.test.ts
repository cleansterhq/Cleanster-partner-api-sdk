import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/update_checklist.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_CHECKLIST_RESPONSE = {
  status: 200,
  data: {
    booking_id: 'bk_001',
    cleaner_id: 'cl_789',
    items: [
      { task: 'Vacuum all rooms', required: true, completed: false },
      { task: 'Clean bathrooms', required: true, completed: false },
      { task: 'Wipe windows', required: false, completed: false },
    ],
  },
};

describe('update_checklist tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      updateChecklist: vi.fn().mockResolvedValue(MOCK_CHECKLIST_RESPONSE),
    };
  });

  it('calls api.updateChecklist with correct args', async () => {
    const params = inputSchema.parse({
      booking_id: 'bk_001',
      cleaner_id: 'cl_789',
      checklist_items: [
        { task: 'Vacuum all rooms', required: true },
        { task: 'Clean bathrooms', required: true },
        { task: 'Wipe windows', required: false },
      ],
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.updateChecklist).toHaveBeenCalledWith('bk_001', {
      cleaner_id: 'cl_789',
      checklist_items: [
        { task: 'Vacuum all rooms', required: true },
        { task: 'Clean bathrooms', required: true },
        { task: 'Wipe windows', required: false },
      ],
    });
  });

  it('returns checklist response in content', async () => {
    const params = inputSchema.parse({
      booking_id: 'bk_001',
      cleaner_id: 'cl_789',
      checklist_items: [{ task: 'Vacuum', required: true }],
    });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.items).toHaveLength(3);
  });

  it('rejects empty checklist_items array', () => {
    expect(() =>
      inputSchema.parse({
        booking_id: 'bk_001',
        cleaner_id: 'cl_789',
        checklist_items: [],
      }),
    ).toThrow();
  });

  it('requires booking_id, cleaner_id, and checklist_items', () => {
    expect(() => inputSchema.parse({})).toThrow();
    expect(() =>
      inputSchema.parse({ booking_id: 'bk_001', cleaner_id: 'cl_789' }),
    ).toThrow();
  });

  it('requires each checklist item to have task and required', () => {
    expect(() =>
      inputSchema.parse({
        booking_id: 'bk_001',
        cleaner_id: 'cl_789',
        checklist_items: [{ task: 'Vacuum' }],
      }),
    ).toThrow();
  });
});
