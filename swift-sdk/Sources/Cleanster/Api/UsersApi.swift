import Foundation

/// API methods for creating users and managing authentication tokens.
public final class UsersApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// Create a new end-user account.
    ///
    /// - Parameters:
    ///   - email: User's email address.
    ///   - firstName: User's first name.
    ///   - lastName: User's last name.
    ///   - phone: Optional phone number (E.164 format recommended).
    /// - Returns: `ApiResponse<User>` — save the returned `data.id` for future token lookups.
    public func createUser(
        email: String,
        firstName: String,
        lastName: String,
        phone: String? = nil
    ) async throws -> ApiResponse<User> {
        let body = CreateUserRequest(email: email, firstName: firstName,
                                     lastName: lastName, phone: phone)
        return try await client.request(method: "POST", path: "/v1/user/account", body: body)
    }

    /// Fetch the long-lived JWT for a user.
    ///
    /// After calling this, pass the returned token to `client.setToken(_:)` before making
    /// any calls on behalf of this user.
    ///
    /// - Parameter userId: The user's Cleanster ID (from `createUser`).
    /// - Returns: `ApiResponse<User>` — the JWT is in `data.token`.
    public func fetchAccessToken(_ userId: Int) async throws -> ApiResponse<User> {
        return try await client.request(
            method: "GET",
            path: "/v1/user/access-token/\(userId)"
        )
    }

    /// Verify that a JWT is valid and has not expired.
    ///
    /// - Parameter token: The JWT string to validate.
    /// - Returns: `ApiResponse<AnyCodable>` — check `message` for "OK".
    public func verifyJwt(_ token: String) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(
            method: "POST",
            path: "/v1/user/verify-jwt",
            body: VerifyJwtRequest(token: token)
        )
    }
}
