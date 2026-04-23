import Foundation

/// API methods for reference data — services, plans, pricing, extras, and cleaners.
public final class OtherApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// Get all cleaning service types available on the partner account.
    public func getServices() async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/services")
    }

    /// Get available booking plans for a property.
    ///
    /// - Parameter propertyId: The property to fetch plans for.
    public func getPlans(propertyId: Int) async throws -> ApiResponse<AnyCodable> {
        let query = [URLQueryItem(name: "propertyId", value: "\(propertyId)")]
        return try await client.requestRaw(method: "GET", path: "/v1/plans", queryItems: query)
    }

    /// Get the system-recommended number of cleaning hours for a property configuration.
    ///
    /// Use the returned `hours` value as the `hours` field when creating a booking.
    ///
    /// - Parameters:
    ///   - propertyId: The property being cleaned.
    ///   - roomCount: Number of rooms.
    ///   - bathroomCount: Number of bathrooms.
    public func getRecommendedHours(
        propertyId: Int,
        roomCount: Int,
        bathroomCount: Int
    ) async throws -> ApiResponse<RecommendedHours> {
        let query = [
            URLQueryItem(name: "propertyId",    value: "\(propertyId)"),
            URLQueryItem(name: "roomCount",      value: "\(roomCount)"),
            URLQueryItem(name: "bathroomCount",  value: "\(bathroomCount)"),
        ]
        return try await client.request(
            method: "GET",
            path: "/v1/recommended-hours",
            queryItems: query
        )
    }

    /// Calculate the estimated total price for a potential booking.
    ///
    /// Call this to show users a price preview before they confirm the booking.
    public func getCostEstimate(_ request: CostEstimateRequest) async throws -> ApiResponse<CostEstimate> {
        return try await client.request(method: "POST", path: "/v1/cost-estimate", body: request)
    }

    /// Get available add-on services for a given service type.
    ///
    /// - Parameter serviceId: The service type ID (from `getServices()`).
    public func getCleaningExtras(serviceId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/cleaning-extras/\(serviceId)")
    }

    /// Find cleaners available for a specific date, time, and property.
    public func getAvailableCleaners(_ request: AvailableCleanersRequest) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "POST", path: "/v1/available-cleaners", body: request)
    }

    /// Get all valid coupon codes available for use at booking creation.
    public func getCoupons() async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/coupons")
    }
}
