/**
 * Comprehensive unit tests for the Cleanster TypeScript SDK.
 * Uses jest.fn() to mock fetch — no network access or API keys required.
 */

import { CleansterClient } from "../src/client";
import { CleansterConfig, SANDBOX_BASE_URL, PRODUCTION_BASE_URL } from "../src/config";
import { CleansterAuthException, CleansterApiException, CleansterException } from "../src/exceptions";
import { HttpClient } from "../src/http-client";
import { BookingsApi } from "../src/api/bookings";
import { UsersApi } from "../src/api/users";
import { PropertiesApi } from "../src/api/properties";
import { ChecklistsApi } from "../src/api/checklists";
import { OtherApi } from "../src/api/other";
import { BlacklistApi } from "../src/api/blacklist";
import { PaymentMethodsApi } from "../src/api/payment-methods";
import { WebhooksApi } from "../src/api/webhooks";

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function ok(data: unknown = null) {
  return { status: 200, message: "OK", data };
}

function mockHttp(): jest.Mocked<HttpClient> {
  return {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
    get bearerToken() { return null; },
    set bearerToken(_: string | null) {},
  } as unknown as jest.Mocked<HttpClient>;
}

// ---------------------------------------------------------------------------
// CleansterConfig Tests
// ---------------------------------------------------------------------------

describe("CleansterConfig", () => {
  test("sandbox() sets sandbox URL", () => {
    const config = CleansterConfig.sandbox("key");
    expect(config.baseUrl).toBe(SANDBOX_BASE_URL);
  });

  test("production() sets production URL", () => {
    const config = CleansterConfig.production("key");
    expect(config.baseUrl).toBe(PRODUCTION_BASE_URL);
  });

  test("throws on empty accessKey", () => {
    expect(() => new CleansterConfig({ accessKey: "" })).toThrow();
  });

  test("throws on whitespace-only accessKey", () => {
    expect(() => new CleansterConfig({ accessKey: "   " })).toThrow();
  });

  test("strips trailing slash from baseUrl", () => {
    const config = new CleansterConfig({ accessKey: "k", baseUrl: "https://api.example.com/" });
    expect(config.baseUrl).toBe("https://api.example.com");
  });

  test("default timeout is positive", () => {
    const config = CleansterConfig.sandbox("k");
    expect(config.timeoutMs).toBeGreaterThan(0);
  });

  test("builder().sandbox() sets sandbox URL", () => {
    const config = CleansterConfig.builder("k").sandbox().build();
    expect(config.baseUrl).toBe(SANDBOX_BASE_URL);
  });

  test("builder().production() sets production URL", () => {
    const config = CleansterConfig.builder("k").production().build();
    expect(config.baseUrl).toBe(PRODUCTION_BASE_URL);
  });

  test("builder().timeoutMs() overrides timeout", () => {
    const config = CleansterConfig.builder("k").timeoutMs(60_000).build();
    expect(config.timeoutMs).toBe(60_000);
  });

  test("builder().baseUrl() sets custom URL", () => {
    const config = CleansterConfig.builder("k").baseUrl("https://custom.api.com").build();
    expect(config.baseUrl).toBe("https://custom.api.com");
  });
});

// ---------------------------------------------------------------------------
// CleansterClient Tests
// ---------------------------------------------------------------------------

describe("CleansterClient", () => {
  test("sandbox() creates client without throwing", () => {
    expect(() => CleansterClient.sandbox("test-key")).not.toThrow();
  });

  test("production() creates client without throwing", () => {
    expect(() => CleansterClient.production("test-key")).not.toThrow();
  });

  test("exposes all API namespaces", () => {
    const client = CleansterClient.sandbox("k");
    expect(client.bookings).toBeInstanceOf(BookingsApi);
    expect(client.users).toBeInstanceOf(UsersApi);
    expect(client.properties).toBeInstanceOf(PropertiesApi);
    expect(client.checklists).toBeInstanceOf(ChecklistsApi);
    expect(client.other).toBeInstanceOf(OtherApi);
    expect(client.blacklist).toBeInstanceOf(BlacklistApi);
    expect(client.paymentMethods).toBeInstanceOf(PaymentMethodsApi);
    expect(client.webhooks).toBeInstanceOf(WebhooksApi);
  });

  test("getAccessToken() returns null by default", () => {
    const client = CleansterClient.sandbox("k");
    expect(client.getAccessToken()).toBeNull();
  });

  test("setAccessToken / getAccessToken round-trip", () => {
    const client = CleansterClient.sandbox("k");
    client.setAccessToken("my-token-xyz");
    expect(client.getAccessToken()).toBe("my-token-xyz");
  });

  test("setAccessToken(null) clears token", () => {
    const client = CleansterClient.sandbox("k");
    client.setAccessToken("token");
    client.setAccessToken(null);
    expect(client.getAccessToken()).toBeNull();
  });
});

