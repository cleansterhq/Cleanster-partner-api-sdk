import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/assign_crew.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_BOOKING = {
  id: 'bk_001',
  status: 'scheduled',
  cleaner: { id: 'cl_789', name: 'Maria S.' },
};

describe('assign_crew tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      assignCrew: vi.fn().mockResolvedValue({ status: 200, data: MOCK_BOOKING }),
    };
  });

  it('calls api.assignCrew with booking_id and cleaner_id', async () => {
    const params = inputSchema.parse({
      booking_id: 'bk_001',
      cleaner_id: 'cl_789',
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.assignCrew).toHaveBeenCalledWith('bk_001', {
      cleaner_id: 'cl_789',
    });
  });

  it('returns updated booking in content', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001', cleaner_id: 'cl_789' });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.cleaner.name).toBe('Maria S.');
  });

  it('requires booking_id', () => {
    expect(() => inputSchema.parse({ cleaner_id: 'cl_789' })).toThrow();
  });

  it('requires cleaner_id', () => {
    expect(() => inputSchema.parse({ booking_id: 'bk_001' })).toThrow();
  });
});
