import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/assign_crew.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_BOOKING = {
  id: 'bk_001',
  status: 'scheduled',
  cleaners: [
    { id: 'cl_789', name: 'Maria S.' },
    { id: 'cl_790', name: 'James T.' },
  ],
};

describe('assign_crew tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      assignCrew: vi.fn().mockResolvedValue({ status: 200, data: MOCK_BOOKING }),
    };
  });

  it('calls api.assignCrew with booking_id and cleaner_ids', async () => {
    const params = inputSchema.parse({
      booking_id: 'bk_001',
      cleaner_ids: ['cl_789', 'cl_790'],
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.assignCrew).toHaveBeenCalledWith('bk_001', {
      cleaner_ids: ['cl_789', 'cl_790'],
    });
  });

  it('works with a single cleaner', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001', cleaner_ids: ['cl_789'] });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.assignCrew).toHaveBeenCalledWith('bk_001', { cleaner_ids: ['cl_789'] });
  });

  it('returns updated booking in content', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001', cleaner_ids: ['cl_789'] });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.cleaners).toHaveLength(2);
  });

  it('rejects empty cleaner_ids array', () => {
    expect(() =>
      inputSchema.parse({ booking_id: 'bk_001', cleaner_ids: [] }),
    ).toThrow();
  });

  it('requires booking_id', () => {
    expect(() => inputSchema.parse({ cleaner_ids: ['cl_789'] })).toThrow();
  });
});