// ---------------------------------------------------------------------------
// BookingsApi Tests
// ---------------------------------------------------------------------------

describe("BookingsApi", () => {
  test("getBookings() with no params calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new BookingsApi(http);

    await api.getBookings();

    expect(http.get).toHaveBeenCalledWith("/v1/bookings", { pageNo: undefined, status: undefined });
  });

  test("getBookings() passes pageNo", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new BookingsApi(http);

    await api.getBookings(2);

    expect(http.get).toHaveBeenCalledWith("/v1/bookings", { pageNo: 2, status: undefined });
  });

  test("getBookings() passes status", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new BookingsApi(http);

    await api.getBookings(undefined, "COMPLETED");

    expect(http.get).toHaveBeenCalledWith("/v1/bookings", { pageNo: undefined, status: "COMPLETED" });
  });

  test("createBooking() posts to correct endpoint", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok({ id: 999, status: "OPEN" }));
    const api = new BookingsApi(http);

    const req = {
      date: "2025-06-15", time: "10:00", propertyId: 1004,
      roomCount: 2, bathroomCount: 1, planId: 5,
      hours: 3, extraSupplies: false, paymentMethodId: 10,
    };
    const result = await api.createBooking(req);

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/create", req);
    expect(result.data.id).toBe(999);
    expect(result.data.status).toBe("OPEN");
  });

  test("getBookingDetails() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({ id: 16926, status: "COMPLETED" }));
    const api = new BookingsApi(http);

    const result = await api.getBookingDetails(16926);

    expect(http.get).toHaveBeenCalledWith("/v1/bookings/16926");
    expect(result.data.id).toBe(16926);
  });

  test("cancelBooking() posts to correct endpoint with reason", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.cancelBooking(16459, { reason: "Changed plans" });

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/16459/cancel", { reason: "Changed plans" });
  });

  test("cancelBooking() with no args sends empty object", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.cancelBooking(16459);

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/16459/cancel", {});
  });

  test("rescheduleBooking() sends date and time", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.rescheduleBooking(16459, { date: "2025-07-01", time: "14:00" });

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/16459/reschedule", { date: "2025-07-01", time: "14:00" });
  });

  test("assignCleaner() sends cleanerId", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.assignCleaner(16459, { cleanerId: 5 });

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/16459/cleaner", { cleanerId: 5 });
  });

  test("removeAssignedCleaner() sends DELETE", async () => {
    const http = mockHttp();
    http.delete.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.removeAssignedCleaner(16459);

    expect(http.delete).toHaveBeenCalledWith("/v1/bookings/16459/cleaner");
  });

  test("adjustHours() sends hours", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.adjustHours(16459, { hours: 4.0 });

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/16459/hours", { hours: 4.0 });
  });

  test("payExpenses() sends paymentMethodId", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.payExpenses(16926, { paymentMethodId: 10 });

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/16926/expenses", { paymentMethodId: 10 });
  });

  test("getBookingInspection() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({}));
    const api = new BookingsApi(http);

    await api.getBookingInspection(16926);

    expect(http.get).toHaveBeenCalledWith("/v1/bookings/16926/inspection");
  });

  test("getBookingInspectionDetails() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({}));
    const api = new BookingsApi(http);

    await api.getBookingInspectionDetails(16926);

    expect(http.get).toHaveBeenCalledWith("/v1/bookings/16926/inspection/details");
  });

  test("assignChecklistToBooking() posts to correct URL", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.assignChecklistToBooking(16926, 105);

    expect(http.put).toHaveBeenCalledWith("/v1/bookings/16926/checklist/105");
  });

  test("submitFeedback() sends rating and comment", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.submitFeedback(16926, { rating: 5, comment: "Great!" });

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/16926/feedback", { rating: 5, comment: "Great!" });
  });

  test("addTip() sends amount and paymentMethodId", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.addTip(16926, { amount: 20.0, paymentMethodId: 10 });

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/16926/tip", { amount: 20.0, paymentMethodId: 10 });
  });

  test("getChat() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({}));
    const api = new BookingsApi(http);

    await api.getChat(17142);

    expect(http.get).toHaveBeenCalledWith("/v1/bookings/17142/chat");
  });

  test("sendMessage() posts message", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.sendMessage(16466, { message: "Hello!" });

    expect(http.post).toHaveBeenCalledWith("/v1/bookings/16466/chat", { message: "Hello!" });
  });

  test("deleteMessage() sends DELETE to correct URL", async () => {
    const http = mockHttp();
    http.delete.mockResolvedValue(ok());
    const api = new BookingsApi(http);

    await api.deleteMessage(16466, "msg-abc-123");

    expect(http.delete).toHaveBeenCalledWith("/v1/bookings/16466/chat/msg-abc-123");
  });
});

