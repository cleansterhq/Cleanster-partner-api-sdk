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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the extended methods added to BookingService, PropertyService,
 * ChecklistService, and OtherService.
 */
@ExtendWith(MockitoExtension.class)
class ServiceExtensionsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock private SOAPTransport transport;
    private CleansterSOAPClient client;

    @BeforeEach
    void setUp() {
        lenient().when(transport.getObjectMapper()).thenReturn(MAPPER);
        lenient().when(transport.extractData(any())).thenAnswer(inv -> inv.getArgument(0));
        client = new CleansterSOAPClient(transport);
    }

    // =========================================================================
    // Booking extensions
    // =========================================================================

    @Test
    @DisplayName("removeAssignedCleaner calls DELETE /v1/bookings/{id}/cleaner")
    void removeAssignedCleanerCallsCorrectPath() {
        when(transport.delete("/v1/bookings/16459/cleaner")).thenReturn(okNode());
        client.removeAssignedCleaner(16459L);
        verify(transport).delete("/v1/bookings/16459/cleaner");
    }

    @Test
    @DisplayName("removeAssignedCleaner returns success response")
    void removeAssignedCleanerReturnsSuccess() {
        when(transport.delete("/v1/bookings/16459/cleaner")).thenReturn(okNode());
        assertTrue(client.removeAssignedCleaner(16459L).isSuccess());
    }

    @Test
    @DisplayName("adjustHours calls POST /v1/bookings/{id}/hours")
    void adjustHoursCallsCorrectPath() {
        when(transport.post(eq("/v1/bookings/16459/hours"), any())).thenReturn(okNode());
        client.adjustHours(16459L, 1.0);
        verify(transport).post(eq("/v1/bookings/16459/hours"), any());
    }

    @Test
    @DisplayName("adjustHours returns success response")
    void adjustHoursReturnsSuccess() {
        when(transport.post(eq("/v1/bookings/16459/hours"), any())).thenReturn(okNode());
        assertTrue(client.adjustHours(16459L, 1.0).isSuccess());
    }

    @Test
    @DisplayName("payExpenses calls POST /v1/bookings/{id}/expenses")
    void payExpensesCallsCorrectPath() {
        when(transport.post(eq("/v1/bookings/16459/expenses"), any())).thenReturn(okNode());
        client.payExpenses(16459L, 10L);
        verify(transport).post(eq("/v1/bookings/16459/expenses"), any());
    }

    @Test
    @DisplayName("payExpenses returns success response")
    void payExpensesReturnsSuccess() {
        when(transport.post(eq("/v1/bookings/16459/expenses"), any())).thenReturn(okNode());
        assertTrue(client.payExpenses(16459L, 10L).isSuccess());
    }

    @Test
    @DisplayName("getBookingInspection calls GET /v1/bookings/{id}/inspection")
    void getBookingInspectionCallsCorrectPath() {
        when(transport.get("/v1/bookings/16459/inspection")).thenReturn(MAPPER.createObjectNode());
        client.getBookingInspection(16459L);
        verify(transport).get("/v1/bookings/16459/inspection");
    }

    @Test
    @DisplayName("getBookingInspectionDetails calls GET /v1/bookings/{id}/inspection/details")
    void getBookingInspectionDetailsCallsCorrectPath() {
        when(transport.get("/v1/bookings/16459/inspection/details")).thenReturn(MAPPER.createObjectNode());
        client.getBookingInspectionDetails(16459L);
        verify(transport).get("/v1/bookings/16459/inspection/details");
    }

    @Test
    @DisplayName("assignChecklistToBooking calls PUT /v1/bookings/{id}/checklist/{checklistId}")
    void assignChecklistToBookingCallsCorrectPath() {
        when(transport.put(eq("/v1/bookings/16459/checklist/105"), any())).thenReturn(okNode());
        client.assignChecklistToBooking(16459L, 105L);
        verify(transport).put(eq("/v1/bookings/16459/checklist/105"), any());
    }

    @Test
    @DisplayName("assignChecklistToBooking returns success response")
    void assignChecklistToBookingReturnsSuccess() {
        when(transport.put(eq("/v1/bookings/16459/checklist/105"), any())).thenReturn(okNode());
        assertTrue(client.assignChecklistToBooking(16459L, 105L).isSuccess());
    }

    @Test
    @DisplayName("submitFeedback calls POST /v1/bookings/{id}/feedback")
    void submitFeedbackCallsCorrectPath() {
        when(transport.post(eq("/v1/bookings/16459/feedback"), any())).thenReturn(okNode());
        client.submitFeedback(16459L, 5, "Great job!");
        verify(transport).post(eq("/v1/bookings/16459/feedback"), any());
    }

    @Test
    @DisplayName("submitFeedback returns success response")
    void submitFeedbackReturnsSuccess() {
        when(transport.post(eq("/v1/bookings/16459/feedback"), any())).thenReturn(okNode());
        assertTrue(client.submitFeedback(16459L, 5, null).isSuccess());
    }

    @Test
    @DisplayName("addTip calls POST /v1/bookings/{id}/tip")
    void addTipCallsCorrectPath() {
        when(transport.post(eq("/v1/bookings/16459/tip"), any())).thenReturn(okNode());
        client.addTip(16459L, 20.0, 10L);
        verify(transport).post(eq("/v1/bookings/16459/tip"), any());
    }

    @Test
    @DisplayName("addTip returns success response")
    void addTipReturnsSuccess() {
        when(transport.post(eq("/v1/bookings/16459/tip"), any())).thenReturn(okNode());
        assertTrue(client.addTip(16459L, 20.0, 10L).isSuccess());
    }

    @Test
    @DisplayName("deleteMessage calls DELETE /v1/bookings/{id}/chat/{messageId}")
    void deleteMessageCallsCorrectPath() {
        when(transport.delete("/v1/bookings/16459/chat/msg123")).thenReturn(okNode());
        client.deleteMessage(16459L, "msg123");
        verify(transport).delete("/v1/bookings/16459/chat/msg123");
    }

    @Test
    @DisplayName("deleteMessage returns success response")
    void deleteMessageReturnsSuccess() {
        when(transport.delete("/v1/bookings/16459/chat/msg123")).thenReturn(okNode());
        assertTrue(client.deleteMessage(16459L, "msg123").isSuccess());
    }

    // =========================================================================
    // Property extensions
    // =========================================================================

    @Test
    @DisplayName("updateProperty calls PUT /v1/properties/{id}")
    void updatePropertyCallsCorrectPath() {
        when(transport.put(eq("/v1/properties/42"), any())).thenReturn(propertyNode(42L));
        CreatePropertyRequest req = new CreatePropertyRequest().setAddress("New Address");
        client.updateProperty(42L, req);
        verify(transport).put(eq("/v1/properties/42"), any());
    }

    @Test
    @DisplayName("updateProperty returns updated Property")
    void updatePropertyReturnsProperty() {
        when(transport.put(eq("/v1/properties/42"), any())).thenReturn(propertyNode(42L));
        Property result = client.updateProperty(42L, new CreatePropertyRequest());
        assertEquals(42L, result.getId());
    }

    @Test
    @DisplayName("updateAdditionalInformation calls PUT /v1/properties/{id}/additional-information")
    void updateAdditionalInformationCallsCorrectPath() {
        when(transport.put(eq("/v1/properties/42/additional-information"), any())).thenReturn(okNode());
        client.updateAdditionalInformation(42L, Map.of("notes", "Gate code 1234"));
        verify(transport).put(eq("/v1/properties/42/additional-information"), any());
    }

    @Test
    @DisplayName("updateAdditionalInformation returns success")
    void updateAdditionalInformationReturnsSuccess() {
        when(transport.put(eq("/v1/properties/42/additional-information"), any())).thenReturn(okNode());
        assertTrue(client.updateAdditionalInformation(42L, Map.of()).isSuccess());
    }

    @Test
    @DisplayName("enableOrDisableProperty calls POST /v1/properties/{id}/enable-disable")
    void enableOrDisablePropertyCallsCorrectPath() {
        when(transport.post(eq("/v1/properties/42/enable-disable"), any())).thenReturn(okNode());
        client.enableOrDisableProperty(42L, false);
        verify(transport).post(eq("/v1/properties/42/enable-disable"), any());
    }

    @Test
    @DisplayName("deleteProperty calls DELETE /v1/properties/{id}")
    void deletePropertyCallsCorrectPath() {
        when(transport.delete("/v1/properties/42")).thenReturn(okNode());
        client.deleteProperty(42L);
        verify(transport).delete("/v1/properties/42");
    }

    @Test
    @DisplayName("deleteProperty returns success")
    void deletePropertyReturnsSuccess() {
        when(transport.delete("/v1/properties/42")).thenReturn(okNode());
        assertTrue(client.deleteProperty(42L).isSuccess());
    }

    @Test
    @DisplayName("getPropertyCleaners calls GET /v1/properties/{id}/cleaners")
    void getPropertyCleanersCallsCorrectPath() {
        when(transport.get("/v1/properties/42/cleaners")).thenReturn(MAPPER.createArrayNode());
        client.getPropertyCleaners(42L);
        verify(transport).get("/v1/properties/42/cleaners");
    }

    @Test
    @DisplayName("getPropertyCleaners returns list")
    void getPropertyCleanersReturnsList() {
        when(transport.get("/v1/properties/42/cleaners")).thenReturn(MAPPER.createArrayNode());
        List<Cleaner> result = client.getPropertyCleaners(42L);
        assertNotNull(result);
    }

    @Test
    @DisplayName("assignCleanerToProperty calls POST /v1/properties/{id}/cleaners")
    void assignCleanerToPropertyCallsCorrectPath() {
        when(transport.post(eq("/v1/properties/42/cleaners"), any())).thenReturn(okNode());
        client.assignCleanerToProperty(42L, 789L);
        verify(transport).post(eq("/v1/properties/42/cleaners"), any());
    }

    @Test
    @DisplayName("unassignCleanerFromProperty calls DELETE /v1/properties/{id}/cleaners/{cleanerId}")
    void unassignCleanerFromPropertyCallsCorrectPath() {
        when(transport.delete("/v1/properties/42/cleaners/789")).thenReturn(okNode());
        client.unassignCleanerFromProperty(42L, 789L);
        verify(transport).delete("/v1/properties/42/cleaners/789");
    }

    @Test
    @DisplayName("addICalLink calls POST /v1/properties/{id}/ical")
    void addICalLinkCallsCorrectPath() {
        when(transport.post(eq("/v1/properties/42/ical"), any())).thenReturn(okNode());
        client.addICalLink(42L, "https://example.com/cal.ics");
        verify(transport).post(eq("/v1/properties/42/ical"), any());
    }

    @Test
    @DisplayName("getICalLink calls GET /v1/properties/{id}/ical")
    void getICalLinkCallsCorrectPath() {
        when(transport.get("/v1/properties/42/ical")).thenReturn(MAPPER.createObjectNode());
        client.getICalLink(42L);
        verify(transport).get("/v1/properties/42/ical");
    }

    @Test
    @DisplayName("removeICalLink calls DELETE /v1/properties/{id}/ical")
    void removeICalLinkCallsCorrectPath() {
        when(transport.delete("/v1/properties/42/ical")).thenReturn(okNode());
        client.removeICalLink(42L, "https://example.com/cal.ics");
        verify(transport).delete("/v1/properties/42/ical");
    }

    @Test
    @DisplayName("setDefaultChecklist calls PUT /v1/properties/{id}/checklist/{checklistId}")
    void setDefaultChecklistCallsCorrectPath() {
        when(transport.put(contains("/v1/properties/42/checklist/105"), any())).thenReturn(okNode());
        client.setDefaultChecklist(42L, 105L, true);
        verify(transport).put(contains("/v1/properties/42/checklist/105"), any());
    }

    @Test
    @DisplayName("setDefaultChecklist returns success")
    void setDefaultChecklistReturnsSuccess() {
        when(transport.put(contains("/v1/properties/42/checklist/105"), any())).thenReturn(okNode());
        assertTrue(client.setDefaultChecklist(42L, 105L, false).isSuccess());
    }

    // =========================================================================
    // Checklist extensions
    // =========================================================================

    @Test
    @DisplayName("updateChecklist calls PUT /v1/checklist/{id}")
    void updateChecklistCallsCorrectPath() {
        when(transport.put(eq("/v1/checklist/105"), any())).thenReturn(checklistNode(105L));
        client.updateChecklist(105L, "Updated Name", Arrays.asList("Item 1", "Item 2"));
        verify(transport).put(eq("/v1/checklist/105"), any());
    }

    @Test
    @DisplayName("updateChecklist returns updated Checklist")
    void updateChecklistReturnsChecklist() {
        when(transport.put(eq("/v1/checklist/105"), any())).thenReturn(checklistNode(105L));
        Checklist result = client.updateChecklist(105L, "Updated Name", null);
        assertEquals(105L, result.getId());
    }

    // =========================================================================
    // Other extensions
    // =========================================================================

    @Test
    @DisplayName("getPlans calls GET /v1/plans")
    void getPlansCallsCorrectPath() {
        when(transport.get(contains("/v1/plans"))).thenReturn(MAPPER.createArrayNode());
        client.getPlans(42L);
        verify(transport).get(contains("/v1/plans?propertyId=42"));
    }

    @Test
    @DisplayName("getPlans returns non-null result")
    void getPlansReturnsResult() {
        when(transport.get(contains("/v1/plans"))).thenReturn(MAPPER.createArrayNode());
        assertNotNull(client.getPlans(42L));
    }

    @Test
    @DisplayName("getRecommendedHours calls GET /v1/recommended-hours")
    void getRecommendedHoursCallsCorrectPath() {
        when(transport.get(contains("/v1/recommended-hours"))).thenReturn(MAPPER.createObjectNode());
        client.getRecommendedHours(42L, 2, 3);
        verify(transport).get(contains("/v1/recommended-hours"));
    }

    @Test
    @DisplayName("getRecommendedHours returns non-null result")
    void getRecommendedHoursReturnsResult() {
        when(transport.get(contains("/v1/recommended-hours"))).thenReturn(MAPPER.createObjectNode());
        assertNotNull(client.getRecommendedHours(42L, 2, 3));
    }

    @Test
    @DisplayName("getCostEstimate calls POST /v1/cost-estimate")
    void getCostEstimateCallsCorrectPath() {
        when(transport.post(eq("/v1/cost-estimate"), any())).thenReturn(MAPPER.createObjectNode());
        client.getCostEstimate(Map.of("service_type", "standard"));
        verify(transport).post(eq("/v1/cost-estimate"), any());
    }

    @Test
    @DisplayName("getCleaningExtras calls GET /v1/cleaning-extras/{serviceId}")
    void getCleaningExtrasCallsCorrectPath() {
        when(transport.get(contains("/v1/cleaning-extras"))).thenReturn(MAPPER.createArrayNode());
        client.getCleaningExtras(3L);
        verify(transport).get(contains("/v1/cleaning-extras/3"));
    }

    @Test
    @DisplayName("getAvailableCleaners calls POST /v1/available-cleaners")
    void getAvailableCleanersCallsCorrectPath() {
        when(transport.post(eq("/v1/available-cleaners"), any())).thenReturn(MAPPER.createArrayNode());
        client.getAvailableCleaners(Map.of("date", "2025-07-01"));
        verify(transport).post(eq("/v1/available-cleaners"), any());
    }

    @Test
    @DisplayName("getCoupons calls GET /v1/coupons")
    void getCouponsCallsCorrectPath() {
        when(transport.get("/v1/coupons")).thenReturn(MAPPER.createArrayNode());
        client.getCoupons();
        verify(transport).get("/v1/coupons");
    }

    @Test
    @DisplayName("getCoupons returns non-null result")
    void getCouponsReturnsResult() {
        when(transport.get("/v1/coupons")).thenReturn(MAPPER.createArrayNode());
        assertNotNull(client.getCoupons());
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private JsonNode okNode() {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("status", 200);
        n.put("message", "OK");
        return n;
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

    private JsonNode checklistNode(long id) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("id", id);
        n.put("name", "Deep Clean");
        return n;
    }
}
