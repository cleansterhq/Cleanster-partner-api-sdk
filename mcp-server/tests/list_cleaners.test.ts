import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/list_cleaners.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_CLEANERS = [
  {
    id: 'cl_789',
    name: 'Maria S.',
    region: 'Atlanta',
    rating: 4.9,
    total_bookings: 142,
    available: true,
  },
  {
    id: 'cl_790',
    name: 'James T.',
    region: 'Atlanta',
    rating: 4.7,
    total_bookings: 88,
    available: true,
  },
];

describe('list_cleaners tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      listCleaners: vi.fn().mockResolvedValue({ status: 200, data: MOCK_CLEANERS }),
    };
  });

  it('calls api.listCleaners with no filters when none provided', async () => {
    const params = inputSchema.parse({});
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.listCleaners).toHaveBeenCalledWith({});
  });

  it('passes status filter to api', async () => {
    const params = inputSchema.parse({ status: 'active' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.listCleaners).toHaveBeenCalledWith({ status: 'active' });
  });

  it('passes search filter to api', async () => {
    const params = inputSchema.parse({ search: 'Maria' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.listCleaners).toHaveBeenCalledWith({ search: 'Maria' });
  });

  it('rejects invalid status values', () => {
    expect(() => inputSchema.parse({ status: 'unknown' })).toThrow();
  });

  it('returns serialised cleaner list', async () => {
    const params = inputSchema.parse({ status: 'active' });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data[0].name).toBe('Maria S.');
  });
});
