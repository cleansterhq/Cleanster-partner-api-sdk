package com.cleanster.model

// ── Users ─────────────────────────────────────────────────────────────────────

data class CreateUserRequest(
    val email:     String,
    val firstName: String,
    val lastName:  String,
    val phone:     String? = null,
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
)

data class EnableDisablePropertyRequest(
    val enabled: Boolean,
)

data class AddPropertyCleanerRequest(
    val cleanerId: Int,
)

data class SetICalLinkRequest(
    val icalLink: String,
)

data class DeleteICalLinkRequest(
    val icalLink: String,
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

// ── Checklists ────────────────────────────────────────────────────────────────

data class CreateChecklistRequest(
    val name:  String,
    val items: List<String>,
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
