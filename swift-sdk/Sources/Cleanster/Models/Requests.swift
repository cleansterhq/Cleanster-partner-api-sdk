import Foundation

// MARK: - User Requests

public struct CreateUserRequest: Encodable {
    public let email: String
    public let firstName: String
    public let lastName: String
    public let phone: String?

    public init(email: String, firstName: String, lastName: String, phone: String? = nil) {
        self.email     = email
        self.firstName = firstName
        self.lastName  = lastName
        self.phone     = phone
    }
}

public struct VerifyJwtRequest: Encodable {
    public let token: String
    public init(token: String) { self.token = token }
}

// MARK: - Property Requests

public struct CreatePropertyRequest: Encodable {
    public let name: String
    public let address: String
    public let city: String
    public let country: String
    public let roomCount: Int
    public let bathroomCount: Int
    public let serviceId: Int
    public let state: String?
    public let zip: String?
    public let timezone: String?
    public let note: String?
    public let latitude: Double?
    public let longitude: Double?

    public init(
        name: String, address: String, city: String, country: String,
        roomCount: Int, bathroomCount: Int, serviceId: Int,
        state: String? = nil, zip: String? = nil, timezone: String? = nil,
        note: String? = nil, latitude: Double? = nil, longitude: Double? = nil
    ) {
        self.name          = name
        self.address       = address
        self.city          = city
        self.country       = country
        self.roomCount     = roomCount
        self.bathroomCount = bathroomCount
        self.serviceId     = serviceId
        self.state         = state
        self.zip           = zip
        self.timezone      = timezone
        self.note          = note
        self.latitude      = latitude
        self.longitude     = longitude
    }

    public func encode(to encoder: Encoder) throws {
        var c = encoder.container(keyedBy: CodingKeys.self)
        try c.encode(name, forKey: .name)
        try c.encode(address, forKey: .address)
        try c.encode(city, forKey: .city)
        try c.encode(country, forKey: .country)
        try c.encode(roomCount, forKey: .roomCount)
        try c.encode(bathroomCount, forKey: .bathroomCount)
        try c.encode(serviceId, forKey: .serviceId)
        try c.encodeIfPresent(state, forKey: .state)
        try c.encodeIfPresent(zip, forKey: .zip)
        try c.encodeIfPresent(timezone, forKey: .timezone)
        try c.encodeIfPresent(note, forKey: .note)
        try c.encodeIfPresent(latitude, forKey: .latitude)
        try c.encodeIfPresent(longitude, forKey: .longitude)
    }

    enum CodingKeys: String, CodingKey {
        case name, address, city, country, roomCount, bathroomCount,
             serviceId, state, zip, timezone, note, latitude, longitude
    }
}

public struct EnableDisablePropertyRequest: Encodable {
    public let enabled: Bool
    public init(enabled: Bool) { self.enabled = enabled }
}

public struct AddPropertyCleanerRequest: Encodable {
    public let cleanerId: Int
    public init(cleanerId: Int) { self.cleanerId = cleanerId }
}

public struct SetICalLinkRequest: Encodable {
    public let icalLink: String
    public init(icalLink: String) { self.icalLink = icalLink }
}

public struct DeleteICalLinkRequest: Encodable {
    public let icalLink: String
    public init(icalLink: String) { self.icalLink = icalLink }
}

public struct UpdateAdditionalInfoRequest: Encodable {
    public let fields: [String: String]
    public init(fields: [String: String]) { self.fields = fields }
    public func encode(to encoder: Encoder) throws {
        var c = encoder.container(keyedBy: DynamicCodingKey.self)
        for (k, v) in fields {
            try c.encode(v, forKey: DynamicCodingKey(k))
        }
    }
}

// MARK: - Booking Requests

public struct CreateBookingRequest: Encodable {
    public let date: String
    public let time: String
    public let propertyId: Int
    public let planId: Int
    public let hours: Double
    public let roomCount: Int
    public let bathroomCount: Int
    public let extraSupplies: Bool
    public let paymentMethodId: Int
    public let couponCode: String?
    public let extras: [Int]?

    public init(
        date: String, time: String, propertyId: Int, planId: Int,
        hours: Double, roomCount: Int, bathroomCount: Int,
        extraSupplies: Bool, paymentMethodId: Int,
        couponCode: String? = nil, extras: [Int]? = nil
    ) {
        self.date            = date
        self.time            = time
        self.propertyId      = propertyId
        self.planId          = planId
        self.hours           = hours
        self.roomCount       = roomCount
        self.bathroomCount   = bathroomCount
        self.extraSupplies   = extraSupplies
        self.paymentMethodId = paymentMethodId
        self.couponCode      = couponCode
        self.extras          = extras
    }

