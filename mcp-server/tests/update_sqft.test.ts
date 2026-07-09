import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/update_sqft.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_RESPONSE = {
  status: 200,
  data: {
    booking_id: 'bk_001',
    totalSqFt: 1500,
  },
};

describe('update_sqft tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      updateSqft: vi.fn().mockResolvedValue(MOCK_RESPONSE),
    };
  });

  it('calls api.updateSqft with booking_id and total_sq_ft', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001', total_sq_ft: 1500 });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.updateSqft).toHaveBeenCalledWith('bk_001', 1500);
  });

  it('returns response in content', async () => {
    const params = inputSchema.parse({ booking_id: 'bk_001', total_sq_ft: 1500 });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.totalSqFt).toBe(1500);
  });

  it('requires booking_id', () => {
    expect(() => inputSchema.parse({ total_sq_ft: 1500 })).toThrow();
  });

  it('requires total_sq_ft', () => {
    expect(() => inputSchema.parse({ booking_id: 'bk_001' })).toThrow();
  });
});
