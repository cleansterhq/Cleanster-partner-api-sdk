import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/get_booking.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_BOOKING = {
  id: 'bk_001',
  status: 'scheduled',
  property_id: 'prop_42',
  scheduled_at: '2025-07-15T09:00:00Z',
  duration_hours: 3,
  service_type: 'apartment',
  notes: 'Focus on the kitchen.',
  cleaner: { id: 'cl_789', name: 'Maria S.', phone: '+1 555 0101' },
  property: {
    id: 'prop_42',
    address: '123 Main St',
    city: 'Atlanta',
    state: 'GA',
  },
};

describe('get_booking tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      getBooking: vi.fn().mockResolvedValue({ status: 200, data: MOCK_BOOKING }),
    };
  });

  it('calls api.getBooking with the correct booking ID', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.getBooking).toHaveBeenCalledWith('bk_001');
  });

  it('returns content with the full booking object', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001' });
    const result = await handler(params, mockApi as CleansterApiClient);
    expect(result.content[0].type).toBe('text');
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.id).toBe('bk_001');
    expect(parsed.data.cleaner.name).toBe('Maria S.');
  });

  it('requires booking_id', () => {
    expect(() => inputSchema.parse({})).toThrow();
  });

  it('propagates API errors', async () => {
    mockApi.getBooking = vi.fn().mockRejectedValue(new Error('404 Not Found'));
    const params = inputSchema.parse({ booking_id: 'bk_999' });
    await expect(handler(params, mockApi as CleansterApiClient)).rejects.toThrow('404 Not Found');
  });
});
