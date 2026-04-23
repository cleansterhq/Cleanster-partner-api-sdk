import Foundation

/// Abstraction over URLSession to allow mocking in tests.
public protocol NetworkSession: Sendable {
    func data(for request: URLRequest) async throws -> (Data, URLResponse)
}

extension URLSession: NetworkSession {}