    public func encode(to encoder: Encoder) throws {
        var c = encoder.container(keyedBy: CodingKeys.self)
        try c.encode(date, forKey: .date)
        try c.encode(time, forKey: .time)
        try c.encode(propertyId, forKey: .propertyId)
        try c.encode(planId, forKey: .planId)
        try c.encode(hours, forKey: .hours)
        try c.encode(roomCount, forKey: .roomCount)
        try c.encode(bathroomCount, forKey: .bathroomCount)
        try c.encode(extraSupplies, forKey: .extraSupplies)
        try c.encode(paymentMethodId, forKey: .paymentMethodId)
        try c.encodeIfPresent(couponCode, forKey: .couponCode)
        try c.encodeIfPresent(extras, forKey: .extras)
    }

    enum CodingKeys: String, CodingKey {
        case date, time, propertyId, planId, hours, roomCount,
             bathroomCount, extraSupplies, paymentMethodId, couponCode, extras
    }
}

public struct CancelBookingRequest: Encodable {
    public let reason: String?
    public init(reason: String? = nil) { self.reason = reason }
}

public struct RescheduleBookingRequest: Encodable {
    public let date: String
    public let time: String
    public init(date: String, time: String) { self.date = date; self.time = time }
}

public struct AssignCleanerRequest: Encodable {
    public let cleanerId: Int
    public init(cleanerId: Int) { self.cleanerId = cleanerId }
}

public struct AdjustHoursRequest: Encodable {
    public let hours: Double
    public init(hours: Double) { self.hours = hours }
}

public struct PayExpensesRequest: Encodable {
    public let paymentMethodId: Int
    public init(paymentMethodId: Int) { self.paymentMethodId = paymentMethodId }
}

public struct SubmitFeedbackRequest: Encodable {
    public let rating: Int
    public let comment: String?
    public init(rating: Int, comment: String? = nil) {
        self.rating  = rating
        self.comment = comment
    }
}

public struct AddTipRequest: Encodable {
    public let amount: Double
    public let paymentMethodId: Int
    public init(amount: Double, paymentMethodId: Int) {
        self.amount          = amount
        self.paymentMethodId = paymentMethodId
    }
}

public struct SendMessageRequest: Encodable {
    public let message: String
    public init(message: String) { self.message = message }
}

// MARK: - Checklist Requests

public struct CreateChecklistRequest: Encodable {
    public let name: String
    public let items: [String]
    public init(name: String, items: [String]) { self.name = name; self.items = items }
}

// MARK: - Payment Method Requests

public struct AddPaymentMethodRequest: Encodable {
    public let paymentMethodId: String
    public init(paymentMethodId: String) { self.paymentMethodId = paymentMethodId }
}

// MARK: - Webhook Requests

public struct CreateWebhookRequest: Encodable {
    public let url: String
    public let event: String
    public init(url: String, event: String) { self.url = url; self.event = event }
}

// MARK: - Blacklist Requests

public struct BlacklistRequest: Encodable {
    public let cleanerId: Int
    public let reason: String?
    public init(cleanerId: Int, reason: String? = nil) {
        self.cleanerId = cleanerId
        self.reason    = reason
    }
}

// MARK: - Cost Estimate Request

public struct CostEstimateRequest: Encodable {
    public let propertyId: Int
    public let planId: Int
    public let hours: Double
    public let couponCode: String?
    public let extras: [Int]?

    public init(propertyId: Int, planId: Int, hours: Double, couponCode: String? = nil, extras: [Int]? = nil) {
        self.propertyId  = propertyId
        self.planId      = planId
        self.hours       = hours
        self.couponCode  = couponCode
        self.extras      = extras
    }

    public func encode(to encoder: Encoder) throws {
        var c = encoder.container(keyedBy: CodingKeys.self)
        try c.encode(propertyId, forKey: .propertyId)
        try c.encode(planId, forKey: .planId)
        try c.encode(hours, forKey: .hours)
        try c.encodeIfPresent(couponCode, forKey: .couponCode)
        try c.encodeIfPresent(extras, forKey: .extras)
    }

    enum CodingKeys: String, CodingKey {
        case propertyId, planId, hours, couponCode, extras
    }
}

// MARK: - Available Cleaners Request

public struct AvailableCleanersRequest: Encodable {
    public let propertyId: Int
    public let date: String
    public let time: String
    public init(propertyId: Int, date: String, time: String) {
        self.propertyId = propertyId
        self.date       = date
        self.time       = time
    }
}

// MARK: - Helpers

struct DynamicCodingKey: CodingKey {
    var stringValue: String
    var intValue: Int? { nil }
    init(_ string: String) { self.stringValue = string }
    init?(stringValue: String) { self.stringValue = stringValue }
    init?(intValue: Int) { return nil }
}
