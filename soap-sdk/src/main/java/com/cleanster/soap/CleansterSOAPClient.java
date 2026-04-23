package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Main entry point for the Cleanster SOAP SDK.
 *
 * <p>Provides a high-level SOAP-style API over the Cleanster Partner REST API.
 * Every method corresponds to one SOAP operation defined in cleanster.wsdl.
 *
 * <h2>API Sections</h2>
 * <ul>
 *   <li>Bookings — 14 operations (list, create, cancel, reschedule, assign, inspect, chat, tip, feedback...)</li>
 *   <li>Properties — 11 operations (CRUD, iCal, cleaners, checklist assignment...)</li>
 *   <li>Cleaners — 2 operations (list, get)</li>
 *   <li>Checklists — 5 operations (CRUD + upload)</li>
 *   <li>Other — 7 operations (services, plans, cost estimate, recommended hours, extras, cleaners, coupons)</li>
 *   <li>Users — 3 operations (create, access token, verify JWT)</li>
 *   <li>Blacklist — 3 operations (list, add, remove)</li>
 *   <li>Payment Methods — 6 operations (setup, PayPal, add, list, delete, default)</li>
 *   <li>Webhooks — 4 operations (list, create, update, delete)</li>
 * </ul>
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * CleansterSOAPClient client = new CleansterSOAPClient("your-api-key");
 *
 * // Bookings
 * Booking booking = client.getBooking(16459);
 * List<Booking> all = client.listBookings(new ListBookingsRequest().setStatus("scheduled"));
 * Booking created = client.createBooking(new CreateBookingRequest()
 *     .setPropertyId(42L).setScheduledAt("2025-07-01T10:00:00Z").setDurationHours(3.0));
 *
 * // Payment methods
 * List<PaymentMethod> methods = client.getPaymentMethods();
 *
 * // Webhooks
 * Webhook wh = client.createWebhook("https://example.com/hook", "booking.completed");
 * }</pre>
 */
public class CleansterSOAPClient {

    private final BookingService       bookingService;
    private final PropertyService      propertyService;
    private final CleanerService       cleanerService;
    private final ChecklistService     checklistService;
    private final OtherService         otherService;
    private final UserService          userService;
    private final BlacklistService     blacklistService;
    private final PaymentMethodService paymentMethodService;
    private final WebhookService       webhookService;

    public CleansterSOAPClient(String apiKey) {
        SOAPTransport transport = new SOAPTransport(apiKey);
        this.bookingService       = new BookingService(transport);
        this.propertyService      = new PropertyService(transport);
        this.cleanerService       = new CleanerService(transport);
        this.checklistService     = new ChecklistService(transport);
        this.otherService         = new OtherService(transport);
        this.userService          = new UserService(transport);
        this.blacklistService     = new BlacklistService(transport);
        this.paymentMethodService = new PaymentMethodService(transport);
        this.webhookService       = new WebhookService(transport);
    }

    CleansterSOAPClient(SOAPTransport transport) {
        this.bookingService       = new BookingService(transport);
        this.propertyService      = new PropertyService(transport);
        this.cleanerService       = new CleanerService(transport);
        this.checklistService     = new ChecklistService(transport);
        this.otherService         = new OtherService(transport);
        this.userService          = new UserService(transport);
        this.blacklistService     = new BlacklistService(transport);
        this.paymentMethodService = new PaymentMethodService(transport);
        this.webhookService       = new WebhookService(transport);
    }

    // =========================================================================
    // Bookings
    // =========================================================================

    public Booking getBooking(long bookingId) {
        return bookingService.getBooking(bookingId);
    }

    public List<Booking> listBookings(ListBookingsRequest request) {
        return bookingService.listBookings(request);
    }

