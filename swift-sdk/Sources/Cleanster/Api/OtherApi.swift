import Foundation

/// API methods for reference data — services, plans, pricing, extras, and cleaners.
public final class OtherApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// Get all cleaning service types available on the partner account.
    public func getServices() async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/services")
    }

    /// Get available booking plans for a property, optionally filtered by subcategory.
    ///
    /// - Parameters:
    ///   - propertyId: The property to fetch plans for.
    ///   - subcatId: Optional service subcategory ID.
    public func getPlans(propertyId: Int, subcatId: Int? = nil) async throws -> ApiResponse<AnyCodable> {
        var query = [URLQueryItem(name: "propertyId", value: "\(propertyId)")]
        if let s = subcatId { query.append(URLQueryItem(name: "subcatId", value: "\(s)")) }
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
    ///   - subcatId: Optional service subcategory ID.
    public func getRecommendedHours(
        propertyId: Int,
        roomCount: Int,
        bathroomCount: Int,
        subcatId: Int? = nil
    ) async throws -> ApiResponse<RecommendedHours> {
        var query = [
            URLQueryItem(name: "propertyId",    value: "\(propertyId)"),
            URLQueryItem(name: "roomCount",      value: "\(roomCount)"),
            URLQueryItem(name: "bathroomCount",  value: "\(bathroomCount)"),
        ]
        if let s = subcatId { query.append(URLQueryItem(name: "subcatId", value: "\(s)")) }
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

    /// List all cleaners, with optional status and search filters.
    ///
    /// - Parameters:
    ///   - status: Filter by cleaner status ('active', 'inactive', 'pending'). Pass nil to omit.
    ///   - search: Partial match against cleaner name or email. Pass nil to omit.
    public func listCleaners(status: String? = nil, search: String? = nil) async throws -> ApiResponse<AnyCodable> {
        var query: [URLQueryItem] = []
        if let s = status { query.append(URLQueryItem(name: "status", value: s)) }
        if let s = search { query.append(URLQueryItem(name: "search", value: s)) }
        return try await client.requestRaw(method: "GET", path: "/v1/cleaners",
                                           queryItems: query.isEmpty ? nil : query)
    }

    /// Retrieve a single cleaner by their ID.
    ///
    /// - Parameter cleanerId: The cleaner's unique ID.
    public func getCleaner(cleanerId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/cleaners/\(cleanerId)")
    }

    /// List service tasks, filterable by property and service type. Supports pagination.
    ///
    /// - Parameters:
    ///   - propertyId: The property ID.
    ///   - serviceId: The service type ID.
    ///   - pageNo: Optional page number.
    ///   - pageSize: Optional page size.
    public func getTasks(propertyId: Int, serviceId: Int, pageNo: Int? = nil, pageSize: Int? = nil) async throws -> ApiResponse<AnyCodable> {
        var query = [
            URLQueryItem(name: "propertyId", value: "\(propertyId)"),
            URLQueryItem(name: "serviceId", value: "\(serviceId)"),
        ]
        if let p = pageNo { query.append(URLQueryItem(name: "pageNo", value: "\(p)")) }
        if let p = pageSize { query.append(URLQueryItem(name: "pageSize", value: "\(p)")) }
        return try await client.requestRaw(method: "GET", path: "/v1/tasks", queryItems: query)
    }

    /// Get the subcategories available under a given service type.
    ///
    /// - Parameter serviceId: The service type ID.
    public func getSubcategories(serviceId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/services/\(serviceId)/subcategories")
    }
}
