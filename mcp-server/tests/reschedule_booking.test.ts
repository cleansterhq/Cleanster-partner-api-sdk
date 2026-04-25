import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/reschedule_booking.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_RESCHEDULED = {
  id: 'bk_001',
  status: 'scheduled',
  scheduled_at: '2025-08-01T10:00:00Z',
  property_id: 'prop_42',
};

describe('reschedule_booking tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      rescheduleBooking: vi.fn().mockResolvedValue({ status: 200, data: MOCK_RESCHEDULED }),
    };
  });

  it('calls api.rescheduleBooking with correct args', async () => {
    const params = inputSchema.parse({
      booking_id: 'bk_001',
      new_scheduled_at: '2025-08-01T10:00:00Z',
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.rescheduleBooking).toHaveBeenCalledWith('bk_001', {
      new_scheduled_at: '2025-08-01T10:00:00Z',
    });
  });

  it('returns updated booking in content', async () => {
    const params = inputSchema.parse({
      booking_id: 'bk_001',
      new_scheduled_at: '2025-08-01T10:00:00Z',
    });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.scheduled_at).toBe('2025-08-01T10:00:00Z');
  });

  it('requires both booking_id and new_scheduled_at', () => {
    expect(() => inputSchema.parse({ booking_id: 'bk_001' })).toThrow();
    expect(() => inputSchema.parse({ new_scheduled_at: '2025-08-01T10:00:00Z' })).toThrow();
  });
});
