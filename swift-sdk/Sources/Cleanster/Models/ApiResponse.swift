import Foundation

/// The standard response envelope returned by every Cleanster API endpoint.
public struct ApiResponse<T: Decodable>: Decodable {
    /// HTTP-style status code (200, 400, 401, 404, 500).
    public let status: Int
    /// Human-readable status description.
    public let message: String
    /// The response payload. `nil` on errors.
    public let data: T?
}
