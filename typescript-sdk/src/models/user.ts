/**
 * User model — represents a Cleanster end-user account.
 */
export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  /** Bearer token — only present after fetchAccessToken(). */
  token?: string;
}

/** Request body for creating a new user. */
export interface CreateUserRequest {
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
}

/** Request body for verifying a JWT token. */
export interface VerifyJwtRequest {
  token: string;
}
