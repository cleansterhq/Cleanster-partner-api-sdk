import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/cancel_booking.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

describe('cancel_booking tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      cancelBooking: vi.fn().mockResolvedValue({ status: 200, message: 'Booking cancelled.' }),
    };
  });

  it('calls api.cancelBooking with booking_id and reason', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001', reason: 'Guest checked out early' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.cancelBooking).toHaveBeenCalledWith('bk_001', 'Guest checked out early');
  });

  it('calls api.cancelBooking without reason when omitted', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.cancelBooking).toHaveBeenCalledWith('bk_001', undefined);
  });

  it('returns response in content', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001' });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.message).toBe('Booking cancelled.');
  });

  it('requires booking_id', () => {
    expect(() => inputSchema.parse({})).toThrow();
  });
});
