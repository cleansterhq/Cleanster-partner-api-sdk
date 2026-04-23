package com.cleanster.android.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")        val id: Int     = 0,
    @SerializedName("email")     val email: String = "",
    @SerializedName("firstName") val firstName: String = "",
    @SerializedName("lastName")  val lastName: String = "",
    @SerializedName("phone")     val phone: String? = null,
    @SerializedName("token")     val token: String? = null,
)

data class Booking(
    @SerializedName("id")              val id: Int     = 0,
    @SerializedName("status")          val status: String = "",
    @SerializedName("date")            val date: String = "",
    @SerializedName("time")            val time: String = "",
    @SerializedName("hours")           val hours: Double = 0.0,
    @SerializedName("propertyId")      val propertyId: Int = 0,
    @SerializedName("planId")          val planId: Int = 0,
    @SerializedName("cleanerId")       val cleanerId: Int? = null,
    @SerializedName("paymentMethodId") val paymentMethodId: Int = 0,
    @SerializedName("roomCount")       val roomCount: Int = 0,
    @SerializedName("bathroomCount")   val bathroomCount: Int = 0,
    @SerializedName("couponCode")      val couponCode: String? = null,
)

data class Property(
    @SerializedName("id")             val id: Int     = 0,
    @SerializedName("name")           val name: String = "",
    @SerializedName("address")        val address: String = "",
    @SerializedName("city")           val city: String = "",
    @SerializedName("state")          val state: String? = null,
    @SerializedName("zip")            val zip: String? = null,
    @SerializedName("country")        val country: String = "",
    @SerializedName("roomCount")      val roomCount: Int = 0,
    @SerializedName("bathroomCount")  val bathroomCount: Int = 0,
    @SerializedName("serviceId")      val serviceId: Int = 0,
    @SerializedName("latitude")       val latitude: Double? = null,
    @SerializedName("longitude")      val longitude: Double? = null,
    @SerializedName("timezone")       val timezone: String? = null,
    @SerializedName("isEnabled")      val isEnabled: Boolean = true,
    @SerializedName("note")           val note: String? = null,
)

data class Checklist(
    @SerializedName("id")    val id: Int = 0,
    @SerializedName("name")  val name: String = "",
    @SerializedName("items") val items: List<String> = emptyList(),
)

data class PaymentMethod(
    @SerializedName("id")        val id: Int = 0,
    @SerializedName("brand")     val brand: String? = null,
    @SerializedName("last4")     val last4: String? = null,
    @SerializedName("isDefault") val isDefault: Boolean = false,
    @SerializedName("type")      val type: String? = null,
)

data class Webhook(
    @SerializedName("id")    val id: Int = 0,
    @SerializedName("url")   val url: String = "",
    @SerializedName("event") val event: String = "",
)

data class Cleaner(
    @SerializedName("id")        val id: Int = 0,
    @SerializedName("firstName") val firstName: String = "",
    @SerializedName("lastName")  val lastName: String = "",
    @SerializedName("rating")    val rating: Double? = null,
)

data class ChatMessage(
    @SerializedName("messageId")   val messageId: String = "",
    @SerializedName("senderId")    val senderId: String = "",
    @SerializedName("content")     val content: String = "",
    @SerializedName("timestamp")   val timestamp: String = "",
    @SerializedName("messageType") val messageType: String = "text",
    @SerializedName("isRead")      val isRead: Boolean = false,
    @SerializedName("senderType")  val senderType: String = "",
)

data class CostEstimate(
    @SerializedName("subtotal") val subtotal: Double = 0.0,
    @SerializedName("discount") val discount: Double = 0.0,
    @SerializedName("total")    val total: Double = 0.0,
)

data class Coupon(
    @SerializedName("code")     val code: String = "",
    @SerializedName("discount") val discount: String = "",
    @SerializedName("type")     val type: String = "",
)

data class BlacklistEntry(
    @SerializedName("cleanerId") val cleanerId: Int = 0,
    @SerializedName("reason")    val reason: String? = null,
)
