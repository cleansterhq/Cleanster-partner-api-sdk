package com.cleanster.sdk.client;

import com.cleanster.sdk.api.*;

/**
 * Main entry point for the Cleanster Partner API SDK.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * CleansterClient client = CleansterClient.sandboxClient("your-access-key");
 *
 * // Create a user
 * CreateUserRequest req = new CreateUserRequest();
 * req.setEmail("user@example.com");
 * req.setFirstName("John");
 * req.setLastName("Doe");
 * CreateUserResponse user = client.users().createUser(req);
 *
 * // Fetch the access token and set it for subsequent calls
 * String token = client.users().fetchAccessToken(user.getData().getId()).getData().getToken();
 * client.setAccessToken(token);
 *
 * // List bookings
 * BookingListResponse bookings = client.bookings().getBookings(null, null);
 * }</pre>
 */
public class CleansterClient {

    private final HttpClient httpClient;
    private final BookingApi bookingApi;
    private final UserApi userApi;
    private final PropertyApi propertyApi;
    private final ChecklistApi checklistApi;
    private final OtherApi otherApi;
    private final BlacklistApi blacklistApi;
    private final PaymentMethodApi paymentMethodApi;
    private final WebhookApi webhookApi;

    public CleansterClient(CleansterConfig config) {
        this.httpClient = new HttpClient(config);
        this.bookingApi = new BookingApi(httpClient);
        this.userApi = new UserApi(httpClient);
        this.propertyApi = new PropertyApi(httpClient);
        this.checklistApi = new ChecklistApi(httpClient);
        this.otherApi = new OtherApi(httpClient);
        this.blacklistApi = new BlacklistApi(httpClient);
        this.paymentMethodApi = new PaymentMethodApi(httpClient);
        this.webhookApi = new WebhookApi(httpClient);
    }

    /**
     * Create a client pointing to the sandbox environment.
     *
     * @param accessKey Your Cleanster partner access key
     */
    public static CleansterClient sandboxClient(String accessKey) {
        return new CleansterClient(CleansterConfig.sandboxBuilder(accessKey).build());
    }

    /**
     * Create a client pointing to the production environment.
     *
     * @param accessKey Your Cleanster partner access key
     */
    public static CleansterClient productionClient(String accessKey) {
        return new CleansterClient(CleansterConfig.productionBuilder(accessKey).build());
    }

    /**
     * Set the user-level access token (Bearer token) for authenticated requests.
     * Call this after obtaining a token via {@link UserApi#fetchAccessToken(int)}.
     *
     * @param token The bearer token string
     */
    public void setAccessToken(String token) {
        httpClient.setBearerToken(token);
    }

    /**
     * Get the currently set access token, or null if not set.
     */
    public String getAccessToken() {
        return httpClient.getBearerToken();
    }

    /**
     * Access booking-related API operations (create, list, cancel, reschedule, etc.)
     */
    public BookingApi bookings() {
        return bookingApi;
    }

    /**
     * Access user-related API operations (create user, fetch token, verify JWT)
     */
    public UserApi users() {
        return userApi;
    }

    /**
     * Access property-related API operations (CRUD, cleaners, iCal, checklists)
     */
    public PropertyApi properties() {
        return propertyApi;
    }

    /**
     * Access checklist-related API operations (list, create, update, delete)
     */
    public ChecklistApi checklists() {
        return checklistApi;
    }

    /**
     * Access other utility operations (services, plans, cost estimate, cleaners, coupons)
     */
    public OtherApi other() {
        return otherApi;
    }

    /**
     * Access blacklist management operations
     */
    public BlacklistApi blacklist() {
        return blacklistApi;
    }

    /**
     * Access payment method operations
     */
    public PaymentMethodApi paymentMethods() {
        return paymentMethodApi;
    }

    /**
     * Access webhook management operations
     */
    public WebhookApi webhooks() {
        return webhookApi;
    }
}
