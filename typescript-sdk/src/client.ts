/**
 * CleansterClient — the main entry point for the Cleanster TypeScript SDK.
 *
 * @example
 * ```typescript
 * import { CleansterClient } from "cleanster";
 *
 * const client = CleansterClient.sandbox("your-access-key");
 *
 * // Create a user and authenticate
 * const { data: user } = await client.users.createUser({
 *   email: "user@example.com",
 *   firstName: "Jane",
 *   lastName: "Smith",
 * });
 *
 * const { data: tokenUser } = await client.users.fetchAccessToken(user.id);
 * client.setAccessToken(tokenUser.token!);
 *
 * // Create a booking
 * const { data: booking } = await client.bookings.createBooking({
 *   date: "2025-06-15",
 *   time: "10:00",
 *   propertyId: 1004,
 *   roomCount: 2,
 *   bathroomCount: 1,
 *   planId: 5,
 *   hours: 3,
 *   extraSupplies: false,
 *   paymentMethodId: 10,
 * });
 * console.log(`Booking #${booking.id} — ${booking.status}`);
 * ```
 */

import { BlacklistApi } from "./api/blacklist";
import { BookingsApi } from "./api/bookings";
import { ChecklistsApi } from "./api/checklists";
import { OtherApi } from "./api/other";
import { PaymentMethodsApi } from "./api/payment-methods";
import { PropertiesApi } from "./api/properties";
import { UsersApi } from "./api/users";
import { WebhooksApi } from "./api/webhooks";
import { CleansterConfig } from "./config";
import { HttpClient } from "./http-client";

export class CleansterClient {
  public readonly bookings: BookingsApi;
  public readonly users: UsersApi;
  public readonly properties: PropertiesApi;
  public readonly checklists: ChecklistsApi;
  public readonly other: OtherApi;
  public readonly blacklist: BlacklistApi;
  public readonly paymentMethods: PaymentMethodsApi;
  public readonly webhooks: WebhooksApi;

  private readonly _http: HttpClient;

  constructor(config: CleansterConfig) {
    this._http = new HttpClient(config);
    this.bookings = new BookingsApi(this._http);
    this.users = new UsersApi(this._http);
    this.properties = new PropertiesApi(this._http);
    this.checklists = new ChecklistsApi(this._http);
    this.other = new OtherApi(this._http);
    this.blacklist = new BlacklistApi(this._http);
    this.paymentMethods = new PaymentMethodsApi(this._http);
    this.webhooks = new WebhooksApi(this._http);
  }

  /**
   * Create a client connected to the **sandbox** environment.
   * Use this for all development and testing — no real charges or cleaners.
   *
   * @param accessKey  Your Cleanster partner access key.
   */
  static sandbox(accessKey: string): CleansterClient {
    return new CleansterClient(CleansterConfig.sandbox(accessKey));
  }

  /**
   * Create a client connected to the **production** environment.
   * Real charges will be applied and real cleaners dispatched.
   *
   * @param accessKey  Your Cleanster partner access key.
   */
  static production(accessKey: string): CleansterClient {
    return new CleansterClient(CleansterConfig.production(accessKey));
  }

  /**
   * Set the user-level bearer token for authenticated requests.
   * Obtain this by calling `client.users.fetchAccessToken(userId)`.
   *
   * @param token  The bearer token string, or null to clear.
   */
  setAccessToken(token: string | null): void {
    this._http.bearerToken = token;
  }

  /**
   * Returns the currently set user bearer token, or null if not set.
   */
  getAccessToken(): string | null {
    return this._http.bearerToken;
  }
}
