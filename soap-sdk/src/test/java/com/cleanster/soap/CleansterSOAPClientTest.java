package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleansterSOAPClientTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock private SOAPTransport transport;
    private CleansterSOAPClient client;

    @BeforeEach
    void setUp() {
        when(transport.getObjectMapper()).thenReturn(MAPPER);
        client = new CleansterSOAPClient(transport);
    }

    // =========================================================================
    // SOAPTransport construction
    // =========================================================================

    @Test
    @DisplayName("SOAPTransport rejects null API key")
    void transportRejectsNullApiKey() {
        assertThrows(IllegalArgumentException.class, () -> new SOAPTransport(null));
    }

    @Test
    @DisplayName("SOAPTransport rejects blank API key")
    void transportRejectsBlankApiKey() {
        assertThrows(IllegalArgumentException.class, () -> new SOAPTransport("  "));
    }

    // =========================================================================
    // GetBooking
    // =========================================================================

    @Test
    @DisplayName("getBooking calls GET /v1/bookings/{id}")
    void getBookingCallsCorrectPath() {
        when(transport.get("/v1/bookings/16459")).thenReturn(bookingNode(16459L, "scheduled"));
        Booking result = client.getBooking(16459);
        verify(transport).get("/v1/bookings/16459");
        assertNotNull(result);
    }

    @Test
    @DisplayName("getBooking returns correct booking ID")
    void getBookingReturnsCorrectId() {
        when(transport.get("/v1/bookings/16459")).thenReturn(bookingNode(16459L, "scheduled"));
        Booking result = client.getBooking(16459);
        assertEquals(16459L, result.getId());
    }

    @Test
    @DisplayName("getBooking returns booking status")
    void getBookingReturnsStatus() {
        when(transport.get("/v1/bookings/16459")).thenReturn(bookingNode(16459L, "completed"));
        Booking result = client.getBooking(16459);
        assertEquals("completed", result.getStatus());
    }

    // =========================================================================
    // ListBookings
    // =========================================================================

    @Test
    @DisplayName("listBookings builds correct query string")
    void listBookingsBuildsPath() {
        when(transport.get(contains("/v1/bookings"))).thenReturn(bookingArrayNode());
        ListBookingsRequest req = new ListBookingsRequest().setStatus("scheduled").setPage(1).setPerPage(10);
        client.listBookings(req);
        verify(transport).get(contains("status=scheduled"));
    }

    @Test
    @DisplayName("listBookings returns a list")
    void listBookingsReturnsList() {
        when(transport.get(contains("/v1/bookings"))).thenReturn(bookingArrayNode());
        List<Booking> results = client.listBookings(new ListBookingsRequest());
        assertNotNull(results);
    }

    // =========================================================================
    // CreateBooking
    // =========================================================================

    @Test
    @DisplayName("createBooking calls POST /v1/bookings")
    void createBookingCallsCorrectPath() {
        when(transport.post(eq("/v1/bookings"), any())).thenReturn(bookingNode(16460L, "scheduled"));
        CreateBookingRequest req = new CreateBookingRequest()
                .setPropertyId(42L)
                .setScheduledAt("2025-06-01T10:00:00Z")
                .setDurationHours(3.0);
        client.createBooking(req);
        verify(transport).post(eq("/v1/bookings"), any());
    }

    @Test
    @DisplayName("createBooking returns new booking")
    void createBookingReturnsBooking() {
        when(transport.post(eq("/v1/bookings"), any())).thenReturn(bookingNode(16460L, "scheduled"));
        CreateBookingRequest req = new CreateBookingRequest()
                .setPropertyId(42L)
                .setScheduledAt("2025-06-01T10:00:00Z")
                .setDurationHours(3.0);
        Booking result = client.createBooking(req);
        assertEquals(16460L, result.getId());
        assertEquals("scheduled", result.getStatus());
    }

    // =========================================================================
    // CancelBooking
    // =========================================================================

    @Test
    @DisplayName("cancelBooking calls POST /v1/bookings/{id}/cancel")
    void cancelBookingCallsCorrectPath() {
        when(transport.post(eq("/v1/bookings/16459/cancel"), any()))
                .thenReturn(apiResponseNode(200, "Cancelled"));
        client.cancelBooking(16459, "Customer request");
        verify(transport).post(eq("/v1/bookings/16459/cancel"), any());
    }

    @Test
    @DisplayName("cancelBooking returns success response")
    void cancelBookingReturnsResponse() {
        when(transport.post(eq("/v1/bookings/16459/cancel"), any()))
                .thenReturn(apiResponseNode(200, "Cancelled"));
        ApiResponse resp = client.cancelBooking(16459, null);
        assertTrue(resp.isSuccess());
    }

    // =========================================================================
    // RescheduleBooking
    // =========================================================================

    @Test
    @DisplayName("rescheduleBooking calls POST /v1/bookings/{id}/reschedule")
    void rescheduleBookingCallsCorrectPath() {
        when(transport.post(eq("/v1/bookings/16459/reschedule"), any()))
                .thenReturn(bookingNode(16459L, "scheduled"));
        RescheduleBookingRequest req = new RescheduleBookingRequest()
                .setBookingId(16459L)
                .setScheduledAt("2025-07-01T10:00:00Z");
        client.rescheduleBooking(req);
        verify(transport).post(eq("/v1/bookings/16459/reschedule"), any());
    }

    @Test
    @DisplayName("rescheduleBooking returns updated booking")
    void rescheduleBookingReturnsBooking() {
        when(transport.post(eq("/v1/bookings/16459/reschedule"), any()))
                .thenReturn(bookingNode(16459L, "scheduled"));
        RescheduleBookingRequest req = new RescheduleBookingRequest()
                .setBookingId(16459L)
                .setScheduledAt("2025-07-01T10:00:00Z");
        Booking result = client.rescheduleBooking(req);
        assertEquals(16459L, result.getId());
    }

    // =========================================================================
    // AssignCleaner
    // =========================================================================

    @Test
    @DisplayName("assignCleaner calls POST /v1/bookings/{id}/assign-cleaner")
    void assignCleanerCallsCorrectPath() {
        when(transport.post(eq("/v1/bookings/16459/assign-cleaner"), any()))
                .thenReturn(bookingNode(16459L, "scheduled"));
        client.assignCleaner(16459, 789);
        verify(transport).post(eq("/v1/bookings/16459/assign-cleaner"), any());
    }

    @Test
    @DisplayName("assignCleaner returns booking with cleaner")
    void assignCleanerReturnsBooking() {
        when(transport.post(eq("/v1/bookings/16459/assign-cleaner"), any()))
                .thenReturn(bookingNode(16459L, "scheduled"));
        Booking result = client.assignCleaner(16459, 789);
        assertNotNull(result);
    }

    // =========================================================================
    // GetProperty
    // =========================================================================

    @Test
    @DisplayName("getProperty calls GET /v1/properties/{id}")
    void getPropertyCallsCorrectPath() {
        when(transport.get("/v1/properties/42")).thenReturn(propertyNode(42L));
        client.getProperty(42);
        verify(transport).get("/v1/properties/42");
    }

    @Test
    @DisplayName("getProperty returns correct property")
    void getPropertyReturnsProperty() {
        when(transport.get("/v1/properties/42")).thenReturn(propertyNode(42L));
        Property result = client.getProperty(42);
        assertEquals(42L, result.getId());
    }

    // =========================================================================
    // ListProperties
    // =========================================================================

    @Test
    @DisplayName("listProperties calls GET /v1/properties")
    void listPropertiesCallsCorrectPath() {
        when(transport.get(contains("/v1/properties"))).thenReturn(MAPPER.createArrayNode());
        client.listProperties(1, 20);
        verify(transport).get(contains("/v1/properties?page=1"));
    }

    @Test
    @DisplayName("listProperties returns a list")
    void listPropertiesReturnsList() {
        when(transport.get(contains("/v1/properties"))).thenReturn(MAPPER.createArrayNode());
        List<Property> result = client.listProperties(1, 20);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // CreateProperty
    // =========================================================================

    @Test
    @DisplayName("createProperty calls POST /v1/properties")
    void createPropertyCallsCorrectPath() {
        when(transport.post(eq("/v1/properties"), any())).thenReturn(propertyNode(43L));
        CreatePropertyRequest req = new CreatePropertyRequest()
                .setAddress("456 Oak Ave").setCity("Savannah").setState("GA").setZip("31401");
        client.createProperty(req);
        verify(transport).post(eq("/v1/properties"), any());
    }

    @Test
    @DisplayName("createProperty returns new property")
    void createPropertyReturnsProperty() {
        when(transport.post(eq("/v1/properties"), any())).thenReturn(propertyNode(43L));
        CreatePropertyRequest req = new CreatePropertyRequest()
                .setAddress("456 Oak Ave").setCity("Savannah").setState("GA").setZip("31401");
        Property result = client.createProperty(req);
        assertEquals(43L, result.getId());
    }

    // =========================================================================
    // ListCleaners
    // =========================================================================

    @Test
    @DisplayName("listCleaners calls GET /v1/cleaners")
    void listCleanersCallsCorrectPath() {
        when(transport.get(contains("/v1/cleaners"))).thenReturn(MAPPER.createArrayNode());
        client.listCleaners("active", 1, 20);
        verify(transport).get(contains("/v1/cleaners"));
    }

    @Test
    @DisplayName("listCleaners with status filter includes status param")
    void listCleanersIncludesStatusParam() {
        when(transport.get(contains("status=active"))).thenReturn(MAPPER.createArrayNode());
        client.listCleaners("active", 1, 20);
        verify(transport).get(contains("status=active"));
    }

    // =========================================================================
    // GetCleaner
    // =========================================================================

    @Test
    @DisplayName("getCleaner calls GET /v1/cleaners/{id}")
    void getCleanerCallsCorrectPath() {
        when(transport.get("/v1/cleaners/789")).thenReturn(cleanerNode(789L));
        client.getCleaner(789);
        verify(transport).get("/v1/cleaners/789");
    }

    @Test
    @DisplayName("getCleaner returns correct cleaner")
    void getCleanerReturnsCleaner() {
        when(transport.get("/v1/cleaners/789")).thenReturn(cleanerNode(789L));
        Cleaner result = client.getCleaner(789);
        assertEquals(789L, result.getId());
    }

    // =========================================================================
    // ListChecklists
    // =========================================================================

    @Test
    @DisplayName("listChecklists calls GET /v1/checklists")
    void listChecklistsCallsCorrectPath() {
        when(transport.get(contains("/v1/checklists"))).thenReturn(MAPPER.createArrayNode());
        client.listChecklists(1, 20);
        verify(transport).get(contains("/v1/checklists?page=1"));
    }

    @Test
    @DisplayName("listChecklists returns empty list when no checklists")
    void listChecklistsReturnsEmptyList() {
        when(transport.get(contains("/v1/checklists"))).thenReturn(MAPPER.createArrayNode());
        List<Checklist> result = client.listChecklists(1, 20);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // GetChecklist
    // =========================================================================

    @Test
    @DisplayName("getChecklist calls GET /v1/checklists/{id}")
    void getChecklistCallsCorrectPath() {
        when(transport.get("/v1/checklists/105")).thenReturn(checklistNode(105L));
        client.getChecklist(105);
        verify(transport).get("/v1/checklists/105");
    }

    @Test
    @DisplayName("getChecklist returns correct checklist")
    void getChecklistReturnsChecklist() {
        when(transport.get("/v1/checklists/105")).thenReturn(checklistNode(105L));
        Checklist result = client.getChecklist(105);
        assertEquals(105L, result.getId());
    }

    // =========================================================================
    // CreateChecklist
    // =========================================================================

    @Test
    @DisplayName("createChecklist calls POST /v1/checklists")
    void createChecklistCallsCorrectPath() {
        when(transport.post(eq("/v1/checklists"), any())).thenReturn(checklistNode(106L));
        client.createChecklist("Bathroom Deep Clean", Arrays.asList("Scrub tub", "Clean mirror"));
        verify(transport).post(eq("/v1/checklists"), any());
    }

    @Test
    @DisplayName("createChecklist returns new checklist")
    void createChecklistReturnsChecklist() {
        when(transport.post(eq("/v1/checklists"), any())).thenReturn(checklistNode(106L));
        Checklist result = client.createChecklist("Test", null);
        assertEquals(106L, result.getId());
    }

    // =========================================================================
    // DeleteChecklist
    // =========================================================================

    @Test
    @DisplayName("deleteChecklist calls DELETE /v1/checklists/{id}")
    void deleteChecklistCallsCorrectPath() {
        when(transport.delete("/v1/checklists/105")).thenReturn(apiResponseNode(200, "Deleted"));
        client.deleteChecklist(105);
        verify(transport).delete("/v1/checklists/105");
    }

    @Test
    @DisplayName("deleteChecklist returns success response")
    void deleteChecklistReturnsSuccess() {
        when(transport.delete("/v1/checklists/105")).thenReturn(apiResponseNode(200, "Deleted"));
        ApiResponse resp = client.deleteChecklist(105);
        assertTrue(resp.isSuccess());
    }

    // =========================================================================
    // UploadChecklistImage
    // =========================================================================

    @Test
    @DisplayName("uploadChecklistImage calls POST multipart /v1/checklist/{id}/upload")
    void uploadChecklistImageCallsCorrectPath() {
        when(transport.postMultipart("/v1/checklist/105/upload", new byte[]{1, 2, 3}, "photo.jpg"))
                .thenReturn(apiResponseNode(200, "Uploaded"));
        client.uploadChecklistImage(105, new byte[]{1, 2, 3}, "photo.jpg");
        verify(transport).postMultipart("/v1/checklist/105/upload", new byte[]{1, 2, 3}, "photo.jpg");
    }

    @Test
    @DisplayName("uploadChecklistImage returns success response")
    void uploadChecklistImageReturnsSuccess() {
        when(transport.postMultipart(anyString(), any(), anyString()))
                .thenReturn(apiResponseNode(200, "Uploaded"));
        ApiResponse resp = client.uploadChecklistImage(105, new byte[]{}, "image.png");
        assertTrue(resp.isSuccess());
    }

    // =========================================================================
    // GetServices
    // =========================================================================

    @Test
    @DisplayName("getServices calls GET /v1/services")
    void getServicesCallsCorrectPath() {
        when(transport.get("/v1/services")).thenReturn(MAPPER.createArrayNode());
        client.getServices();
        verify(transport).get("/v1/services");
    }

    @Test
    @DisplayName("getServices returns list")
    void getServicesReturnsList() {
        when(transport.get("/v1/services")).thenReturn(MAPPER.createArrayNode());
        List<ServiceType> result = client.getServices();
        assertNotNull(result);
    }

    // =========================================================================
    // SendMessage / GetChat
    // =========================================================================

    @Test
    @DisplayName("sendMessage calls POST /v1/bookings/{id}/chat")
    void sendMessageCallsCorrectPath() {
        when(transport.post(eq("/v1/bookings/16459/chat"), any()))
                .thenReturn(chatMessageNode(1001L, 16459L));
        client.sendMessage(16459, "Please use eco-friendly products.");
        verify(transport).post(eq("/v1/bookings/16459/chat"), any());
    }

    @Test
    @DisplayName("sendMessage returns ChatMessage")
    void sendMessageReturnsChatMessage() {
        when(transport.post(eq("/v1/bookings/16459/chat"), any()))
                .thenReturn(chatMessageNode(1001L, 16459L));
        ChatMessage result = client.sendMessage(16459, "Hello");
        assertEquals(1001L, result.getId());
    }

    @Test
    @DisplayName("getChat calls GET /v1/bookings/{id}/chat")
    void getChatCallsCorrectPath() {
        when(transport.get("/v1/bookings/16459/chat")).thenReturn(MAPPER.createArrayNode());
        client.getChat(16459);
        verify(transport).get("/v1/bookings/16459/chat");
    }

    @Test
    @DisplayName("getChat returns list of messages")
    void getChatReturnsList() {
        when(transport.get("/v1/bookings/16459/chat")).thenReturn(MAPPER.createArrayNode());
        List<ChatMessage> result = client.getChat(16459);
        assertNotNull(result);
    }

    // =========================================================================
    // ApiResponse helpers
    // =========================================================================

    @Test
    @DisplayName("ApiResponse.isSuccess returns true for 2xx status")
    void apiResponseIsSuccessForOk() {
        assertTrue(new ApiResponse(200, "OK").isSuccess());
        assertTrue(new ApiResponse(201, "Created").isSuccess());
    }

    @Test
    @DisplayName("ApiResponse.isSuccess returns false for 4xx status")
    void apiResponseIsFailureFor4xx() {
        assertFalse(new ApiResponse(404, "Not Found").isSuccess());
        assertFalse(new ApiResponse(422, "Unprocessable Entity").isSuccess());
    }

    // =========================================================================
    // Helper builders
    // =========================================================================

    private JsonNode bookingNode(long id, String status) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("id", id);
        n.put("status", status);
        n.put("scheduled_at", "2025-06-01T10:00:00Z");
        n.put("duration_hours", 3.0);
        return n;
    }

    private JsonNode bookingArrayNode() {
        return MAPPER.createArrayNode();
    }

    private JsonNode propertyNode(long id) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("id", id);
        n.put("address", "123 Main St");
        n.put("city", "Atlanta");
        n.put("state", "GA");
        n.put("zip", "30301");
        return n;
    }

    private JsonNode cleanerNode(long id) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("id", id);
        n.put("name", "Jane Smith");
        n.put("status", "active");
        return n;
    }

    private JsonNode checklistNode(long id) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("id", id);
        n.put("name", "Deep Clean");
        return n;
    }

    private JsonNode apiResponseNode(int status, String message) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("status", status);
        n.put("message", message);
        return n;
    }

    private JsonNode chatMessageNode(long id, long bookingId) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("id", id);
        n.put("booking_id", bookingId);
        n.put("message", "Hello");
        n.put("sender", "partner");
        n.put("sent_at", "2025-06-01T11:00:00Z");
        return n;
    }
}
