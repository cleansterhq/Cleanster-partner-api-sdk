import Foundation
@testable import Cleanster

/// Configurable mock for URLSession used in all unit tests.
final class MockNetworkSession: NetworkSession, @unchecked Sendable {
    var responseData: Data = Data()
    var statusCode: Int    = 200
    var capturedRequest: URLRequest?

    func data(for request: URLRequest) async throws -> (Data, URLResponse) {
        capturedRequest = request
        let url = request.url ?? URL(string: "https://example.com")!
        let response = HTTPURLResponse(
            url:        url,
            statusCode: statusCode,
            httpVersion: "HTTP/1.1",
            headerFields: nil
        )!
        return (responseData, response)
    }

    // MARK: - Helpers

    /// Configure the mock to return a successful JSON envelope wrapping `payload`.
    func succeed(with payload: [String: Any], status: Int = 200) {
        let envelope: [String: Any] = [
            "status":  status,
            "message": "OK",
            "data":    payload,
        ]
        responseData = try! JSONSerialization.data(withJSONObject: envelope)
        statusCode   = status
    }

    /// Configure the mock to return a successful JSON envelope wrapping an array.
    func succeedWithArray(_ payload: [[String: Any]], status: Int = 200) {
        let envelope: [String: Any] = [
            "status":  status,
            "message": "OK",
            "data":    payload,
        ]
        responseData = try! JSONSerialization.data(withJSONObject: envelope)
        statusCode   = status
    }

    /// Configure the mock to return an empty-data success response.
    func succeedEmpty(status: Int = 200) {
        let envelope: [String: Any] = [
            "status":  status,
            "message": "OK",
        ]
        responseData = try! JSONSerialization.data(withJSONObject: envelope)
        statusCode   = status
    }

    /// Configure the mock to return an error response.
    func fail(statusCode: Int, message: String = "Error") {
        let envelope: [String: Any] = [
            "status":  statusCode,
            "message": message,
        ]
        responseData     = try! JSONSerialization.data(withJSONObject: envelope)
        self.statusCode  = statusCode
    }
}

// MARK: - Test utilities

extension MockNetworkSession {
    var capturedMethod: String? { capturedRequest?.httpMethod }
    var capturedURL: String?    { capturedRequest?.url?.absoluteString }

    var capturedBody: [String: Any]? {
        guard let data = capturedRequest?.httpBody else { return nil }
        return try? JSONSerialization.jsonObject(with: data) as? [String: Any]
    }

    var capturedHeaders: [String: String]? {
        capturedRequest?.allHTTPHeaderFields
    }
}
