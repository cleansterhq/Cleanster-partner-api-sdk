/**
 * Cleanster REST API endpoint constants.
 *
 * All URLs are relative to CLEANSTER_API_BASE_URL.
 * Verified against the Cleanster Partner API (Postman collection 2sAYdoF7ep).
 */

export const ENDPOINTS = {
  // ── Bookings ──────────────────────────────────────────────────────────────
  /** GET  → list bookings (query: property_id, status, date_from, date_to, limit) */
  BOOKINGS_LIST: '/v1/bookings',

  /** GET  → get a single booking by ID */
  BOOKING_GET: (id: string) => `/v1/bookings/${id}`,

  /** POST → create a new booking */
  BOOKING_CREATE: '/v1/bookings/create',

  /** POST → cancel a booking */
  BOOKING_CANCEL: (id: string) => `/v1/bookings/${id}/cancel`,

  /** POST → reschedule a booking to a new date/time */
  BOOKING_RESCHEDULE: (id: string) => `/v1/bookings/${id}/reschedule`,

  /** POST → assign a single cleaner to a booking (body: { cleaner_id }) */
  BOOKING_ASSIGN_CREW: (id: string) => `/v1/bookings/${id}/cleaner`,

  /** PUT  → assign an existing checklist to a booking (checklistId in URL path) */
  BOOKING_CHECKLIST: (bookingId: string, checklistId: string) => `/v1/bookings/${bookingId}/checklist/${checklistId}`,

  // ── Properties ────────────────────────────────────────────────────────────
  /** GET  → list properties (query: account_id, property_type) */
  PROPERTIES_LIST: '/v1/properties',

  /** GET  → get a single property by ID */
  PROPERTY_GET: (id: string) => `/v1/properties/${id}`,

  // ── Cleaners ──────────────────────────────────────────────────────────────
  /** GET  → list cleaners (query: status, search) */
  CLEANERS_LIST: '/v1/cleaners',

  /** GET  → get a single cleaner by ID */
  CLEANER_GET: (id: string) => `/v1/cleaners/${id}`,
} as const;
