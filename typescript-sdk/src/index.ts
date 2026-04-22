/**
 * Cleanster TypeScript SDK
 * Official client library for the Cleanster Partner API.
 *
 * @example
 * ```typescript
 * import { CleansterClient } from "cleanster";
 *
 * const client = CleansterClient.sandbox("your-access-key");
 * const { data: user } = await client.users.createUser({
 *   email: "jane@example.com",
 *   firstName: "Jane",
 *   lastName: "Smith",
 * });
 * const { data: tokenUser } = await client.users.fetchAccessToken(user.id);
 * client.setAccessToken(tokenUser.token!);
 *
 * const { data: booking } = await client.bookings.createBooking({
 *   date: "2025-06-15", time: "10:00", propertyId: 1004,
 *   roomCount: 2, bathroomCount: 1, planId: 5,
 *   hours: 3, extraSupplies: false, paymentMethodId: 10,
 * });
 * console.log(booking.id);
 * ```
 *
 * @packageDocumentation
 */

export { CleansterClient } from "./client";
export { CleansterConfig, CleansterConfigBuilder, SANDBOX_BASE_URL, PRODUCTION_BASE_URL } from "./config";
export { CleansterException, CleansterAuthException, CleansterApiException } from "./exceptions";

// API classes
export { BookingsApi } from "./api/bookings";
export { UsersApi } from "./api/users";
export { PropertiesApi } from "./api/properties";
export { ChecklistsApi } from "./api/checklists";
export { OtherApi } from "./api/other";
export { BlacklistApi } from "./api/blacklist";
export { PaymentMethodsApi } from "./api/payment-methods";
export { WebhooksApi } from "./api/webhooks";

// Models & request types
export * from "./models";
export type { CostEstimateRequest, AvailableCleanersRequest } from "./api/other";
export type { BlacklistRequest } from "./api/blacklist";
export type { WebhookRequest } from "./api/webhooks";
