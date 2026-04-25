/**
 * Cleanster REST API endpoint constants.
 *
 * All URLs are relative to CLEANSTER_API_BASE_URL.
 *
 * TODO: Confirm each path against the live Cleanster Partner API documentation
 *       at https://documenter.getpostman.com/view/26172658/2sAYdoF7ep before
 *       deploying to production. These are best-effort based on the SDK source.
 */

export const ENDPOINTS = {
  // ── Bookings ──────────────────────────────────────────────────────────────
  /** GET  → list bookings (query: property_id, status, date_from, date_to, limit) */
  BOOKINGS_LIST: '/v1/bookings',

  /** GET  → get a single booking by ID */
  BOOKING_GET: (id: string) => `/v1/bookings/${id}`,

  /** POST → create a new booking */
  BOOKING_CREATE: '/v1/bookings',

  /** POST → cancel a booking */
  BOOKING_CANCEL: (id: string) => `/v1/bookings/${id}/cancel`,

  /** POST → reschedule a booking */
  BOOKING_RESCHEDULE: (id: string) => `/v1/bookings/${id}/reschedule`,

  /** POST → assign cleaner(s) to a booking */
  // TODO: Verify — may be POST /v1/bookings/{id}/cleaner-assignment
  BOOKING_ASSIGN_CREW: (id: string) => `/v1/bookings/${id}/cleaner-assignment`,

  /** PUT  → update checklist items for a booking */
  // TODO: Verify path — may require cleaner_id as query or path param
  BOOKING_CHECKLIST: (id: string) => `/v1/bookings/${id}/checklist`,

  // ── Properties ────────────────────────────────────────────────────────────
  /** GET  → list properties (query: account_id, property_type) */
  PROPERTIES_LIST: '/v1/properties',

  /** GET  → get a single property by ID */
  PROPERTY_GET: (id: string) => `/v1/properties/${id}`,

  // ── Cleaners ──────────────────────────────────────────────────────────────
  /** GET  → list cleaners (query: region, available_on) */
  CLEANERS_LIST: '/v1/cleaners',

  // ── Payouts ───────────────────────────────────────────────────────────────
  // TODO: Confirm payout endpoint — not yet documented in public Postman collection
  /** GET  → get payout records (query: cleaner_id, date_from, date_to) */
  PAYOUTS_LIST: '/v1/payouts',
} as const;
