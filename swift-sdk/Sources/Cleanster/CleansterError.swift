import Foundation

/// Errors thrown by the Cleanster SDK.
public enum CleansterError: Error, LocalizedError {

    /// The API returned an error status code.
    case apiError(statusCode: Int, message: String)

    /// The `access-key` or `token` header was rejected (HTTP 401).
    case unauthorized

    /// The resource was not found (HTTP 404).
    case notFound

    /// A network-level error occurred (e.g. no connectivity).
    case networkError(Error)

    /// The server response could not be decoded.
    case decodingError(Error)

    /// An invalid URL was constructed — usually a programming error.
    case invalidURL(String)

    /// The response was not an HTTP response.
    case invalidResponse

    public var errorDescription: String? {
        switch self {
        case .apiError(let code, let msg):   return "API error \(code): \(msg)"
        case .unauthorized:                  return "Unauthorized — check your access-key and token."
        case .notFound:                      return "Resource not found."
        case .networkError(let e):           return "Network error: \(e.localizedDescription)"
        case .decodingError(let e):          return "Decoding error: \(e.localizedDescription)"
        case .invalidURL(let url):           return "Invalid URL: \(url)"
        case .invalidResponse:              return "Received a non-HTTP response."
        }
    }
}