// ---------------------------------------------------------------------------
// UsersApi Tests
// ---------------------------------------------------------------------------

describe("UsersApi", () => {
  test("createUser() posts to correct endpoint", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok({ id: 42, email: "jane@example.com", firstName: "Jane", lastName: "Smith" }));
    const api = new UsersApi(http);

    const req = { email: "jane@example.com", firstName: "Jane", lastName: "Smith" };
    const result = await api.createUser(req);

    expect(http.post).toHaveBeenCalledWith("/v1/user/account", req);
    expect(result.data.id).toBe(42);
    expect(result.data.email).toBe("jane@example.com");
    expect(result.data.firstName).toBe("Jane");
  });

  test("createUser() with phone includes phone field", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok({ id: 43 }));
    const api = new UsersApi(http);

    await api.createUser({ email: "a@b.com", firstName: "A", lastName: "B", phone: "+15551234567" });

    const calledWith = http.post.mock.calls[0][1] as Record<string, unknown>;
    expect(calledWith.phone).toBe("+15551234567");
  });

  test("fetchAccessToken() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({ id: 42, token: "some-long-token" }));
    const api = new UsersApi(http);

    const result = await api.fetchAccessToken(42);

    expect(http.get).toHaveBeenCalledWith("/v1/user/access-token/42");
    expect(result.data.token).toBe("some-long-token");
  });

  test("verifyJwt() posts to correct endpoint", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok({}));
    const api = new UsersApi(http);

    await api.verifyJwt({ token: "my.jwt.token" });

    expect(http.post).toHaveBeenCalledWith("/v1/user/verify-jwt", { token: "my.jwt.token" });
  });
});

// ---------------------------------------------------------------------------
// PropertiesApi Tests
// ---------------------------------------------------------------------------

