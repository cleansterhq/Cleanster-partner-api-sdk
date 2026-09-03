package com.cleanster.model

import com.google.gson.annotations.SerializedName

data class User(
    val id:        Int?    = null,
    val email:     String? = null,
    val firstName: String? = null,
    val lastName:  String? = null,
    val phone:     String? = null,
    val token:     String? = null,
)

data class CreateUserResponse(
    val userId:      Int?    = null,
    val accessToken: String? = null,
)

data class PagedBookings(
    val content: List<Booking>? = null,
)

data class Property(
    val id:            Int?     = null,
    val name:          String?  = null,
    @SerializedName(value = "nickName", alternate = ["nickname"])
    val nickName:      String?  = null,
    val address:       String?  = null,
    val city:          String?  = null,
    val state:         String?  = null,
    val zip:           String?  = null,
    val country:       String?  = null,
    val timezone:      String?  = null,
    val roomCount:     Int?     = null,
    val bathroomCount: Int?     = null,
    val serviceId:     Int?     = null,
    val isEnabled:     Boolean? = null,
    val note:          String?  = null,
    val latitude:      Double?  = null,
    val longitude:     Double?  = null,
)

data class Booking(
    val id:              Int?     = null,
    val status:          String?  = null,
    val cleanerStatus:   String?  = null,
    val date:            String?  = null,
    val time:            String?  = null,
    val hours:           Double?  = null,
    val cost:            Double?  = null,
    val propertyId:      Int?     = null,
    val cleanerId:       Int?     = null,
    val planId:          Int?     = null,
    val roomCount:       Int?     = null,
    val bathroomCount:   Int?     = null,
    val extraSupplies:   Boolean? = null,
    val paymentMethodId: Int?     = null,
    val couponCode:      String?  = null,
)

data class Checklist(
    val id:    Int?               = null,
    val name:  String?            = null,
    val items: List<ChecklistItem>? = null,
)

data class ChecklistItem(
    val id:          Int?     = null,
    val description: String?  = null,
    val isCompleted: Boolean? = null,
    val imageUrl:    String?  = null,
)

data class PaymentMethod(
    val id:        Int?     = null,
    val type:      String?  = null,
    val lastFour:  String?  = null,
    val brand:     String?  = null,
    val isDefault: Boolean? = null,
)

data class Webhook(
    val id:     Int?    = null,
    val url:    String? = null,
    val event:  String? = null,
    val secret: String? = null,
)

data class ChatAttachment(
    val type: String? = null,
    val url: String? = null,
    @SerializedName(value = "thumb_url", alternate = ["thumbUrl"])
    val thumbUrl: String? = null,
)

data class ChatMessage(
    @SerializedName(value = "message_id", alternate = ["messageId"])
    val messageId: String? = null,
    @SerializedName(value = "sender_id", alternate = ["senderId"])
    val senderId: String? = null,
    val content: String? = null,
    val timestamp: String? = null,
    @SerializedName(value = "message_type", alternate = ["messageType"])
    val messageType: String? = null,
    val attachments: List<ChatAttachment>? = null,
    @SerializedName(value = "is_read", alternate = ["isRead"])
    val isRead: Boolean? = null,
    @SerializedName(value = "sender_type", alternate = ["senderType"])
    val senderType: String? = null,
    // Legacy fields are retained for compatibility with older response shapes.
    val id: String? = null,
    val message: String? = null,
    val sentBy: String? = null,
    val sentAt: String? = null,
    val isDeleted: Boolean? = null,
)

data class Cleaner(
    val id:              Int?    = null,
    val firstName:       String? = null,
    val lastName:        String? = null,
    val rating:          Double? = null,
    val profileImageUrl: String? = null,
)

data class CostEstimate(
    val subtotal: Double? = null,
    val discount: Double? = null,
    val total:    Double? = null,
)

data class RecommendedHours(
    val hours: Double? = null,
)
