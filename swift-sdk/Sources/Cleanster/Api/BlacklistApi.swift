import Foundation

/// API methods for blocking specific cleaners from being assigned to bookings.
public final class BlacklistApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// List all blacklisted cleaners.
    public func listBlacklistedCleaners() async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/blacklist/cleaner")
    }

    /// Add a cleaner to the blacklist.
    ///
    /// - Parameters:
    ///   - cleanerId: The cleaner's user ID.
    ///   - reason: Optional internal note explaining the reason.
    public func addToBlacklist(cleanerId: Int, reason: String? = nil) async throws -> ApiResponse<AnyCodable> {
        let body = BlacklistRequest(cleanerId: cleanerId, reason: reason)
        return try await client.requestRaw(method: "POST", path: "/v1/blacklist/cleaner", body: body)
    }

    /// Remove a cleaner from the blacklist.
    public func removeFromBlacklist(cleanerId: Int) async throws -> ApiResponse<AnyCodable> {
        let body = BlacklistRequest(cleanerId: cleanerId)
        return try await client.requestRaw(method: "DELETE", path: "/v1/blacklist/cleaner", body: body)
    }
}
