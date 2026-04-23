package com.cleanster.android.model

import com.google.gson.annotations.SerializedName

data class CreateUserRequest(
    @SerializedName("email")     val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName")  val lastName: String,
    @SerializedName("phone")     val phone: String? = null,
)

data class VerifyJwtRequest(
    @SerializedName("token") val token: String,
)

data class CreateBookingRequest(
    @SerializedName("date")            val date: String,
    @SerializedName("time")            val time: String,
    @SerializedName("propertyId")      val propertyId: Int,
    @SerializedName("planId")          val planId: Int,
    @SerializedName("hours")           val hours: Double,
    @SerializedName("roomCount")       val roomCount: Int,
    @SerializedName("bathroomCount")   val bathroomCount: Int,
    @SerializedName("extraSupplies")   val extraSupplies: Boolean,
    @SerializedName("paymentMethodId") val paymentMethodId: Int,
    @SerializedName("couponCode")      val couponCode: String? = null,
    @SerializedName("extras")          val extras: List<Int>? = null,
)

data class CancelBookingRequest(
    @SerializedName("reason") val reason: String? = null,
)

data class RescheduleBookingRequest(
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
)

data class AssignCleanerRequest(
    @SerializedName("cleanerId") val cleanerId: Int,
)

data class AdjustHoursRequest(
    @SerializedName("hours") val hours: Double,
)

data class PayExpensesRequest(
    @SerializedName("paymentMethodId") val paymentMethodId: Int,
)

data class FeedbackRequest(
    @SerializedName("rating")  val rating: Int,
    @SerializedName("comment") val comment: String? = null,
)

data class TipRequest(
    @SerializedName("amount")          val amount: Double,
    @SerializedName("paymentMethodId") val paymentMethodId: Int,
)

data class SendMessageRequest(
    @SerializedName("message") val message: String,
)

data class CreatePropertyRequest(
    @SerializedName("name")          val name: String,
    @SerializedName("address")       val address: String,
    @SerializedName("city")          val city: String,
    @SerializedName("country")       val country: String,
    @SerializedName("roomCount")     val roomCount: Int,
    @SerializedName("bathroomCount") val bathroomCount: Int,
    @SerializedName("serviceId")     val serviceId: Int,
    @SerializedName("state")         val state: String? = null,
    @SerializedName("zip")           val zip: String? = null,
    @SerializedName("timezone")      val timezone: String? = null,
    @SerializedName("note")          val note: String? = null,
    @SerializedName("latitude")      val latitude: Double? = null,
    @SerializedName("longitude")     val longitude: Double? = null,
)

data class EnableDisableRequest(
    @SerializedName("enabled") val enabled: Boolean,
)

data class AddCleanerToPropertyRequest(
    @SerializedName("cleanerId") val cleanerId: Int,
)

data class ICalRequest(
    @SerializedName("icalLink") val icalLink: String,
)

data class CreateChecklistRequest(
    @SerializedName("name")  val name: String,
    @SerializedName("items") val items: List<String>,
)

data class AddPaymentMethodRequest(
    @SerializedName("paymentMethodId") val paymentMethodId: String,
)

data class CreateWebhookRequest(
    @SerializedName("url")   val url: String,
    @SerializedName("event") val event: String,
)

data class AddToBlacklistRequest(
    @SerializedName("cleanerId") val cleanerId: Int,
    @SerializedName("reason")    val reason: String? = null,
)

data class RemoveFromBlacklistRequest(
    @SerializedName("cleanerId") val cleanerId: Int,
)

data class CostEstimateRequest(
    @SerializedName("propertyId") val propertyId: Int,
    @SerializedName("planId")     val planId: Int,
    @SerializedName("hours")      val hours: Double,
    @SerializedName("couponCode") val couponCode: String? = null,
    @SerializedName("extras")     val extras: List<Int>? = null,
)

data class AvailableCleanersRequest(
    @SerializedName("propertyId") val propertyId: Int,
    @SerializedName("date")       val date: String,
    @SerializedName("time")       val time: String,
)
