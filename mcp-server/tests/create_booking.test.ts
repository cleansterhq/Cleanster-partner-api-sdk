import { describe, it, expect, vi, beforeEach } from 'vitest';
import { handler, inputSchema } from '../src/tools/create_booking.js';
import type { CleansterApiClient } from '../src/api/cleanster.js';

const MOCK_CREATED_BOOKING = {
  id: 16459,
  status: 'OPEN',
  propertyId: 1004,
  planId: 2,
  date: '2025-09-15',
  time: '09:00',
  createdAt: '2025-07-25T14:00:00Z',
};

describe('create_booking tool', () => {
  let mockApi: Partial<CleansterApiClient>;

  beforeEach(() => {
    mockApi = {
      createBooking: vi.fn().mockResolvedValue({ status: 201, data: MOCK_CREATED_BOOKING }),
    };
  });

  it('calls api.createBooking with all provided params mapped to the real request shape', async () => {
    const params = inputSchema.parse({
      date: '2025-09-15',
      time: '09:00',
      property_id: 1004,
      plan_id: 2,
      hours: 3,
      room_count: 2,
      bathroom_count: 1,
      extra_supplies: false,
      payment_method_id: 55,
      coupon_code: '20POFF',
      extras: [3, 7],
    });
    await handler(params, mockApi as CleansterApiClient);
    expect(mockApi.createBooking).toHaveBeenCalledWith({
      date: '2025-09-15',
      time: '09:00',
      propertyId: 1004,
      planId: 2,
      hours: 3,
      roomCount: 2,
      bathroomCount: 1,
      extraSupplies: false,
      paymentMethodId: 55,
      couponCode: '20POFF',
      extras: [3, 7],
    });
  });

  it('works without optional fields', async () => {
    const params = inputSchema.parse({
      date: '2025-09-15',
      time: '09:00',
      property_id: 1004,
      plan_id: 2,
      hours: 3,
      room_count: 2,
      bathroom_count: 1,
      extra_supplies: false,
      payment_method_id: 55,
    });
    const result = await handler(params, mockApi as CleansterApiClient);
    expect(result.content[0].type).toBe('text');
    expect(mockApi.createBooking).toHaveBeenCalledWith({
      date: '2025-09-15',
      time: '09:00',
      propertyId: 1004,
      planId: 2,
      hours: 3,
      roomCount: 2,
      bathroomCount: 1,
      extraSupplies: false,
      paymentMethodId: 55,
    });
  });

  it('returns created booking in content', async () => {
    const params = inputSchema.parse({
      date: '2025-09-15',
      time: '09:00',
      property_id: 1004,
      plan_id: 2,
      hours: 3,
      room_count: 2,
      bathroom_count: 1,
      extra_supplies: false,
      payment_method_id: 55,
    });
    const result = await handler(params, mockApi as CleansterApiClient);
    const parsed = JSON.parse(result.content[0].text);
    expect(parsed.data.id).toBe(16459);
    expect(parsed.data.status).toBe('OPEN');
  });

  it('requires plan_id', () => {
    expect(() =>
      inputSchema.parse({
        date: '2025-09-15',
        time: '09:00',
        property_id: 1004,
        hours: 3,
        room_count: 2,
        bathroom_count: 1,
        extra_supplies: false,
        payment_method_id: 55,
      }),
    ).toThrow();
  });

  it('requires property_id', () => {
    expect(() =>
      inputSchema.parse({
        date: '2025-09-15',
        time: '09:00',
        plan_id: 2,
        hours: 3,
        room_count: 2,
        bathroom_count: 1,
        extra_supplies: false,
        payment_method_id: 55,
      }),
    ).toThrow();
  });

  it('requires payment_method_id', () => {
    expect(() =>
      inputSchema.parse({
        date: '2025-09-15',
        time: '09:00',
        property_id: 1004,
        plan_id: 2,
        hours: 3,
        room_count: 2,
        bathroom_count: 1,
        extra_supplies: false,
      }),
    ).toThrow();
  });

  it('rejects a non-numeric property_id', () => {
    expect(() =>
      inputSchema.parse({
        date: '2025-09-15',
        time: '09:00',
        property_id: 'prop_42',
        plan_id: 2,
        hours: 3,
        room_count: 2,
        bathroom_count: 1,
        extra_supplies: false,
        payment_method_id: 55,
      }),
    ).toThrow();
  });
});
