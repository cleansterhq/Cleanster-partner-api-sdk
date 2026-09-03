import Foundation

// MARK: - User

public struct User: Decodable {
    public let id: Int?
    public let email: String?
    public let firstName: String?
    public let lastName: String?
    public let phone: String?
    public let token: String?
}

// MARK: - CreateUserResponse

public struct CreateUserResponse: Decodable {
    public let userId: Int?
    public let accessToken: String?
}

// MARK: - Property

public struct Property: Decodable {
    public let id: Int?
    public let name: String?
    public let address: String?
    public let city: String?
    public let state: String?
    public let zip: String?
    public let country: String?
    public let timezone: String?
    public let roomCount: Int?
    public let bathroomCount: Int?
    public let serviceId: Int?
    public let isEnabled: Bool?
    public let note: String?
    public let latitude: Double?
    public let longitude: Double?
}

// MARK: - Booking

public struct Booking: Decodable {
    public let id: Int?
    public let status: String?
    public let cleanerStatus: String?
    public let date: String?
    public let time: String?
    public let hours: Double?
    public let cost: Double?
    public let propertyId: Int?
    public let cleanerId: Int?
    public let planId: Int?
    public let roomCount: Int?
    public let bathroomCount: Int?
    public let extraSupplies: Bool?
    public let paymentMethodId: Int?
    public let couponCode: String?
}

// MARK: - Checklist

public struct Checklist: Decodable {
    public let id: Int?
    public let name: String?
    public let items: [ChecklistItem]?
}

public struct ChecklistItem: Decodable {
    public let id: Int?
    public let description: String?
    public let isCompleted: Bool?
    public let imageUrl: String?
}

// MARK: - PaymentMethod

public struct PaymentMethod: Decodable {
    public let id: Int?
    public let type: String?
    public let lastFour: String?
    public let brand: String?
    public let isDefault: Bool?
}

// MARK: - Webhook

public struct Webhook: Decodable {
    public let id: Int?
    public let url: String?
    public let event: String?
    public let secret: String?
}

// MARK: - ChatMessage

public struct ChatAttachment: Decodable {
    public let type: String?
    public let url: String?
    public let thumbUrl: String?

    enum CodingKeys: String, CodingKey {
        case type, url
        case thumbUrl = "thumb_url"
    }
}

public struct ChatMessage: Decodable {
    public let messageId: String?
    public let senderId: String?
    public let content: String?
    public let timestamp: String?
    public let messageType: String?
    public let attachments: [ChatAttachment]?
    public let isRead: Bool?
    public let senderType: String?

    // Legacy response fields retained for source compatibility.
    public let id: String?
    public let message: String?
    public let sentBy: String?
    public let sentAt: String?
    public let isDeleted: Bool?

    enum CodingKeys: String, CodingKey {
        case messageId = "message_id"
        case senderId = "sender_id"
        case content, timestamp, attachments
        case messageType = "message_type"
        case isRead = "is_read"
        case senderType = "sender_type"
        case id, message, sentBy, sentAt, isDeleted
    }
}

// MARK: - Cleaner

public struct Cleaner: Decodable {
    public let id: Int?
    public let firstName: String?
    public let lastName: String?
    public let rating: Double?
    public let profileImageUrl: String?
}

// MARK: - CostEstimate

public struct CostEstimate: Decodable {
    public let subtotal: Double?
    public let discount: Double?
    public let total: Double?
}

// MARK: - Plan

public struct Plan: Decodable {
    public let id: Int?
    public let name: String?
    public let pricePerHour: Double?
}

// MARK: - ServiceType

public struct ServiceType: Decodable {
    public let id: Int?
    public let name: String?
}

// MARK: - CleaningExtra

public struct CleaningExtra: Decodable {
    public let id: Int?
    public let name: String?
    public let price: Double?
}

// MARK: - BlacklistedCleaner

public struct BlacklistedCleaner: Decodable {
    public let cleanerId: Int?
    public let reason: String?
}

// MARK: - Coupon

public struct Coupon: Decodable {
    public let code: String?
    public let discount: String?
    public let type: String?
    public let isExpired: Bool?
}

// MARK: - RecommendedHours

public struct RecommendedHours: Decodable {
    public let hours: Double?
}