describe("PropertiesApi", () => {
  test("listProperties() with no filter", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new PropertiesApi(http);

    await api.listProperties();

    expect(http.get).toHaveBeenCalledWith("/v1/properties", undefined);
  });

  test("listProperties() with serviceId", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new PropertiesApi(http);

    await api.listProperties(1);

    expect(http.get).toHaveBeenCalledWith("/v1/properties", { serviceId: 1 });
  });

  test("addProperty() posts to correct endpoint", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok({ id: 1040, name: "Beach House" }));
    const api = new PropertiesApi(http);

    const req = { name: "Beach House", address: "123 Ocean Dr", city: "Miami", country: "USA", roomCount: 3, bathroomCount: 2, serviceId: 1 };
    const result = await api.addProperty(req);

    expect(http.post).toHaveBeenCalledWith("/v1/properties", req);
    expect(result.data.id).toBe(1040);
  });

  test("getProperty() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({ id: 1040, name: "Beach House" }));
    const api = new PropertiesApi(http);

    const result = await api.getProperty(1040);

    expect(http.get).toHaveBeenCalledWith("/v1/properties/1040");
    expect(result.data.name).toBe("Beach House");
  });

  test("deleteProperty() sends DELETE", async () => {
    const http = mockHttp();
    http.delete.mockResolvedValue(ok());
    const api = new PropertiesApi(http);

    await api.deleteProperty(1004);

    expect(http.delete).toHaveBeenCalledWith("/v1/properties/1004");
  });

  test("enableOrDisableProperty() posts enabled flag", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new PropertiesApi(http);

    await api.enableOrDisableProperty(1040, { enabled: false });

    expect(http.post).toHaveBeenCalledWith("/v1/properties/1040/enable-disable", { enabled: false });
  });

  test("getPropertyCleaners() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new PropertiesApi(http);

    await api.getPropertyCleaners(1040);

    expect(http.get).toHaveBeenCalledWith("/v1/properties/1040/cleaners");
  });

  test("assignCleanerToProperty() posts cleanerId", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new PropertiesApi(http);

    await api.assignCleanerToProperty(1040, { cleanerId: 5 });

    expect(http.post).toHaveBeenCalledWith("/v1/properties/1040/cleaners", { cleanerId: 5 });
  });

  test("unassignCleanerFromProperty() sends DELETE to correct URL", async () => {
    const http = mockHttp();
    http.delete.mockResolvedValue(ok());
    const api = new PropertiesApi(http);

    await api.unassignCleanerFromProperty(1040, 5);

    expect(http.delete).toHaveBeenCalledWith("/v1/properties/1040/cleaners/5");
  });

  test("addICalLink() sends icalLink", async () => {
    const http = mockHttp();
    http.put.mockResolvedValue(ok());
    const api = new PropertiesApi(http);

    await api.addICalLink(1040, { icalLink: "https://cal.example.com/feed.ics" });

    expect(http.put).toHaveBeenCalledWith("/v1/properties/1040/ical", { icalLink: "https://cal.example.com/feed.ics" });
  });

  test("getICalLink() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({}));
    const api = new PropertiesApi(http);

    await api.getICalLink(1040);

    expect(http.get).toHaveBeenCalledWith("/v1/properties/1040/ical");
  });

  test("assignChecklistToProperty() includes updateUpcomingBookings flag", async () => {
    const http = mockHttp();
    http.put.mockResolvedValue(ok());
    const api = new PropertiesApi(http);

    await api.assignChecklistToProperty(1040, 105, true);

    const calledPath = (http.put.mock.calls[0][0] as string);
    expect(calledPath).toContain("/v1/properties/1040/checklist/105");
    expect(calledPath).toContain("updateUpcomingBookings=true");
  });
});

// ---------------------------------------------------------------------------
// ChecklistsApi Tests
// ---------------------------------------------------------------------------

describe("ChecklistsApi", () => {
  test("listChecklists() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new ChecklistsApi(http);

    await api.listChecklists();

    expect(http.get).toHaveBeenCalledWith("/v1/checklist");
  });

  test("getChecklist() returns typed data", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({ id: 105, name: "Standard Clean", items: [{ id: 1, description: "Vacuum", isCompleted: false }] }));
    const api = new ChecklistsApi(http);

    const result = await api.getChecklist(105);

    expect(http.get).toHaveBeenCalledWith("/v1/checklist/105");
    expect(result.data.name).toBe("Standard Clean");
    expect(result.data.items[0].description).toBe("Vacuum");
  });

  test("createChecklist() posts name and items", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok({ id: 200, name: "My Checklist" }));
    const api = new ChecklistsApi(http);

    const req = { name: "My Checklist", items: ["Task 1", "Task 2"] };
    const result = await api.createChecklist(req);

    expect(http.post).toHaveBeenCalledWith("/v1/checklist", req);
    expect(result.data.id).toBe(200);
  });

  test("updateChecklist() puts to correct URL", async () => {
    const http = mockHttp();
    http.put.mockResolvedValue(ok({ id: 200, name: "Updated" }));
    const api = new ChecklistsApi(http);

    await api.updateChecklist(200, { name: "Updated", items: ["New task"] });

    expect(http.put).toHaveBeenCalledWith("/v1/checklist/200", { name: "Updated", items: ["New task"] });
  });

  test("deleteChecklist() sends DELETE", async () => {
    const http = mockHttp();
    http.delete.mockResolvedValue(ok());
    const api = new ChecklistsApi(http);

    await api.deleteChecklist(105);

    expect(http.delete).toHaveBeenCalledWith("/v1/checklist/105");
  });
});

