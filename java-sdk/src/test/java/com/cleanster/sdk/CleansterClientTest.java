package com.cleanster.sdk;

import com.cleanster.sdk.api.*;
import com.cleanster.sdk.client.CleansterClient;
import com.cleanster.sdk.client.CleansterConfig;
import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.exception.CleansterApiException;
import com.cleanster.sdk.exception.CleansterAuthException;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for all SDK API classes using Mockito to mock the HTTP client.
 * These tests verify correct URL construction, HTTP method selection, request body
 * serialization, and response deserialization without requiring network access.
 */
class CleansterClientTest {

    // ---- Config / CleansterClient Tests ----

    @Test
    @DisplayName("Sandbox config uses sandbox base URL")
    void sandboxConfigUsesSandboxUrl() {
        CleansterConfig config = CleansterConfig.sandboxBuilder("my-key").build();
        assertEquals(CleansterConfig.SANDBOX_BASE_URL, config.getBaseUrl());
    }

    @Test
    @DisplayName("Production config uses production base URL")
    void productionConfigUsesProductionUrl() {
        CleansterConfig config = CleansterConfig.productionBuilder("my-key").build();
        assertEquals(CleansterConfig.PRODUCTION_BASE_URL, config.getBaseUrl());
    }

    @Test
    @DisplayName("Config rejects blank access key")
    void configRejectsBlankAccessKey() {
        assertThrows(IllegalArgumentException.class,
                () -> new CleansterConfig.Builder("").build());
    }

    @Test
    @DisplayName("Config rejects null access key")
    void configRejectsNullAccessKey() {
        assertThrows(IllegalArgumentException.class,
                () -> new CleansterConfig.Builder(null).build());
    }

    @Test
    @DisplayName("CleansterClient sandboxClient factory works")
    void sandboxClientFactory() {
        CleansterClient client = CleansterClient.sandboxClient("test-key");
        assertNotNull(client);
        assertNotNull(client.bookings());
        assertNotNull(client.users());
        assertNotNull(client.properties());
        assertNotNull(client.checklists());
        assertNotNull(client.other());
        assertNotNull(client.blacklist());
        assertNotNull(client.paymentMethods());
        assertNotNull(client.webhooks());
    }

    @Test
    @DisplayName("CleansterClient productionClient factory works")
    void productionClientFactory() {
        CleansterClient client = CleansterClient.productionClient("prod-key");
        assertNotNull(client);
    }

    @Test
    @DisplayName("setAccessToken stores and retrieves token")
    void setAndGetAccessToken() {
        CleansterClient client = CleansterClient.sandboxClient("key");
        client.setAccessToken("my-token-123");
        assertEquals("my-token-123", client.getAccessToken());
    }

    @Test
    @DisplayName("getAccessToken returns null when not set")
    void getAccessTokenNullByDefault() {
        CleansterClient client = CleansterClient.sandboxClient("key");
        assertNull(client.getAccessToken());
    }

    @Test
    @DisplayName("Config custom baseUrl is used")
    void configCustomBaseUrl() {
        CleansterConfig config = new CleansterConfig.Builder("key")
                .baseUrl("https://custom.example.com")
                .build();
        assertEquals("https://custom.example.com", config.getBaseUrl());
    }

    @Test
    @DisplayName("Config default timeouts are positive")
    void configDefaultTimeouts() {
        CleansterConfig config = CleansterConfig.sandboxBuilder("key").build();
        assertTrue(config.getConnectTimeoutSeconds() > 0);
        assertTrue(config.getReadTimeoutSeconds() > 0);
        assertTrue(config.getWriteTimeoutSeconds() > 0);
    }

    // ---- BookingApi Tests ----

    @Test
    @DisplayName("getBookings calls GET /v1/bookings with no params")
    void getBookingsNoParams() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        ApiResponse<Object> expected = new ApiResponse<>();
        expected.setStatus(200);
        when(mockHttp.get(eq("/v1/bookings"), any(TypeReference.class))).thenReturn(expected);

        ApiResponse<Object> result = api.getBookings(null, null);

