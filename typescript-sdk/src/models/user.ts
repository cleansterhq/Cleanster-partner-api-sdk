/**
 * User model - represents a Cleanster end-user account.
 */
export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  /** Bearer token - only present after fetchAccessToken(). */
  token?: string;
}

/** Request body for creating a new user. */
export interface CreateUserRequest {
  email: string;
  firstName: string;
  lastName: string;
  /** Your own internal customer/user ID for this person - required by the live sandbox API. */
  customerId: string;
  phone?: string;
}

/**
 * Response from POST /v1/user/account.
 *
 * Confirmed against the live sandbox API: creating a user does NOT return a full
 * user profile - only the new Cleanster user ID and a per-user JWT already
 * prefixed with "Bearer ".
 */
export interface CreateUserResponse {
  userId: number;
  accessToken: string;
}

/** Request body for verifying a JWT token. */
export interface VerifyJwtRequest {
  token: string;
}
