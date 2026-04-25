import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/list_bookings.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_BOOKINGS = [
  {
    id: 'bk_001',
    status: 'scheduled',
    property_id: 'prop_42',
    scheduled_at: '2025-07-15T09:00:00Z',
    duration_hours: 3,
    service_type: 'apartment',
    cleaner: { id: 'cl_789', name: 'Maria S.' },
  },
  {
    id: 'bk_002',
    status: 'completed',
    property_id: 'prop_42',
    scheduled_at: '2025-06-01T10:00:00Z',
    duration_hours: 2.5,
    service_type: 'apartment',
    cleaner: { id: 'cl_790', name: 'James T.' },
  },
];

describe('list_bookings tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      listBookings: vi.fn().mockResolvedValue({ status: 200, data: MOCK_BOOKINGS }),
    };
  });

  it('calls api.listBookings with all provided params', async () => {
    const params = inputSchema.parse({
      property_id: 'prop_42',
      status: 'scheduled',
      date_from: '2025-07-01',
      date_to: '2025-07-31',
      limit: 10,
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.listBookings).toHaveBeenCalledWith({
      property_id: 'prop_42',
      status: 'scheduled',
      date_from: '2025-07-01',
      date_to: '2025-07-31',
      limit: 10,
    });
  });

  it('uses default limit of 20 when not specified', async () => {
    const params = inputSchema.parse({});
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.listBookings).toHaveBeenCalledWith(
      expect.objectContaining({ limit: 20 }),
    );
  });

  it('returns content array with JSON-serialised bookings', async () => {
    const params = inputSchema.parse({ property_id: 'prop_42' });
    const result = await handler(params, mockApi as CleansterApiClient);
    expect(result.content).toHaveLength(1);
    expect(result.content[0].type).toBe('text');
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data).toHaveLength(2);
    expect(parsed.data[0].id).toBe('bk_001');
  });

  it('rejects invalid status values', () => {
    expect(() =>
      inputSchema.parse({ status: 'unknown_status' }),
    ).toThrow();
  });

  it('rejects limit above 100', () => {
    expect(() => inputSchema.parse({ limit: 101 })).toThrow();
  });

  it('rejects limit below 1', () => {
    expect(() => inputSchema.parse({ limit: 0 })).toThrow();
  });
});
