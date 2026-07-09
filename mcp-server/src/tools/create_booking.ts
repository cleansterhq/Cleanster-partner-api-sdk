import { z } from 'zod';
import type { CleansterApiClient } from '../api/cleanster.js';

export const name = 'create_booking';

export const description =
  'Create a new cleaning booking for a property. Requires a plan ID (from list_services / GET /v1/plans) ' +
  'rather than a fixed service-type string, since available service types and plans are account-specific. ' +
  'Returns the created booking with its ID and scheduled time.';

export const inputSchema = z.object({
  date: z.string().describe('Service date in YYYY-MM-DD format'),
  time: z.string().describe('Start time in HH:mm 24-hour format'),
  property_id: z.number().int().describe('ID of the property to be cleaned'),
  plan_id: z
    .number()
    .int()
    .describe('Cleaning plan ID for this booking (from GET /v1/plans)'),
  hours: z.number().describe('Duration of the booking in hours (from GET /v1/recommended-hours)'),
  room_count: z.number().int().describe('Number of rooms'),
  bathroom_count: z.number().int().describe('Number of bathrooms'),
  extra_supplies: z.boolean().describe('Whether to add cleaning supplies'),
  payment_method_id: z.number().int().describe('Saved payment method ID to charge'),
  coupon_code: z.string().optional().describe('Optional discount coupon code'),
  extras: z
    .array(z.number().int())
    .optional()
    .describe('Optional array of extra service IDs (from GET /v1/cleaning-extras)'),
});

export type Input = z.infer<typeof inputSchema>;

export async function handler(
  params: Input,
  api: CleansterApiClient,
): Promise<{ content: Array<{ type: 'text'; text: string }> }> {
  const data = await api.createBooking({
    date: params.date,
    time: params.time,
    propertyId: params.property_id,
    planId: params.plan_id,
    hours: params.hours,
    roomCount: params.room_count,
    bathroomCount: params.bathroom_count,
    extraSupplies: params.extra_supplies,
    paymentMethodId: params.payment_method_id,
    ...(params.coupon_code !== undefined ? { couponCode: params.coupon_code } : {}),
    ...(params.extras !== undefined ? { extras: params.extras } : {}),
  });
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}
