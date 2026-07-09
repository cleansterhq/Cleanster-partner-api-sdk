/**
 * UsersApi - manage end-user accounts and authentication tokens.
 */

import { HttpClient } from "../http-client";
import { User, CreateUserRequest, CreateUserResponse, VerifyJwtRequest } from "../models/user";
import { ApiResponse } from "../models/response";

export class UsersApi {
  constructor(private readonly http: HttpClient) {}

  /**
   * Create a new user account under your partner.
   *
   * Confirmed against the live sandbox API: requires `customerId` (undocumented in
   * the original spec) and returns only `{ userId, accessToken }`, not a full profile.
   * @param request  email, firstName, lastName, customerId, and optional phone.
   * @returns ApiResponse containing the new userId and accessToken.
   */
  createUser(request: CreateUserRequest): Promise<ApiResponse<CreateUserResponse>> {
    return this.http.post<CreateUserResponse>("/v1/user/account", request);
  }

  /**
   * Fetch the long-lived bearer token for a user.
   * Pass the returned token to client.setAccessToken().
   * @param userId  The user ID returned by createUser.
   * @returns ApiResponse with User containing the token field.
   */
  fetchAccessToken(userId: number): Promise<ApiResponse<User>> {
    return this.http.get<User>(`/v1/user/access-token/${userId}`);
  }

  /**
   * Verify that a JWT token is valid and has not expired.
   * @param request  The JWT string to verify.
   */
  verifyJwt(request: VerifyJwtRequest): Promise<ApiResponse<unknown>> {
    return this.http.post("/v1/user/verify-jwt", request);
  }
}
