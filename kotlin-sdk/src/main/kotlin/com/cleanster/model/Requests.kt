package com.cleanster.model

// ── Users ─────────────────────────────────────────────────────────────────────

data class CreateUserRequest(
    val email:      String,
    val firstName:  String,
    val lastName:   String,
    val customerId: String,
    val phone:      String? = null,
)

data class VerifyJwtRequest(
    val token: String,
)

// ── Properties ────────────────────────────────────────────────────────────────

data class CreatePropertyRequest(
    val name:          String,
    val address:       String,
    val city:          String,
    val country:       String,
    val roomCount:     Int,
    val bathroomCount: Int,
    val serviceId:     Int,
    val state:         String? = null,
    val zip:           String? = null,
    val timezone:      String? = null,
    val note:          String? = null,
    val latitude:      Double? = null,
    val longitude:     Double? = null,
    val nickName:      String? = null,
    val street:        String? = null,
    val apt:           String? = null,
    val zipCode:       String? = null,
)

data class EnableDisablePropertyRequest(
    val enabled: Boolean,
)

data class AddPropertyCleanerRequest(
    val cleanerId: Int,
)

/**
 * Request body for adding one or more iCal calendar links to a property.
 * Each URL must be a live, publicly fetchable .ics feed - the API validates
 * the feed content, not just the URL shape.
 */
data class SetICalLinkRequest(
    val calendarLinks: List<String>,
)

/** A single calendar link attached to a property, as returned by getICalLink. */
data class CalendarLink(
    val id: Int,
    val calendarLink: String,
)

/** Request body for removing calendar links from a property, by numeric link ID. */
data class DeleteICalLinkRequest(
    val ids: List<Int>,
)

// ── Bookings ──────────────────────────────────────────────────────────────────

data class CreateBookingRequest(
    val date:            String,
    val time:            String,
    val propertyId:      Int,
    val planId:          Int,
    val hours:           Double,
    val roomCount:       Int,
    val bathroomCount:   Int,
    val extraSupplies:   Boolean,
    val paymentMethodId: Int,
    val couponCode:      String?   = null,
    val extras:          List<Int>? = null,
)

data class CancelBookingRequest(
    val reason: String? = null,
)

data class RescheduleBookingRequest(
    val date: String,
    val time: String,
)

data class AssignCleanerRequest(
    val cleanerId: Int,
)

data class AdjustHoursRequest(
    val hours: Double,
)

data class PayExpensesRequest(
    val paymentMethodId: Int,
)

data class SubmitFeedbackRequest(
    val rating:  Int,
    val comment: String? = null,
)

data class AddTipRequest(
    val amount:          Double,
    val paymentMethodId: Int,
)

data class SendMessageRequest(
    val message: String,
)

data class TaskQuantity(
    val id:       Int,
    val quantity: Int,
)

data class UpdateTaskRequest(
    val tasks: List<TaskQuantity>,
)

data class UpdateSqftRequest(
    val totalSqFt: Double,
)

// ── Checklists ────────────────────────────────────────────────────────────────

data class CreateChecklistRequest(
    val title: String,
    val tasks: List<ChecklistTask>,
)

// ── Payment Methods ───────────────────────────────────────────────────────────

data class AddPaymentMethodRequest(
    val paymentMethodId: String,
)

// ── Webhooks ──────────────────────────────────────────────────────────────────

data class CreateWebhookRequest(
    val url:   String,
    val event: String,
)

// ── Blacklist ─────────────────────────────────────────────────────────────────

data class BlacklistRequest(
    val cleanerId: Int,
    val reason:    String? = null,
)

// ── Reference Data ────────────────────────────────────────────────────────────

data class CostEstimateRequest(
    val propertyId:  Int,
    val planId:      Int,
    val hours:       Double,
    val couponCode:  String?   = null,
    val extras:      List<Int>? = null,
)

data class AvailableCleanersRequest(
    val propertyId: Int,
    val date:       String,
    val time:       String,
)