    public Booking createBooking(CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    public ApiResponse cancelBooking(long bookingId, String reason) {
        return bookingService.cancelBooking(bookingId, reason);
    }

    public Booking rescheduleBooking(RescheduleBookingRequest request) {
        return bookingService.rescheduleBooking(request);
    }

    public Booking assignCleaner(long bookingId, long cleanerId) {
        return bookingService.assignCleaner(bookingId, cleanerId);
    }

    public ApiResponse removeAssignedCleaner(long bookingId) {
        return bookingService.removeAssignedCleaner(bookingId);
    }

    public ApiResponse adjustHours(long bookingId, double hours) {
        return bookingService.adjustHours(bookingId, hours);
    }

    public ApiResponse payExpenses(long bookingId, long paymentMethodId) {
        return bookingService.payExpenses(bookingId, paymentMethodId);
    }

    public JsonNode getBookingInspection(long bookingId) {
        return bookingService.getBookingInspection(bookingId);
    }

    public JsonNode getBookingInspectionDetails(long bookingId) {
        return bookingService.getBookingInspectionDetails(bookingId);
    }

    public ApiResponse assignChecklistToBooking(long bookingId, long checklistId) {
        return bookingService.assignChecklistToBooking(bookingId, checklistId);
    }

    public ApiResponse submitFeedback(long bookingId, int rating, String comment) {
        return bookingService.submitFeedback(bookingId, rating, comment);
    }

    public ApiResponse addTip(long bookingId, double amount, long paymentMethodId) {
        return bookingService.addTip(bookingId, amount, paymentMethodId);
    }

    public List<ChatMessage> getChat(long bookingId) {
        return bookingService.getChat(bookingId);
    }

    public ChatMessage sendMessage(long bookingId, String message) {
        return bookingService.sendMessage(bookingId, message);
    }

    public ApiResponse deleteMessage(long bookingId, String messageId) {
        return bookingService.deleteMessage(bookingId, messageId);
    }

    // =========================================================================
    // Properties
    // =========================================================================

    public Property getProperty(long propertyId) {
        return propertyService.getProperty(propertyId);
    }

    public List<Property> listProperties(int page, int perPage) {
        return propertyService.listProperties(page, perPage);
    }

    public Property createProperty(CreatePropertyRequest request) {
        return propertyService.createProperty(request);
    }

    public Property updateProperty(long propertyId, CreatePropertyRequest request) {
        return propertyService.updateProperty(propertyId, request);
    }

    public ApiResponse updateAdditionalInformation(long propertyId, Map<String, Object> info) {
        return propertyService.updateAdditionalInformation(propertyId, info);
    }

    public ApiResponse enableOrDisableProperty(long propertyId, boolean enabled) {
        return propertyService.enableOrDisableProperty(propertyId, enabled);
    }

    public ApiResponse deleteProperty(long propertyId) {
        return propertyService.deleteProperty(propertyId);
    }

    public List<Cleaner> getPropertyCleaners(long propertyId) {
        return propertyService.getPropertyCleaners(propertyId);
    }

    public ApiResponse assignCleanerToProperty(long propertyId, long cleanerId) {
        return propertyService.assignCleanerToProperty(propertyId, cleanerId);
    }

    public ApiResponse unassignCleanerFromProperty(long propertyId, long cleanerId) {
        return propertyService.unassignCleanerFromProperty(propertyId, cleanerId);
    }

    public ApiResponse addICalLink(long propertyId, String icalUrl) {
        return propertyService.addICalLink(propertyId, icalUrl);
    }

    public JsonNode getICalLink(long propertyId) {
        return propertyService.getICalLink(propertyId);
    }

    public ApiResponse removeICalLink(long propertyId, String icalUrl) {
        return propertyService.removeICalLink(propertyId, icalUrl);
    }

    public ApiResponse setDefaultChecklist(long propertyId, long checklistId, boolean updateUpcoming) {
        return propertyService.setDefaultChecklist(propertyId, checklistId, updateUpcoming);
    }

    // =========================================================================
    // Cleaners
    // =========================================================================

    public List<Cleaner> listCleaners(String status, int page, int perPage) {
        return cleanerService.listCleaners(status, page, perPage);
    }

    public Cleaner getCleaner(long cleanerId) {
        return cleanerService.getCleaner(cleanerId);
    }

    // =========================================================================
    // Checklists
    // =========================================================================

    public List<Checklist> listChecklists(int page, int perPage) {
        return checklistService.listChecklists(page, perPage);
    }

    public Checklist getChecklist(long checklistId) {
        return checklistService.getChecklist(checklistId);
    }

    public Checklist createChecklist(String name, List<String> items) {
        return checklistService.createChecklist(name, items);
    }

    public Checklist updateChecklist(long checklistId, String name, List<String> items) {
        return checklistService.updateChecklist(checklistId, name, items);
    }

    public ApiResponse deleteChecklist(long checklistId) {
        return checklistService.deleteChecklist(checklistId);
    }

    public ApiResponse uploadChecklistImage(long checklistId, byte[] imageData, String fileName) {
        return checklistService.uploadChecklistImage(checklistId, imageData, fileName);
    }

    // =========================================================================
    // Other
    // =========================================================================

    public List<ServiceType> getServices() {
        return otherService.getServices();
    }

    public JsonNode getPlans(long propertyId) {
        return otherService.getPlans(propertyId);
    }

    public JsonNode getRecommendedHours(long propertyId, int bathroomCount, int roomCount) {
        return otherService.getRecommendedHours(propertyId, bathroomCount, roomCount);
    }

    public JsonNode getCostEstimate(Map<String, Object> request) {
        return otherService.getCostEstimate(request);
    }

    public JsonNode getCleaningExtras(long serviceId) {
        return otherService.getCleaningExtras(serviceId);
    }

    public JsonNode getAvailableCleaners(Map<String, Object> request) {
        return otherService.getAvailableCleaners(request);
    }

    public JsonNode getCoupons() {
        return otherService.getCoupons();
    }

    // =========================================================================
    // Users
    // =========================================================================

    public User createUser(CreateUserRequest request) {
        return userService.createUser(request);
    }

    public User fetchAccessToken(long userId) {
        return userService.fetchAccessToken(userId);
    }

    public ApiResponse verifyJwt(String token) {
        return userService.verifyJwt(token);
    }

    // =========================================================================
    // Blacklist
    // =========================================================================

    public List<BlacklistEntry> listBlacklist() {
        return blacklistService.listBlacklist();
    }

    public ApiResponse addToBlacklist(long cleanerId, String reason) {
        return blacklistService.addToBlacklist(cleanerId, reason);
    }

    public ApiResponse removeFromBlacklist(long cleanerId) {
        return blacklistService.removeFromBlacklist(cleanerId);
    }

    // =========================================================================
    // Payment Methods
    // =========================================================================

    public JsonNode getSetupIntentDetails() {
        return paymentMethodService.getSetupIntentDetails();
    }

    public JsonNode getPaypalClientToken() {
        return paymentMethodService.getPaypalClientToken();
    }

    public PaymentMethod addPaymentMethod(String paymentMethodId) {
        return paymentMethodService.addPaymentMethod(paymentMethodId);
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethodService.getPaymentMethods();
    }

    public ApiResponse deletePaymentMethod(long paymentMethodId) {
        return paymentMethodService.deletePaymentMethod(paymentMethodId);
    }

    public ApiResponse setDefaultPaymentMethod(long paymentMethodId) {
        return paymentMethodService.setDefaultPaymentMethod(paymentMethodId);
    }

    // =========================================================================
    // Webhooks
    // =========================================================================

    public List<Webhook> listWebhooks() {
        return webhookService.listWebhooks();
    }

    public Webhook createWebhook(String url, String event) {
        return webhookService.createWebhook(url, event);
    }

    public Webhook updateWebhook(long webhookId, String url, String event) {
        return webhookService.updateWebhook(webhookId, url, event);
    }

    public ApiResponse deleteWebhook(long webhookId) {
        return webhookService.deleteWebhook(webhookId);
    }
}
