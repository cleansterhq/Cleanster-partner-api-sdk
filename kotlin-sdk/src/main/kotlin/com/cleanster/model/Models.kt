package com.cleanster.model

data class User(
    val id:        Int?    = null,
    val email:     String? = null,
    val firstName: String? = null,
    val lastName:  String? = null,
    val phone:     String? = null,
    val token:     String? = null,
)

data class Property(
    val id:            Int?     = null,
    val name:          String?  = null,
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
    val id:    Int?    = null,
    val url:   String? = null,
    val event: String? = null,
)

data class ChatMessage(
    val id:        String?  = null,
    val message:   String?  = null,
    val sentBy:    String?  = null,
    val sentAt:    String?  = null,
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
