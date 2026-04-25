/**
 * Token validation and scope enforcement.
 *
 * v1: Simple bearer token (API key) validation — checks that the header is
 * present and non-empty. No cryptographic verification at this layer; the
 * Cleanster API rejects invalid keys with 401.
 *
 * TODO: OAuth 2.0 + PKCE seam — replace `validateBearerToken` with a JWT
 *       verifier that checks signature, expiry, and scopes. The middleware
 *       in auth/middleware.ts does not need to change; only this file.
 *
 * Scopes (for future use):
 *   bookings:read   — list_bookings, get_booking
 *   bookings:write  — create_booking, cancel_booking, reschedule_booking, assign_crew
 *   properties:read — list_properties, get_property
 *   cleaners:read   — list_cleaners
 *   payouts:read    — get_payout_records
 *   checklists:write — update_checklist
 */

export interface TokenInfo {
  token: string;
  /** Scopes granted to this token. Populated by OAuth in a future version. */
  scopes: string[];
}

/**
 * Extract and validate the bearer token from an Authorization header value.
 * Returns null if the header is missing or malformed.
 */
export function validateBearerToken(authHeader: string | undefined): TokenInfo | null {
  if (!authHeader) return null;
  const match = authHeader.match(/^Bearer\s+(.+)$/i);
  if (!match || !match[1]) return null;
  const token = match[1].trim();
  if (!token) return null;
  return {
    token,
    // v1: all tokens get all scopes — OAuth will enforce granular scopes
    scopes: [
      'bookings:read',
      'bookings:write',
      'properties:read',
      'cleaners:read',
      'payouts:read',
      'checklists:write',
    ],
  };
}

/** Check whether a TokenInfo has a required scope. */
export function hasScope(tokenInfo: TokenInfo, required: string): boolean {
  return tokenInfo.scopes.includes(required);
}
