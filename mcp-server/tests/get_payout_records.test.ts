import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/get_cleaner.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_CLEANER = {
  id: 'cl_789',
  name: 'Maria S.',
  email: 'maria@example.com',
  status: 'active',
  rating: 4.9,
  total_bookings: 142,
};

describe('get_cleaner tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      getCleaner: vi.fn().mockResolvedValue({ status: 200, data: MOCK_CLEANER }),
    };
  });

  it('calls api.getCleaner with the cleaner id', async () => {
    const params = inputSchema.parse({ cleaner_id: 'cl_789' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.getCleaner).toHaveBeenCalledWith('cl_789');
  });

  it('returns cleaner data in content', async () => {
    const params = inputSchema.parse({ cleaner_id: 'cl_789' });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.name).toBe('Maria S.');
    expect(parsed.data.rating).toBe(4.9);
  });

  it('requires cleaner_id', () => {
    expect(() => inputSchema.parse({})).toThrow();
  });
});