// ---------------------------------------------------------------------------
// OtherApi Tests
// ---------------------------------------------------------------------------

describe("OtherApi", () => {
  test("getServices() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new OtherApi(http);

    await api.getServices();

    expect(http.get).toHaveBeenCalledWith("/v1/services");
  });

  test("getPlans() sends propertyId", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new OtherApi(http);

    await api.getPlans(1004);

    expect(http.get).toHaveBeenCalledWith("/v1/plans", { propertyId: 1004 });
  });

  test("getRecommendedHours() sends all params", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({}));
    const api = new OtherApi(http);

    await api.getRecommendedHours(1004, 2, 3);

    expect(http.get).toHaveBeenCalledWith("/v1/recommended-hours", { propertyId: 1004, bathroomCount: 2, roomCount: 3 });
  });

  test("calculateCost() posts request body", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok({}));
    const api = new OtherApi(http);

    const req = { propertyId: 1004, planId: 2, hours: 3.0 };
    await api.calculateCost(req);

    expect(http.post).toHaveBeenCalledWith("/v1/cost-estimate", req);
  });

  test("getCleaningExtras() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new OtherApi(http);

    await api.getCleaningExtras(1);

    expect(http.get).toHaveBeenCalledWith("/v1/cleaning-extras/1");
  });

  test("getAvailableCleaners() posts request body", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok([]));
    const api = new OtherApi(http);

    const req = { propertyId: 1004, date: "2025-06-15", time: "10:00" };
    await api.getAvailableCleaners(req);

    expect(http.post).toHaveBeenCalledWith("/v1/available-cleaners", req);
  });

  test("getCoupons() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new OtherApi(http);

    await api.getCoupons();

    expect(http.get).toHaveBeenCalledWith("/v1/coupons");
  });
});

// ---------------------------------------------------------------------------
// BlacklistApi Tests
// ---------------------------------------------------------------------------

describe("BlacklistApi", () => {
  test("listBlacklistedCleaners() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new BlacklistApi(http);

    await api.listBlacklistedCleaners();

    expect(http.get).toHaveBeenCalledWith("/v1/blacklist/cleaner");
  });

  test("addToBlacklist() posts request", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok());
    const api = new BlacklistApi(http);

    await api.addToBlacklist({ cleanerId: 7, reason: "Damaged furniture" });

    expect(http.post).toHaveBeenCalledWith("/v1/blacklist/cleaner", { cleanerId: 7, reason: "Damaged furniture" });
  });

  test("removeFromBlacklist() sends DELETE", async () => {
    const http = mockHttp();
    http.delete.mockResolvedValue(ok());
    const api = new BlacklistApi(http);

    await api.removeFromBlacklist({ cleanerId: 7 });

    expect(http.delete).toHaveBeenCalledWith("/v1/blacklist/cleaner", { cleanerId: 7 });
  });
});

// ---------------------------------------------------------------------------
// PaymentMethodsApi Tests
// ---------------------------------------------------------------------------

describe("PaymentMethodsApi", () => {
  test("getSetupIntentDetails() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({}));
    const api = new PaymentMethodsApi(http);

    await api.getSetupIntentDetails();

    expect(http.get).toHaveBeenCalledWith("/v1/payment-methods/setup-intent-details");
  });

  test("getPaypalClientToken() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok({}));
    const api = new PaymentMethodsApi(http);

    await api.getPaypalClientToken();

    expect(http.get).toHaveBeenCalledWith("/v1/payment-methods/paypal-client-token");
  });

  test("addPaymentMethod() posts request", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok({}));
    const api = new PaymentMethodsApi(http);

    await api.addPaymentMethod({ paymentMethodId: "pm_xxxx" });

    expect(http.post).toHaveBeenCalledWith("/v1/payment-methods", { paymentMethodId: "pm_xxxx" });
  });

  test("getPaymentMethods() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new PaymentMethodsApi(http);

    await api.getPaymentMethods();

    expect(http.get).toHaveBeenCalledWith("/v1/payment-methods");
  });

  test("deletePaymentMethod() sends DELETE to correct URL", async () => {
    const http = mockHttp();
    http.delete.mockResolvedValue(ok());
    const api = new PaymentMethodsApi(http);

    await api.deletePaymentMethod(193);

    expect(http.delete).toHaveBeenCalledWith("/v1/payment-methods/193");
  });

  test("setDefaultPaymentMethod() puts to correct URL", async () => {
    const http = mockHttp();
    http.put.mockResolvedValue(ok());
    const api = new PaymentMethodsApi(http);

    await api.setDefaultPaymentMethod(193);

    expect(http.put).toHaveBeenCalledWith("/v1/payment-methods/193/default");
  });
});

