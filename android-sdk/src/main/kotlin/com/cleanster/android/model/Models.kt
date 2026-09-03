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

data class CreateUserResponse(
    @SerializedName("userId")      val userId: Int     = 0,
    @SerializedName("accessToken") val accessToken: String = "",
)

data class PagedBookings(
    @SerializedName("content") val content: List<Booking> = emptyList(),
)

data class Booking(
    @SerializedName("id")              val id: Int     = 0,
    @SerializedName("status")          val status: String = "",
    @SerializedName("cleanerStatus")   val cleanerStatus: String? = null,
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
    @SerializedName(value = "nickName", alternate = ["nickname"])
    val nickName: String? = null,
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
    @SerializedName("userId")         val userId: Int? = null,
    @SerializedName("apt")            val apt: String? = null,
    @SerializedName("street")         val street: String? = null,
    @SerializedName("zipCode")        val zipCode: String? = null,
    @SerializedName("isActive")       val isActive: Boolean? = null,
    @SerializedName("isEnable")       val isEnable: Boolean? = null,
    @SerializedName("pets")           val pets: String? = null,
    @SerializedName("publicName")     val publicName: String? = null,
    @SerializedName("wifiName")       val wifiName: String? = null,
    @SerializedName("wifiPassword")   val wifiPassword: String? = null,
    @SerializedName("laundry")        val laundry: Boolean? = null,
    @SerializedName("garbage")        val garbage: String? = null,
    @SerializedName("extraSupplies")  val extraSupplies: Boolean? = null,
    @SerializedName("createdDate")    val createdDate: String? = null,
    @SerializedName("access")         val access: String? = null,
    @SerializedName("suppliesLocation") val suppliesLocation: String? = null,
    @SerializedName("parking")        val parking: String? = null,
    @SerializedName("otherNote")      val otherNote: String? = null,
)

data class Checklist(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("is_default") val isDefault: Boolean? = null,
    @SerializedName("disabled") val disabled: Boolean? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("totalTasks") val totalTasks: Int? = null,
    @SerializedName("totalSubTasks") val totalSubTasks: Int? = null,
    @SerializedName("tasks") val tasks: List<ChecklistTask> = emptyList(),
    // Legacy fields are retained for older response shapes.
    @SerializedName("name") val name: String = "",
    @SerializedName("items") val items: List<ChecklistItem> = emptyList(),
)

data class ChecklistTask(
    @SerializedName("image_name") val imageName: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("totalSubtasks") val totalSubtasks: Int? = null,
    @SerializedName("subtasks") val subtasks: List<ChecklistSubtask> = emptyList(),
)

data class ChecklistSubtask(
    @SerializedName("description") val description: String? = null,
    @SerializedName("flag_request_photos") val flagRequestPhotos: Boolean? = null,
    @SerializedName("photos") val photos: List<String> = emptyList(),
)

data class ChecklistItem(
    @SerializedName("id")          val id: Int? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("isCompleted") val isCompleted: Boolean? = null,
    @SerializedName("imageUrl")    val imageUrl: String? = null,
)

data class PaymentMethod(
    @SerializedName("id")        val id: Int = 0,
    @SerializedName("brand")     val brand: String? = null,
    @SerializedName("last4")     val last4: String? = null,
    @SerializedName("isDefault") val isDefault: Boolean = false,
    @SerializedName("type")      val type: String? = null,
)

data class Webhook(
    @SerializedName("id")     val id: Int = 0,
    @SerializedName("url")    val url: String = "",
    @SerializedName("event")  val event: String = "",
    @SerializedName("secret") val secret: String? = null,
)

data class Cleaner(
    @SerializedName("id")        val id: Int = 0,
    @SerializedName("firstName") val firstName: String = "",
    @SerializedName("lastName")  val lastName: String = "",
    @SerializedName("rating")    val rating: Double? = null,
)

data class ChatAttachment(
    @SerializedName("type") val type: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName(value = "thumb_url", alternate = ["thumbUrl"])
    val thumbUrl: String? = null,
)

data class ChatMessage(
    @SerializedName(value = "message_id", alternate = ["messageId"])
    val messageId: String = "",
    @SerializedName(value = "sender_id", alternate = ["senderId"])
    val senderId: String = "",
    @SerializedName("content")     val content: String = "",
    @SerializedName("timestamp")   val timestamp: String = "",
    @SerializedName(value = "message_type", alternate = ["messageType"])
    val messageType: String = "text",
    @SerializedName("attachments") val attachments: List<ChatAttachment> = emptyList(),
    @SerializedName(value = "is_read", alternate = ["isRead"])
    val isRead: Boolean = false,
    @SerializedName(value = "sender_type", alternate = ["senderType"])
    val senderType: String = "",
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
