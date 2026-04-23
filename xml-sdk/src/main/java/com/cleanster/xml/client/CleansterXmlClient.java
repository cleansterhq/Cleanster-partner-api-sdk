package com.cleanster.xml.client;

import com.cleanster.xml.api.*;
import okhttp3.OkHttpClient;

/**
 * Main entry point for the Cleanster Partner API XML SDK.
 *
 * <p>All model objects are JAXB-annotated, so every response can be serialised to
 * XML via {@link XmlConverter#toXml(Object)} and every XML string can be deserialised
 * back with {@link XmlConverter#fromXml(String, Class)}.
 *
 * <h2>Quick start — sandbox</h2>
 * <pre>{@code
 * CleansterXmlClient client = CleansterXmlClient.sandbox("your-access-key");
 *
 * // Authenticate
 * User user = client.users().fetchAccessToken(userId).getData();
 * client.setToken(user.getToken());
 *
 * // Create a booking and get XML back
 * Booking booking = client.bookings().createBooking(req).getData();
 * System.out.println(XmlConverter.toXml(booking));
 * }</pre>
 *
 * <h2>Production</h2>
 * <pre>{@code
 * CleansterXmlClient client = CleansterXmlClient.production("your-access-key");
 * }</pre>
 */
public class CleansterXmlClient {

    public static final String SANDBOX_URL    = "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public";
    public static final String PRODUCTION_URL = "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public";

    private final XmlHttpClient     http;
    private final BookingsXmlApi    bookings;
    private final UsersXmlApi       users;
    private final PropertiesXmlApi  properties;
    private final ChecklistsXmlApi  checklists;
    private final PaymentMethodsXmlApi paymentMethods;
    private final WebhooksXmlApi    webhooks;
    private final BlacklistXmlApi   blacklist;
    private final OtherXmlApi       other;

    private CleansterXmlClient(XmlHttpClient http) {
        this.http           = http;
        this.bookings       = new BookingsXmlApi(http);
        this.users          = new UsersXmlApi(http);
        this.properties     = new PropertiesXmlApi(http);
        this.checklists     = new ChecklistsXmlApi(http);
        this.paymentMethods = new PaymentMethodsXmlApi(http);
        this.webhooks       = new WebhooksXmlApi(http);
        this.blacklist      = new BlacklistXmlApi(http);
        this.other          = new OtherXmlApi(http);
    }

    /* ─────────────────────── factories ──────────────────────────────────── */

    /** Client targeting the sandbox environment. */
    public static CleansterXmlClient sandbox(String accessKey) {
        return new CleansterXmlClient(
                XmlHttpClient.builder()
                        .baseUrl(SANDBOX_URL)
                        .accessKey(accessKey)
                        .build());
    }

    /** Client targeting the production environment. */
    public static CleansterXmlClient production(String accessKey) {
        return new CleansterXmlClient(
                XmlHttpClient.builder()
                        .baseUrl(PRODUCTION_URL)
                        .accessKey(accessKey)
                        .build());
    }

    /** Client with a custom base URL — useful for testing with MockWebServer. */
    public static CleansterXmlClient custom(String baseUrl, String accessKey, OkHttpClient httpClient) {
        return new CleansterXmlClient(
                XmlHttpClient.builder()
                        .baseUrl(baseUrl)
                        .accessKey(accessKey)
                        .httpClient(httpClient)
                        .build());
    }

    /* ─────────────────────── token management ───────────────────────────── */

    /** Set the auth token (obtained via {@code users().fetchAccessToken(...)}). */
    public void setToken(String token) { http.setToken(token); }

    /** Return the current auth token, or null if not yet set. */
    public String getToken() { return http.getToken(); }

    /* ─────────────────────── API accessors ──────────────────────────────── */

    public BookingsXmlApi        bookings()       { return bookings; }
    public UsersXmlApi           users()          { return users; }
    public PropertiesXmlApi      properties()     { return properties; }
    public ChecklistsXmlApi      checklists()     { return checklists; }
    public PaymentMethodsXmlApi  paymentMethods() { return paymentMethods; }
    public WebhooksXmlApi        webhooks()       { return webhooks; }
    public BlacklistXmlApi       blacklist()      { return blacklist; }
    public OtherXmlApi           other()          { return other; }

    /* ─────────────────────── XML convenience ────────────────────────────── */

    /**
     * Convenience wrapper: serialize any model object to an XML string.
     *
     * @param obj a JAXB-annotated model object
     * @return well-formed XML string
     */
    public static String toXml(Object obj) {
        return XmlConverter.toXml(obj);
    }

    /**
     * Convenience wrapper: deserialize an XML string to a model object.
     *
     * @param xml   XML string
     * @param clazz target class
     * @param <T>   target type
     * @return the deserialised object
     */
    public static <T> T fromXml(String xml, Class<T> clazz) {
        return XmlConverter.fromXml(xml, clazz);
    }
}