// ---------------------------------------------------------------------------
// WebhooksApi Tests
// ---------------------------------------------------------------------------

describe("WebhooksApi", () => {
  test("listWebhooks() calls correct URL", async () => {
    const http = mockHttp();
    http.get.mockResolvedValue(ok([]));
    const api = new WebhooksApi(http);

    await api.listWebhooks();

    expect(http.get).toHaveBeenCalledWith("/v1/webhooks");
  });

  test("createWebhook() posts request", async () => {
    const http = mockHttp();
    http.post.mockResolvedValue(ok({ id: 50 }));
    const api = new WebhooksApi(http);

    const req = { url: "https://example.com/webhook", event: "booking.created" };
    await api.createWebhook(req);

    expect(http.post).toHaveBeenCalledWith("/v1/webhooks", req);
  });

  test("updateWebhook() puts to correct URL", async () => {
    const http = mockHttp();
    http.put.mockResolvedValue(ok());
    const api = new WebhooksApi(http);

    const req = { url: "https://example.com/v2" };
    await api.updateWebhook(50, req);

    expect(http.put).toHaveBeenCalledWith("/v1/webhooks/50", req);
  });

  test("deleteWebhook() sends DELETE", async () => {
    const http = mockHttp();
    http.delete.mockResolvedValue(ok());
    const api = new WebhooksApi(http);

    await api.deleteWebhook(50);

    expect(http.delete).toHaveBeenCalledWith("/v1/webhooks/50");
  });
});

// ---------------------------------------------------------------------------
// Exception Tests
// ---------------------------------------------------------------------------

describe("Exceptions", () => {
  test("CleansterAuthException has statusCode 401", () => {
    const ex = new CleansterAuthException("Unauthorized", "{}");
    expect(ex.statusCode).toBe(401);
    expect(ex.message).toBe("Unauthorized");
    expect(ex.responseBody).toBe("{}");
  });

  test("CleansterApiException stores statusCode", () => {
    const ex = new CleansterApiException(404, "Not Found", "{}");
    expect(ex.statusCode).toBe(404);
    expect(ex.message).toBe("Not Found");
    expect(ex.responseBody).toBe("{}");
  });

  test("CleansterAuthException is instanceof CleansterException", () => {
    const ex = new CleansterAuthException("Unauth");
    expect(ex).toBeInstanceOf(CleansterException);
  });

  test("CleansterApiException is instanceof CleansterException", () => {
    const ex = new CleansterApiException(500, "Server Error");
    expect(ex).toBeInstanceOf(CleansterException);
  });

  test("CleansterAuthException has correct name", () => {
    const ex = new CleansterAuthException("Unauth");
    expect(ex.name).toBe("CleansterAuthException");
  });

  test("CleansterApiException has correct name", () => {
    const ex = new CleansterApiException(404, "Not Found");
    expect(ex.name).toBe("CleansterApiException");
  });

  test("propagates from rejected http.get", async () => {
    const http = mockHttp();
    http.get.mockRejectedValue(new CleansterAuthException("Unauthorized"));
    const api = new BookingsApi(http);

    await expect(api.getBookings()).rejects.toBeInstanceOf(CleansterAuthException);
  });

  test("propagates CleansterApiException with status code", async () => {
    const http = mockHttp();
    http.get.mockRejectedValue(new CleansterApiException(404, "Not Found"));
    const api = new BookingsApi(http);

    await expect(api.getBookingDetails(99999)).rejects.toMatchObject({ statusCode: 404 });
  });
});
