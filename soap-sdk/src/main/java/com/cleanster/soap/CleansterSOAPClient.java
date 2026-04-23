package com.cleanster.soap;

import com.cleanster.soap.model.*;

import java.util.List;

/**
 * Main entry point for the Cleanster SOAP SDK.
 *
 * <p>Provides a high-level SOAP-style API over the Cleanster Partner REST API.
 * Each method corresponds to one SOAP operation defined in cleanster.wsdl.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * CleansterSOAPClient client = new CleansterSOAPClient("your-api-key");
 *
 * // Get a booking
 * Booking booking = client.getBooking(16459);
 *
 * // Create a booking
 * CreateBookingRequest req = new CreateBookingRequest();
 * req.setPropertyId(42L);
 * req.setScheduledAt("2025-06-01T10:00:00Z");
 * req.setDurationHours(3.0);
 * req.setServiceType("standard");
 * Booking created = client.createBooking(req);
 * }</pre>
 */
public class CleansterSOAPClient {

    private final BookingService    bookingService;
    private final PropertyService   propertyService;
    private final CleanerService    cleanerService;
    private final ChecklistService  checklistService;
    private final OtherService      otherService;

    public CleansterSOAPClient(String apiKey) {
        SOAPTransport transport = new SOAPTransport(apiKey);
        this.bookingService   = new BookingService(transport);
        this.propertyService  = new PropertyService(transport);
        this.cleanerService   = new CleanerService(transport);
        this.checklistService = new ChecklistService(transport);
        this.otherService     = new OtherService(transport);
    }

    CleansterSOAPClient(SOAPTransport transport) {
        this.bookingService   = new BookingService(transport);
        this.propertyService  = new PropertyService(transport);
        this.cleanerService   = new CleanerService(transport);
        this.checklistService = new ChecklistService(transport);
        this.otherService     = new OtherService(transport);
    }

    // -------------------------------------------------------------------------
    // Booking operations
    // -------------------------------------------------------------------------

    /** GetBooking — retrieve a booking by ID. */
    public Booking getBooking(long bookingId) {
        return bookingService.getBooking(bookingId);
    }

    /** ListBookings — list bookings with optional filters. */
    public List<Booking> listBookings(ListBookingsRequest request) {
        return bookingService.listBookings(request);
    }

    /** CreateBooking — create a new cleaning booking. */
    public Booking createBooking(CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    /** CancelBooking — cancel an existing booking. */
    public ApiResponse cancelBooking(long bookingId, String reason) {
        return bookingService.cancelBooking(bookingId, reason);
    }

    /** RescheduleBooking — move a booking to a new date and time. */
    public Booking rescheduleBooking(RescheduleBookingRequest request) {
        return bookingService.rescheduleBooking(request);
    }

    /** AssignCleaner — assign a cleaner to a booking. */
    public Booking assignCleaner(long bookingId, long cleanerId) {
        return bookingService.assignCleaner(bookingId, cleanerId);
    }

    // -------------------------------------------------------------------------
    // Property operations
    // -------------------------------------------------------------------------

    /** GetProperty — retrieve a property by ID. */
    public Property getProperty(long propertyId) {
        return propertyService.getProperty(propertyId);
    }

    /** ListProperties — list all properties. */
    public List<Property> listProperties(int page, int perPage) {
        return propertyService.listProperties(page, perPage);
    }

    /** CreateProperty — add a new property. */
    public Property createProperty(CreatePropertyRequest request) {
        return propertyService.createProperty(request);
    }

    // -------------------------------------------------------------------------
    // Cleaner operations
    // -------------------------------------------------------------------------

    /** ListCleaners — list cleaners with optional status filter. */
    public List<Cleaner> listCleaners(String status, int page, int perPage) {
        return cleanerService.listCleaners(status, page, perPage);
    }

    /** GetCleaner — retrieve a cleaner by ID. */
    public Cleaner getCleaner(long cleanerId) {
        return cleanerService.getCleaner(cleanerId);
    }

    // -------------------------------------------------------------------------
    // Checklist operations
    // -------------------------------------------------------------------------

    /** ListChecklists — list all checklists. */
    public List<Checklist> listChecklists(int page, int perPage) {
        return checklistService.listChecklists(page, perPage);
    }

    /** GetChecklist — retrieve a checklist by ID. */
    public Checklist getChecklist(long checklistId) {
        return checklistService.getChecklist(checklistId);
    }

    /** CreateChecklist — create a new checklist. */
    public Checklist createChecklist(String name, List<String> items) {
        return checklistService.createChecklist(name, items);
    }

    /** DeleteChecklist — delete a checklist by ID. */
    public ApiResponse deleteChecklist(long checklistId) {
        return checklistService.deleteChecklist(checklistId);
    }

    /** UploadChecklistImage — upload an image for a checklist. */
    public ApiResponse uploadChecklistImage(long checklistId, byte[] imageData, String fileName) {
        return checklistService.uploadChecklistImage(checklistId, imageData, fileName);
    }

    // -------------------------------------------------------------------------
    // Other operations
    // -------------------------------------------------------------------------

    /** GetServices — list available cleaning service types. */
    public List<ServiceType> getServices() {
        return otherService.getServices();
    }

    /** GetChat — retrieve the chat history for a booking. */
    public List<ChatMessage> getChat(long bookingId) {
        return otherService.getChat(bookingId);
    }

    /** SendMessage — send a chat message on a booking. */
    public ChatMessage sendMessage(long bookingId, String message) {
        return otherService.sendMessage(bookingId, message);
    }
}
