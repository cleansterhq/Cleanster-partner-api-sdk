/**
 * BlacklistApi — prevent specific cleaners from being assigned to your bookings.
 */

import { HttpClient } from "../http-client";
import { ApiResponse } from "../models/response";

export interface BlacklistRequest {
  cleanerId: number;
  reason?: string;
}

export class BlacklistApi {
  constructor(private readonly http: HttpClient) {}

  /**
   * Return all cleaners currently on the blacklist.
   */
  listBlacklistedCleaners(): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/blacklist/cleaner");
  }

  /**
   * Add a cleaner to the blacklist.
   * @param request  cleanerId and optional reason.
   */
  addToBlacklist(request: BlacklistRequest): Promise<ApiResponse<unknown>> {
    return this.http.post("/v1/blacklist/cleaner", request);
  }

  /**
   * Remove a cleaner from the blacklist.
   * @param request  cleanerId to remove.
   */
  removeFromBlacklist(request: BlacklistRequest): Promise<ApiResponse<unknown>> {
    return this.http.delete("/v1/blacklist/cleaner", request);
  }
}
