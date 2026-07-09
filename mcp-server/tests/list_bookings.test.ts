import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/list_bookings.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_PAGE = {
  status: 200,
  message: 'Success',
  data: {
    number: 1,
    size: 25,
    numberOfElements: 2,
    totalPages: 1,
    totalElements: 2,
    content: [
      {
        id: 16459,
        status: 'UPCOMING',
        date: '2026-07-15',
        time: '09:00',
        hours: 3,
        cost: 120,
        propertyId: 42,
        cleanerId: 789,
        planId: 5,
        roomCount: 2,
        bathroomCount: 1,
        extraSupplies: false,
        paymentMethodId: 55,
      },
      {
        id: 16460,
        status: 'COMPLETED',
        date: '2026-06-01',
        time: '10:00',
        hours: 2.5,
        cost: 100,
        propertyId: 42,
        cleanerId: 790,
        planId: 5,
        roomCount: 2,
        bathroomCount: 1,
        extraSupplies: true,
        paymentMethodId: 55,
      },
    ],
  },
};

describe('list_bookings tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      listBookings: vi.fn().mockResolvedValue(MOCK_PAGE),
    };
  });

  it('calls api.listBookings with status and pageNo', async () => {
    const params = inputSchema.parse({ status: 'UPCOMING', pageNo: 2 });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.listBookings).toHaveBeenCalledWith({
      status: 'UPCOMING',
      pageNo: 2,
    });
  });

  it('defaults pageNo to 1 when not specified', async () => {
    const params = inputSchema.parse({ status: 'COMPLETED' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.listBookings).toHaveBeenCalledWith(
      expect.objectContaining({ pageNo: 1 }),
    );
  });

  it('returns content array with the raw paginated response', async () => {
    const params = inputSchema.parse({ status: 'UPCOMING' });
    const result = await handler(params, mockApi as CleansterApiClient);
    expect(result.content).toHaveLength(1);
    expect(result.content[0].type).toBe('text');
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.content).toHaveLength(2);
    expect(parsed.data.content[0].id).toBe(16459);
  });

  it('rejects invalid status values', () => {
    expect(() => inputSchema.parse({ status: 'scheduled' })).toThrow();
  });

  it('requires status to be provided', () => {
    expect(() => inputSchema.parse({})).toThrow();
  });

  it('rejects pageNo below 1', () => {
    expect(() => inputSchema.parse({ status: 'UPCOMING', pageNo: 0 })).toThrow();
  });
});
