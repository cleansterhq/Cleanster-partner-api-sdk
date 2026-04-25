import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/create_booking.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_CREATED_BOOKING = {
  id: 'bk_new_001',
  status: 'scheduled',
  property_id: 'prop_42',
  service_type: 'apartment',
  scheduled_at: '2025-09-01T09:00:00Z',
  notes: 'Focus on the kitchen and bathrooms.',
  created_at: '2025-07-25T14:00:00Z',
};

describe('create_booking tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      createBooking: vi.fn().mockResolvedValue({ status: 201, data: MOCK_CREATED_BOOKING }),
    };
  });

  it('calls api.createBooking with all provided params', async () => {
    const params = inputSchema.parse({
      property_id: 'prop_42',
      service_type: 'apartment',
      scheduled_at: '2025-09-01T09:00:00Z',
      notes: 'Focus on the kitchen and bathrooms.',
      checklist_id: 'cl_105',
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.createBooking).toHaveBeenCalledWith({
      property_id: 'prop_42',
      service_type: 'apartment',
      scheduled_at: '2025-09-01T09:00:00Z',
      notes: 'Focus on the kitchen and bathrooms.',
      checklist_id: 'cl_105',
    });
  });

  it('works without optional fields', async () => {
    const params = inputSchema.parse({
      property_id: 'prop_42',
      service_type: 'house',
      scheduled_at: '2025-09-01T09:00:00Z',
    });
    const result = await handler(params, mockApi as CleansterApiClient);
    expect(result.content[0].type).toBe('text');
  });

  it('returns created booking in content', async () => {
    const params = inputSchema.parse({
      property_id: 'prop_42',
      service_type: 'apartment',
      scheduled_at: '2025-09-01T09:00:00Z',
    });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.id).toBe('bk_new_001');
    expect(parsed.data.status).toBe('scheduled');
  });

  it('rejects invalid service_type', () => {
    expect(() =>
      inputSchema.parse({
        property_id: 'prop_42',
        service_type: 'office',
        scheduled_at: '2025-09-01T09:00:00Z',
      }),
    ).toThrow();
  });

  it('requires property_id', () => {
    expect(() =>
      inputSchema.parse({ service_type: 'apartment', scheduled_at: '2025-09-01T09:00:00Z' }),
    ).toThrow();
  });

  it('requires scheduled_at', () => {
    expect(() =>
      inputSchema.parse({ property_id: 'prop_42', service_type: 'apartment' }),
    ).toThrow();
  });

  it('accepts all valid service_type values', () => {
    const valid = ['apartment', 'house', 'str', 'commercial', 'chores', 'handyman'];
    valid.forEach((s) =>
      expect(() =>
        inputSchema.parse({ property_id: 'x', service_type: s, scheduled_at: '2025-09-01T09:00:00Z' }),
      ).not.toThrow(),
    );
  });
});
