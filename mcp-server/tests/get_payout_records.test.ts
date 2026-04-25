import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/get_payout_records.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_PAYOUTS = [
  {
    id: 'pay_001',
    cleaner_id: 'cl_789',
    cleaner_name: 'Maria S.',
    amount: 125.0,
    currency: 'USD',
    booking_id: 'bk_001',
    paid_at: '2025-07-16T12:00:00Z',
  },
  {
    id: 'pay_002',
    cleaner_id: 'cl_789',
    cleaner_name: 'Maria S.',
    amount: 95.0,
    currency: 'USD',
    booking_id: 'bk_002',
    paid_at: '2025-07-20T12:00:00Z',
  },
];

describe('get_payout_records tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      getPayoutRecords: vi.fn().mockResolvedValue({ status: 200, data: MOCK_PAYOUTS }),
    };
  });

  it('calls api.getPayoutRecords with all params', async () => {
    const params = inputSchema.parse({
      cleaner_id: 'cl_789',
      date_from: '2025-07-01',
      date_to: '2025-07-31',
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.getPayoutRecords).toHaveBeenCalledWith({
      cleaner_id: 'cl_789',
      date_from: '2025-07-01',
      date_to: '2025-07-31',
    });
  });

  it('works without optional cleaner_id', async () => {
    const params = inputSchema.parse({ date_from: '2025-07-01', date_to: '2025-07-31' });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.getPayoutRecords).toHaveBeenCalledWith(
      expect.not.objectContaining({ cleaner_id: expect.anything() }),
    );
  });

  it('returns payout records in content', async () => {
    const params = inputSchema.parse({ date_from: '2025-07-01', date_to: '2025-07-31' });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data).toHaveLength(2);
    expect(parsed.data[0].amount).toBe(125.0);
  });

  it('requires date_from', () => {
    expect(() => inputSchema.parse({ date_to: '2025-07-31' })).toThrow();
  });

  it('requires date_to', () => {
    expect(() => inputSchema.parse({ date_from: '2025-07-01' })).toThrow();
  });
});