        verify(mockHttp).get(eq("/v1/bookings"), any(TypeReference.class));
        assertEquals(200, result.getStatus());
    }

    @Test
    @DisplayName("getBookings appends pageNo query param")
    void getBookingsWithPageNo() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.get(anyString(), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.getBookings(2, null);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockHttp).get(pathCaptor.capture(), any(TypeReference.class));
        assertTrue(pathCaptor.getValue().contains("pageNo=2"));
    }

    @Test
    @DisplayName("getBookings appends status query param")
    void getBookingsWithStatus() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.get(anyString(), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.getBookings(null, "COMPLETED");

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockHttp).get(pathCaptor.capture(), any(TypeReference.class));
        assertTrue(pathCaptor.getValue().contains("status=COMPLETED"));
    }

    @Test
    @DisplayName("getBookings appends both pageNo and status query params")
    void getBookingsWithBothParams() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.get(anyString(), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.getBookings(1, "OPEN");

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockHttp).get(pathCaptor.capture(), any(TypeReference.class));
        String path = pathCaptor.getValue();
        assertTrue(path.contains("pageNo=1"));
        assertTrue(path.contains("status=OPEN"));
    }

    @Test
    @DisplayName("createBooking calls POST /v1/bookings/create")
    void createBooking() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        Booking booking = new Booking();
        booking.setId(999);
        booking.setStatus("OPEN");
        ApiResponse<Booking> expected = new ApiResponse<>();
        expected.setData(booking);
        when(mockHttp.post(eq("/v1/bookings/create"), any(), any(TypeReference.class)))
                .thenReturn(expected);

        CreateBookingRequest req = new CreateBookingRequest();
        req.setDate("2025-06-15");
        req.setTime("10:00");
        req.setPropertyId(1004);
        req.setRoomCount(2);
        req.setBathroomCount(1);
        req.setPlanId(5);
        req.setHours(3.0f);
        req.setExtraSupplies(false);
        req.setPaymentMethodId(10);

        ApiResponse<Booking> result = api.createBooking(req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/bookings/create"), bodyCaptor.capture(), any(TypeReference.class));
        CreateBookingRequest captured = (CreateBookingRequest) bodyCaptor.getValue();
        assertEquals("2025-06-15", captured.getDate());
        assertEquals(1004, captured.getPropertyId());
        assertEquals(999, result.getData().getId());
        assertEquals("OPEN", result.getData().getStatus());
    }

    @Test
    @DisplayName("getBookingDetails calls GET /v1/bookings/{id}")
    void getBookingDetails() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        Booking booking = new Booking();
        booking.setId(16926);
        booking.setStatus("COMPLETED");
        ApiResponse<Booking> expected = new ApiResponse<>();
        expected.setData(booking);
        when(mockHttp.get(eq("/v1/bookings/16926"), any(TypeReference.class))).thenReturn(expected);

        ApiResponse<Booking> result = api.getBookingDetails(16926);

        verify(mockHttp).get(eq("/v1/bookings/16926"), any(TypeReference.class));
        assertEquals(16926, result.getData().getId());
        assertEquals("COMPLETED", result.getData().getStatus());
    }

    @Test
    @DisplayName("cancelBooking calls POST /v1/bookings/{id}/cancel")
    void cancelBooking() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.post(eq("/v1/bookings/16459/cancel"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        CancelBookingRequest req = new CancelBookingRequest("Changed plans");
        api.cancelBooking(16459, req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/bookings/16459/cancel"), bodyCaptor.capture(), any(TypeReference.class));
        assertEquals("Changed plans", ((CancelBookingRequest) bodyCaptor.getValue()).getReason());
    }

    @Test
    @DisplayName("rescheduleBooking calls POST /v1/bookings/{id}/reschedule")
    void rescheduleBooking() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.post(eq("/v1/bookings/16459/reschedule"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        RescheduleBookingRequest req = new RescheduleBookingRequest("2025-07-01", "14:00");
        api.rescheduleBooking(16459, req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/bookings/16459/reschedule"), bodyCaptor.capture(), any(TypeReference.class));
        RescheduleBookingRequest captured = (RescheduleBookingRequest) bodyCaptor.getValue();
        assertEquals("2025-07-01", captured.getDate());
        assertEquals("14:00", captured.getTime());
    }

    @Test
    @DisplayName("assignCleaner calls POST /v1/bookings/{id}/cleaner")
    void assignCleaner() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.post(eq("/v1/bookings/16459/cleaner"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        CleanerAssignmentRequest req = new CleanerAssignmentRequest(5);
        api.assignCleaner(16459, req);

        verify(mockHttp).post(eq("/v1/bookings/16459/cleaner"), any(), any(TypeReference.class));
    }

    @Test
    @DisplayName("removeAssignedCleaner calls DELETE /v1/bookings/{id}/cleaner")
    void removeAssignedCleaner() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.delete(eq("/v1/bookings/16459/cleaner"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.removeAssignedCleaner(16459);

        verify(mockHttp).delete(eq("/v1/bookings/16459/cleaner"), any(TypeReference.class));
    }

    @Test
    @DisplayName("adjustHours calls POST /v1/bookings/{id}/hours")
    void adjustHours() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.post(eq("/v1/bookings/16459/hours"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        AdjustHoursRequest req = new AdjustHoursRequest(4.0f);
        api.adjustHours(16459, req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/bookings/16459/hours"), bodyCaptor.capture(), any(TypeReference.class));
        assertEquals(4.0f, ((AdjustHoursRequest) bodyCaptor.getValue()).getHours());
    }

    @Test
    @DisplayName("getChat calls GET /v1/bookings/{id}/chat")
    void getChat() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.get(eq("/v1/bookings/17142/chat"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.getChat(17142);

        verify(mockHttp).get(eq("/v1/bookings/17142/chat"), any(TypeReference.class));
    }

    @Test
    @DisplayName("sendMessage calls POST /v1/bookings/{id}/chat")
    void sendMessage() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.post(eq("/v1/bookings/16466/chat"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        SendMessageRequest req = new SendMessageRequest("Hello cleaner!");
        api.sendMessage(16466, req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/bookings/16466/chat"), bodyCaptor.capture(), any(TypeReference.class));
        assertEquals("Hello cleaner!", ((SendMessageRequest) bodyCaptor.getValue()).getMessage());
    }

    @Test
    @DisplayName("deleteMessage calls DELETE /v1/bookings/{id}/chat/{msgId}")
    void deleteMessage() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.delete(eq("/v1/bookings/16466/chat/msg-abc-123"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.deleteMessage(16466, "msg-abc-123");

        verify(mockHttp).delete(eq("/v1/bookings/16466/chat/msg-abc-123"), any(TypeReference.class));
    }

    @Test
    @DisplayName("submitFeedback calls POST /v1/bookings/{id}/feedback")
    void submitFeedback() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.post(eq("/v1/bookings/16926/feedback"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        FeedbackRequest req = new FeedbackRequest(5, "Excellent service!");
        api.submitFeedback(16926, req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/bookings/16926/feedback"), bodyCaptor.capture(), any(TypeReference.class));
        FeedbackRequest captured = (FeedbackRequest) bodyCaptor.getValue();
        assertEquals(5, captured.getRating());
        assertEquals("Excellent service!", captured.getComment());
    }

    @Test
    @DisplayName("addTip calls POST /v1/bookings/{id}/tip")
    void addTip() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.post(eq("/v1/bookings/16926/tip"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        TipRequest req = new TipRequest(20.0f, 10);
        api.addTip(16926, req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/bookings/16926/tip"), bodyCaptor.capture(), any(TypeReference.class));
        TipRequest captured = (TipRequest) bodyCaptor.getValue();
        assertEquals(20.0f, captured.getAmount());
        assertEquals(10, captured.getPaymentMethodId());
    }

    // ---- UserApi Tests ----

    @Test
    @DisplayName("createUser calls POST /v1/user/account")
    void createUser() {
        HttpClient mockHttp = mock(HttpClient.class);
        UserApi api = new UserApi(mockHttp);
        User user = new User();
        user.setId(42);
        user.setEmail("john@example.com");
        ApiResponse<User> expected = new ApiResponse<>();
        expected.setData(user);
        expected.setStatus(200);
        when(mockHttp.post(eq("/v1/user/account"), any(), any(TypeReference.class))).thenReturn(expected);

        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("john@example.com");
        req.setFirstName("John");
        req.setLastName("Doe");

        ApiResponse<User> result = api.createUser(req);

        verify(mockHttp).post(eq("/v1/user/account"), any(), any(TypeReference.class));
        assertEquals(42, result.getData().getId());
        assertEquals("john@example.com", result.getData().getEmail());
    }

    @Test
    @DisplayName("fetchAccessToken calls GET /v1/user/access-token/{id}")
    void fetchAccessToken() {
        HttpClient mockHttp = mock(HttpClient.class);
        UserApi api = new UserApi(mockHttp);
        User user = new User();
        user.setToken("some-long-token");
        ApiResponse<User> expected = new ApiResponse<>();
        expected.setData(user);
        when(mockHttp.get(eq("/v1/user/access-token/6"), any(TypeReference.class))).thenReturn(expected);

        ApiResponse<User> result = api.fetchAccessToken(6);

        verify(mockHttp).get(eq("/v1/user/access-token/6"), any(TypeReference.class));
        assertEquals("some-long-token", result.getData().getToken());
    }

    @Test
    @DisplayName("verifyJwt calls POST /v1/user/verify-jwt")
    void verifyJwt() {
        HttpClient mockHttp = mock(HttpClient.class);
        UserApi api = new UserApi(mockHttp);
        when(mockHttp.post(eq("/v1/user/verify-jwt"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        VerifyJwtRequest req = new VerifyJwtRequest("some.jwt.token");
        api.verifyJwt(req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/user/verify-jwt"), bodyCaptor.capture(), any(TypeReference.class));
        assertEquals("some.jwt.token", ((VerifyJwtRequest) bodyCaptor.getValue()).getToken());
    }

    // ---- PropertyApi Tests ----

    @Test
    @DisplayName("listProperties calls GET /v1/properties with no filter")
    void listPropertiesNoFilter() {
        HttpClient mockHttp = mock(HttpClient.class);
        PropertyApi api = new PropertyApi(mockHttp);
        when(mockHttp.get(eq("/v1/properties"), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.listProperties(null);

        verify(mockHttp).get(eq("/v1/properties"), any(TypeReference.class));
    }

    @Test
    @DisplayName("listProperties appends serviceId filter")
    void listPropertiesWithServiceId() {
        HttpClient mockHttp = mock(HttpClient.class);
        PropertyApi api = new PropertyApi(mockHttp);
        when(mockHttp.get(anyString(), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.listProperties(1);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockHttp).get(pathCaptor.capture(), any(TypeReference.class));
        assertTrue(pathCaptor.getValue().contains("serviceId=1"));
    }

    @Test
    @DisplayName("addProperty calls POST /v1/properties")
    void addProperty() {
        HttpClient mockHttp = mock(HttpClient.class);
        PropertyApi api = new PropertyApi(mockHttp);
        Property property = new Property();
        property.setId(1040);
        property.setName("Test Home");
        ApiResponse<Property> expected = new ApiResponse<>();
        expected.setData(property);
        when(mockHttp.post(eq("/v1/properties"), any(), any(TypeReference.class))).thenReturn(expected);

        CreatePropertyRequest req = new CreatePropertyRequest();
        req.setName("Test Home");
        req.setAddress("123 Main St");
        req.setCity("Toronto");
        req.setCountry("Canada");
        req.setRoomCount(2);
        req.setBathroomCount(1);
        req.setServiceId(1);

        ApiResponse<Property> result = api.addProperty(req);

        verify(mockHttp).post(eq("/v1/properties"), any(), any(TypeReference.class));
        assertEquals(1040, result.getData().getId());
        assertEquals("Test Home", result.getData().getName());
    }

    @Test
    @DisplayName("getProperty calls GET /v1/properties/{id}")
    void getProperty() {
        HttpClient mockHttp = mock(HttpClient.class);
        PropertyApi api = new PropertyApi(mockHttp);
        Property property = new Property();
        property.setId(1040);
        property.setName("Beach House");
        ApiResponse<Property> expected = new ApiResponse<>();
        expected.setData(property);
        when(mockHttp.get(eq("/v1/properties/1040"), any(TypeReference.class))).thenReturn(expected);

        ApiResponse<Property> result = api.getProperty(1040);

        verify(mockHttp).get(eq("/v1/properties/1040"), any(TypeReference.class));
        assertEquals("Beach House", result.getData().getName());
    }

    @Test
    @DisplayName("deleteProperty calls DELETE /v1/properties/{id}")
    void deleteProperty() {
        HttpClient mockHttp = mock(HttpClient.class);
        PropertyApi api = new PropertyApi(mockHttp);
        when(mockHttp.delete(eq("/v1/properties/1004"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.deleteProperty(1004);

        verify(mockHttp).delete(eq("/v1/properties/1004"), any(TypeReference.class));
    }

    @Test
    @DisplayName("addICalLink calls PUT /v1/properties/{id}/ical")
    void addICalLink() {
        HttpClient mockHttp = mock(HttpClient.class);
        PropertyApi api = new PropertyApi(mockHttp);
        when(mockHttp.put(eq("/v1/properties/1004/ical"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        ICalRequest req = new ICalRequest("https://calendar.example.com/feed.ics");
        api.addICalLink(1004, req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).put(eq("/v1/properties/1004/ical"), bodyCaptor.capture(), any(TypeReference.class));
        assertEquals("https://calendar.example.com/feed.ics", ((ICalRequest) bodyCaptor.getValue()).getIcalLink());
    }

    @Test
    @DisplayName("getPropertyCleaners calls GET /v1/properties/{id}/cleaners")
    void getPropertyCleaners() {
        HttpClient mockHttp = mock(HttpClient.class);
        PropertyApi api = new PropertyApi(mockHttp);
        when(mockHttp.get(eq("/v1/properties/1040/cleaners"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.getPropertyCleaners(1040);

        verify(mockHttp).get(eq("/v1/properties/1040/cleaners"), any(TypeReference.class));
    }

    @Test
    @DisplayName("assignChecklistToProperty calls PUT /v1/properties/{id}/checklist/{checklistId}")
    void assignChecklistToProperty() {
        HttpClient mockHttp = mock(HttpClient.class);
        PropertyApi api = new PropertyApi(mockHttp);
        when(mockHttp.put(anyString(), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.assignChecklistToProperty(1040, 200, false);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockHttp).put(pathCaptor.capture(), any(), any(TypeReference.class));
        String path = pathCaptor.getValue();
        assertTrue(path.contains("/v1/properties/1040/checklist/200"));
    }

    // ---- ChecklistApi Tests ----

    @Test
    @DisplayName("listChecklists calls GET /v1/checklist")
    void listChecklists() {
        HttpClient mockHttp = mock(HttpClient.class);
        ChecklistApi api = new ChecklistApi(mockHttp);
        when(mockHttp.get(eq("/v1/checklist"), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.listChecklists();

        verify(mockHttp).get(eq("/v1/checklist"), any(TypeReference.class));
    }

    @Test
    @DisplayName("createChecklist calls POST /v1/checklist with name and items")
    void createChecklist() {
        HttpClient mockHttp = mock(HttpClient.class);
        ChecklistApi api = new ChecklistApi(mockHttp);
        Checklist checklist = new Checklist();
        checklist.setId(200);
        checklist.setName("Standard Clean");
        ApiResponse<Checklist> expected = new ApiResponse<>();
        expected.setData(checklist);
        when(mockHttp.post(eq("/v1/checklist"), any(), any(TypeReference.class))).thenReturn(expected);

        List<String> items = Arrays.asList("Vacuum floors", "Wipe countertops", "Clean bathrooms");
        CreateChecklistRequest req = new CreateChecklistRequest("Standard Clean", items);

        ApiResponse<Checklist> result = api.createChecklist(req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/checklist"), bodyCaptor.capture(), any(TypeReference.class));
        CreateChecklistRequest captured = (CreateChecklistRequest) bodyCaptor.getValue();
        assertEquals("Standard Clean", captured.getName());
        assertEquals(3, captured.getItems().size());
        assertEquals(200, result.getData().getId());
    }

    @Test
    @DisplayName("updateChecklist calls PUT /v1/checklist/{id}")
    void updateChecklist() {
        HttpClient mockHttp = mock(HttpClient.class);
        ChecklistApi api = new ChecklistApi(mockHttp);
        when(mockHttp.put(eq("/v1/checklist/200"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        List<String> items = Arrays.asList("New task 1", "New task 2");
        CreateChecklistRequest req = new CreateChecklistRequest("Updated List", items);
        api.updateChecklist(200, req);

        verify(mockHttp).put(eq("/v1/checklist/200"), any(), any(TypeReference.class));
    }

    @Test
    @DisplayName("deleteChecklist calls DELETE /v1/checklist/{id}")
    void deleteChecklist() {
        HttpClient mockHttp = mock(HttpClient.class);
        ChecklistApi api = new ChecklistApi(mockHttp);
        when(mockHttp.delete(eq("/v1/checklist/105"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.deleteChecklist(105);

        verify(mockHttp).delete(eq("/v1/checklist/105"), any(TypeReference.class));
    }

    // ---- OtherApi Tests ----

    @Test
    @DisplayName("getServices calls GET /v1/services")
    void getServices() {
        HttpClient mockHttp = mock(HttpClient.class);
        OtherApi api = new OtherApi(mockHttp);
        when(mockHttp.get(eq("/v1/services"), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.getServices();

        verify(mockHttp).get(eq("/v1/services"), any(TypeReference.class));
    }

    @Test
    @DisplayName("getPlans calls GET /v1/plans with propertyId")
    void getPlans() {
        HttpClient mockHttp = mock(HttpClient.class);
        OtherApi api = new OtherApi(mockHttp);
        when(mockHttp.get(anyString(), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.getPlans(1004);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockHttp).get(pathCaptor.capture(), any(TypeReference.class));
        String path = pathCaptor.getValue();
        assertTrue(path.contains("propertyId=1004"));
    }

    @Test
    @DisplayName("getRecommendedHours includes all query params")
    void getRecommendedHours() {
        HttpClient mockHttp = mock(HttpClient.class);
        OtherApi api = new OtherApi(mockHttp);
        when(mockHttp.get(anyString(), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.getRecommendedHours(1004, 2, 3);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockHttp).get(pathCaptor.capture(), any(TypeReference.class));
        String path = pathCaptor.getValue();
        assertTrue(path.contains("propertyId=1004"));
        assertTrue(path.contains("bathroomCount=2"));
        assertTrue(path.contains("roomCount=3"));
    }

    @Test
    @DisplayName("calculateCost calls POST /v1/cost-estimate")
    void calculateCost() {
        HttpClient mockHttp = mock(HttpClient.class);
        OtherApi api = new OtherApi(mockHttp);
        when(mockHttp.post(eq("/v1/cost-estimate"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        CostEstimateRequest req = new CostEstimateRequest();
        req.setPropertyId(1004);
        req.setPlanId(2);
        req.setHours(3.0f);
        api.calculateCost(req);

        verify(mockHttp).post(eq("/v1/cost-estimate"), any(), any(TypeReference.class));
    }

    @Test
    @DisplayName("getCleaningExtras calls GET /v1/cleaning-extras/{serviceId}")
    void getExtras() {
        HttpClient mockHttp = mock(HttpClient.class);
        OtherApi api = new OtherApi(mockHttp);
        when(mockHttp.get(anyString(), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.getCleaningExtras(1);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockHttp).get(pathCaptor.capture(), any(TypeReference.class));
        assertTrue(pathCaptor.getValue().contains("/v1/cleaning-extras/1"));
    }

    @Test
    @DisplayName("getCoupons calls GET /v1/coupons")
    void getCoupons() {
        HttpClient mockHttp = mock(HttpClient.class);
        OtherApi api = new OtherApi(mockHttp);
        when(mockHttp.get(eq("/v1/coupons"), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.getCoupons();

        verify(mockHttp).get(eq("/v1/coupons"), any(TypeReference.class));
    }

    // ---- BlacklistApi Tests ----

    @Test
    @DisplayName("listBlacklistedCleaners calls GET /v1/blacklist/cleaner")
    void listBlacklistedCleaners() {
        HttpClient mockHttp = mock(HttpClient.class);
        BlacklistApi api = new BlacklistApi(mockHttp);
        when(mockHttp.get(eq("/v1/blacklist/cleaner"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.listBlacklistedCleaners();

        verify(mockHttp).get(eq("/v1/blacklist/cleaner"), any(TypeReference.class));
    }

    @Test
    @DisplayName("addToBlacklist calls POST /v1/blacklist/cleaner")
    void addToBlacklist() {
        HttpClient mockHttp = mock(HttpClient.class);
        BlacklistApi api = new BlacklistApi(mockHttp);
        when(mockHttp.post(eq("/v1/blacklist/cleaner"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        BlacklistRequest req = new BlacklistRequest(7, "Damaged furniture");
        api.addToBlacklist(req);

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockHttp).post(eq("/v1/blacklist/cleaner"), bodyCaptor.capture(), any(TypeReference.class));
        assertEquals(7, ((BlacklistRequest) bodyCaptor.getValue()).getCleanerId());
    }

    @Test
    @DisplayName("removeFromBlacklist calls DELETE /v1/blacklist/cleaner with body")
    void removeFromBlacklist() {
        HttpClient mockHttp = mock(HttpClient.class);
        BlacklistApi api = new BlacklistApi(mockHttp);
        when(mockHttp.delete(eq("/v1/blacklist/cleaner"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        BlacklistRequest req = new BlacklistRequest(7, null);
        api.removeFromBlacklist(req);

        verify(mockHttp).delete(eq("/v1/blacklist/cleaner"), any(), any(TypeReference.class));
    }

    // ---- PaymentMethodApi Tests ----

    @Test
    @DisplayName("getPaymentMethods calls GET /v1/payment-methods")
    void getPaymentMethods() {
        HttpClient mockHttp = mock(HttpClient.class);
        PaymentMethodApi api = new PaymentMethodApi(mockHttp);
        when(mockHttp.get(eq("/v1/payment-methods"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.getPaymentMethods();

        verify(mockHttp).get(eq("/v1/payment-methods"), any(TypeReference.class));
    }

    @Test
    @DisplayName("deletePaymentMethod calls DELETE /v1/payment-methods/{id}")
    void deletePaymentMethod() {
        HttpClient mockHttp = mock(HttpClient.class);
        PaymentMethodApi api = new PaymentMethodApi(mockHttp);
        when(mockHttp.delete(eq("/v1/payment-methods/193"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.deletePaymentMethod(193);

        verify(mockHttp).delete(eq("/v1/payment-methods/193"), any(TypeReference.class));
    }

    @Test
    @DisplayName("setDefaultPaymentMethod calls PUT /v1/payment-methods/{id}/default")
    void setDefaultPaymentMethod() {
        HttpClient mockHttp = mock(HttpClient.class);
        PaymentMethodApi api = new PaymentMethodApi(mockHttp);
        when(mockHttp.put(eq("/v1/payment-methods/193/default"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.setDefaultPaymentMethod(193);

        verify(mockHttp).put(eq("/v1/payment-methods/193/default"), any(), any(TypeReference.class));
    }

    // ---- WebhookApi Tests ----

    @Test
    @DisplayName("listWebhooks calls GET /v1/webhooks")
    void listWebhooks() {
        HttpClient mockHttp = mock(HttpClient.class);
        WebhookApi api = new WebhookApi(mockHttp);
        when(mockHttp.get(eq("/v1/webhooks"), any(TypeReference.class))).thenReturn(new ApiResponse<>());

        api.listWebhooks();

        verify(mockHttp).get(eq("/v1/webhooks"), any(TypeReference.class));
    }

    @Test
    @DisplayName("createWebhook calls POST /v1/webhooks")
    void createWebhook() {
        HttpClient mockHttp = mock(HttpClient.class);
        WebhookApi api = new WebhookApi(mockHttp);
        ApiResponse<Object> expected = new ApiResponse<>();
        expected.setStatus(200);
        when(mockHttp.post(eq("/v1/webhooks"), any(), any(TypeReference.class))).thenReturn(expected);

        java.util.Map<String, String> payload = new java.util.HashMap<>();
        payload.put("url", "https://example.com/webhook");
        payload.put("event", "booking.created");
        ApiResponse<Object> result = api.createWebhook(payload);

        verify(mockHttp).post(eq("/v1/webhooks"), any(), any(TypeReference.class));
        assertEquals(200, result.getStatus());
    }

    @Test
    @DisplayName("updateWebhook calls PUT /v1/webhooks/{id}")
    void updateWebhook() {
        HttpClient mockHttp = mock(HttpClient.class);
        WebhookApi api = new WebhookApi(mockHttp);
        when(mockHttp.put(eq("/v1/webhooks/50"), any(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.updateWebhook(50, new java.util.HashMap<>());

        verify(mockHttp).put(eq("/v1/webhooks/50"), any(), any(TypeReference.class));
    }

    @Test
    @DisplayName("deleteWebhook calls DELETE /v1/webhooks/{id}")
    void deleteWebhook() {
        HttpClient mockHttp = mock(HttpClient.class);
        WebhookApi api = new WebhookApi(mockHttp);
        when(mockHttp.delete(eq("/v1/webhooks/50"), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>());

        api.deleteWebhook(50);

        verify(mockHttp).delete(eq("/v1/webhooks/50"), any(TypeReference.class));
    }

    // ---- Exception / Error Handling Tests ----

    @Test
    @DisplayName("CleansterAuthException has correct message and status")
    void authExceptionFields() {
        CleansterAuthException ex = new CleansterAuthException("Unauthorized", "{\"message\":\"Unauthorized\"}");
        assertEquals("Unauthorized", ex.getMessage());
        assertEquals(401, ex.getStatusCode());
        assertNotNull(ex.getResponseBody());
    }

    @Test
    @DisplayName("CleansterApiException stores HTTP status code")
    void apiExceptionStatusCode() {
        CleansterApiException ex = new CleansterApiException(404, "Not Found", "{}");
        assertEquals(404, ex.getStatusCode());
        assertEquals("Not Found", ex.getMessage());
    }

    @Test
    @DisplayName("CleansterApiException stores 500 status code")
    void apiException500StatusCode() {
        CleansterApiException ex = new CleansterApiException(500, "Internal Server Error", "{}");
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    @DisplayName("API propagates exception from HttpClient")
    void apiPropagatesException() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.get(anyString(), any(TypeReference.class)))
                .thenThrow(new CleansterAuthException("Unauthorized", "{}"));

        assertThrows(CleansterAuthException.class,
                () -> api.getBookings(null, null));
    }

    @Test
    @DisplayName("API propagates CleansterApiException from HttpClient")
    void apiPropagatesApiException() {
        HttpClient mockHttp = mock(HttpClient.class);
        BookingApi api = new BookingApi(mockHttp);
        when(mockHttp.get(anyString(), any(TypeReference.class)))
                .thenThrow(new CleansterApiException(404, "Not Found", "{}"));

        CleansterApiException ex = assertThrows(CleansterApiException.class,
                () -> api.getBookingDetails(99999));
        assertEquals(404, ex.getStatusCode());
    }

    // ---- Model Tests ----

    @Test
    @DisplayName("Booking model stores and retrieves fields")
    void bookingModelFields() {
        Booking b = new Booking();
        b.setId(100);
        b.setStatus("OPEN");
        b.setHours(3.0f);
        assertEquals(100, b.getId());
        assertEquals("OPEN", b.getStatus());
        assertEquals(3.0f, b.getHours());
    }

    @Test
    @DisplayName("User model stores and retrieves fields")
    void userModelFields() {
        User u = new User();
        u.setId(42);
        u.setEmail("test@example.com");
        u.setFirstName("Alice");
        u.setLastName("Smith");
        u.setToken("abc.def.ghi");
        assertEquals(42, u.getId());
        assertEquals("test@example.com", u.getEmail());
        assertEquals("Alice", u.getFirstName());
        assertEquals("Smith", u.getLastName());
        assertEquals("abc.def.ghi", u.getToken());
    }

    @Test
    @DisplayName("Property model stores and retrieves fields")
    void propertyModelFields() {
        Property p = new Property();
        p.setId(1040);
        p.setName("Beach House");
        p.setAddress("123 Ocean Dr");
        p.setCity("Miami");
        p.setCountry("USA");
        p.setRoomCount(3);
        p.setBathroomCount(2);
        assertEquals(1040, p.getId());
        assertEquals("Beach House", p.getName());
        assertEquals("Miami", p.getCity());
        assertEquals(3, p.getRoomCount());
    }

    @Test
    @DisplayName("ApiResponse model stores and retrieves fields")
    void apiResponseModelFields() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("OK");
        response.setData("test-data");
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getMessage());
        assertEquals("test-data", response.getData());
    }

    @Test
    @DisplayName("CreateBookingRequest stores all fields")
    void createBookingRequestFields() {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setDate("2025-06-15");
        req.setTime("10:00");
        req.setPropertyId(1004);
        req.setRoomCount(2);
        req.setBathroomCount(1);
        req.setPlanId(5);
        req.setHours(3.0f);
        req.setExtraSupplies(true);
        req.setPaymentMethodId(10);
        assertEquals("2025-06-15", req.getDate());
        assertEquals("10:00", req.getTime());
        assertEquals(1004, req.getPropertyId());
        assertTrue(req.getExtraSupplies());
    }

    @Test
    @DisplayName("CancelBookingRequest stores reason")
    void cancelBookingRequestFields() {
        CancelBookingRequest req = new CancelBookingRequest("Changed plans");
        assertEquals("Changed plans", req.getReason());
    }

    @Test
    @DisplayName("RescheduleBookingRequest stores date and time")
    void rescheduleBookingRequestFields() {
        RescheduleBookingRequest req = new RescheduleBookingRequest("2025-07-01", "14:00");
        assertEquals("2025-07-01", req.getDate());
        assertEquals("14:00", req.getTime());
    }

    @Test
    @DisplayName("FeedbackRequest stores rating and comment")
    void feedbackRequestFields() {
        FeedbackRequest req = new FeedbackRequest(5, "Great job!");
        assertEquals(5, req.getRating());
        assertEquals("Great job!", req.getComment());
    }

    @Test
    @DisplayName("TipRequest stores amount and paymentMethodId")
    void tipRequestFields() {
        TipRequest req = new TipRequest(15.50f, 8);
        assertEquals(15.50f, req.getAmount(), 0.001f);
        assertEquals(8, req.getPaymentMethodId());
    }

    @Test
    @DisplayName("BlacklistRequest stores cleanerId and reason")
    void blacklistRequestFields() {
        BlacklistRequest req = new BlacklistRequest(3, "Unprofessional");
        assertEquals(3, req.getCleanerId());
        assertEquals("Unprofessional", req.getReason());
    }

    @Test
    @DisplayName("VerifyJwtRequest stores token")
    void verifyJwtRequestFields() {
        VerifyJwtRequest req = new VerifyJwtRequest("my.jwt.token");
        assertEquals("my.jwt.token", req.getToken());
    }

    @Test
    @DisplayName("ICalRequest stores icalLink")
    void icalRequestFields() {
        ICalRequest req = new ICalRequest("https://cal.example.com/ical.ics");
        assertEquals("https://cal.example.com/ical.ics", req.getIcalLink());
    }
}
