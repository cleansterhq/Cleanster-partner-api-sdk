import Foundation

/// API methods for managing cleaning properties (locations).
public final class PropertiesApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// List all properties. Optionally filter by service type.
    public func listProperties(serviceId: Int? = nil) async throws -> ApiResponse<AnyCodable> {
        var query: [URLQueryItem]? = nil
        if let sid = serviceId {
            query = [URLQueryItem(name: "serviceId", value: "\(sid)")]
        }
        return try await client.requestRaw(method: "GET", path: "/v1/properties", queryItems: query)
    }

    /// Create a new property.
    public func addProperty(_ request: CreatePropertyRequest) async throws -> ApiResponse<Property> {
        return try await client.request(method: "POST", path: "/v1/properties", body: request)
    }

    /// Get a single property by ID.
    public func getProperty(_ propertyId: Int) async throws -> ApiResponse<Property> {
        return try await client.request(method: "GET", path: "/v1/properties/\(propertyId)")
    }

    /// Update an existing property.
    public func updateProperty(_ propertyId: Int, request: CreatePropertyRequest) async throws -> ApiResponse<Property> {
        return try await client.request(method: "PUT", path: "/v1/properties/\(propertyId)", body: request)
    }

    /// Permanently delete a property.
    public func deleteProperty(_ propertyId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "DELETE", path: "/v1/properties/\(propertyId)")
    }

    /// Update freeform additional information fields on a property.
    public func updateAdditionalInformation(
        _ propertyId: Int,
        fields: [String: String]
    ) async throws -> ApiResponse<AnyCodable> {
        let body = UpdateAdditionalInfoRequest(fields: fields)
        return try await client.requestRaw(
            method: "PUT",
            path: "/v1/properties/\(propertyId)/additional-information",
            body: body
        )
    }

    /// Enable or disable a property. Disabled properties cannot receive new bookings.
    public func enableOrDisableProperty(_ propertyId: Int, enabled: Bool) async throws -> ApiResponse<AnyCodable> {
        let body = EnableDisablePropertyRequest(enabled: enabled)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/properties/\(propertyId)/enable-disable",
            body: body
        )
    }

    /// List the cleaners associated with a property.
    public func getPropertyCleaners(_ propertyId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/properties/\(propertyId)/cleaners")
    }

    /// Add a cleaner to a property's preferred pool.
    public func addCleanerToProperty(_ propertyId: Int, cleanerId: Int) async throws -> ApiResponse<AnyCodable> {
        let body = AddPropertyCleanerRequest(cleanerId: cleanerId)
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/properties/\(propertyId)/cleaners",
            body: body
        )
    }

    /// Remove a cleaner from a property's preferred pool.
    public func removeCleanerFromProperty(_ propertyId: Int, cleanerId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(
            method: "DELETE",
            path: "/v1/properties/\(propertyId)/cleaners/\(cleanerId)"
        )
    }

    /// Set an iCal feed URL for calendar sync (e.g. Airbnb, VRBO).
    public func setICalLink(_ propertyId: Int, icalLink: String) async throws -> ApiResponse<AnyCodable> {
        let body = SetICalLinkRequest(icalLink: icalLink)
        return try await client.requestRaw(
            method: "PUT",
            path: "/v1/properties/\(propertyId)/ical",
            body: body
        )
    }

    /// Retrieve the current iCal feed URL for a property.
    public func getICalLink(_ propertyId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/properties/\(propertyId)/ical")
    }

    /// Remove the iCal feed link from a property.
    public func deleteICalLink(_ propertyId: Int, icalLink: String) async throws -> ApiResponse<AnyCodable> {
        let body = DeleteICalLinkRequest(icalLink: icalLink)
        return try await client.requestRaw(
            method: "DELETE",
            path: "/v1/properties/\(propertyId)/ical",
            body: body
        )
    }

    /// Assign a checklist as the default for all future bookings on a property.
    ///
    /// - Parameter updateUpcomingBookings: If `true`, also updates already-scheduled bookings.
    public func setDefaultChecklist(
        _ propertyId: Int,
        checklistId: Int,
        updateUpcomingBookings: Bool = false
    ) async throws -> ApiResponse<AnyCodable> {
        let query = [URLQueryItem(name: "updateUpcomingBookings", value: "\(updateUpcomingBookings)")]
        return try await client.requestRaw(
            method: "PUT",
            path: "/v1/properties/\(propertyId)/checklist/\(checklistId)",
            queryItems: query
        )
    }
}
