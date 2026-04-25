import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/update_checklist.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_CHECKLIST_RESPONSE = {
  status: 200,
  data: {
    booking_id: 'bk_001',
    checklist_id: 'ch_42',
    name: 'Deep Clean',
    items: ['Vacuum all rooms', 'Clean bathrooms', 'Wipe windows'],
  },
};

describe('assign_checklist tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      assignChecklist: vi.fn().mockResolvedValue(MOCK_CHECKLIST_RESPONSE),
    };
  });

  it('calls api.assignChecklist with booking_id and checklist_id', async () => {
    const params = inputSchema.parse({
      booking_id: 'bk_001',
      checklist_id: 'ch_42',
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.assignChecklist).toHaveBeenCalledWith('bk_001', 'ch_42');
  });

  it('returns checklist response in content', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001', checklist_id: 'ch_42' });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.name).toBe('Deep Clean');
  });

  it('requires booking_id', () => {
    expect(() => inputSchema.parse({ checklist_id: 'ch_42' })).toThrow();
  });

  it('requires checklist_id', () => {
    expect(() => inputSchema.parse({ booking_id: 'bk_001' })).toThrow();
  });
});
