import Foundation

/// API methods for registering and managing webhook endpoints.
public final class WebhooksApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// List all registered webhook endpoints.
    public func listWebhooks() async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/webhooks")
    }

    /// Register a new webhook endpoint.
    ///
    /// - Parameters:
    ///   - url: HTTPS URL that will receive the POST payload.
    ///   - event: The event name to subscribe to (e.g. `"booking.completed"`).
    public func createWebhook(url: String, event: String) async throws -> ApiResponse<Webhook> {
        let body = CreateWebhookRequest(url: url, event: event)
        return try await client.request(method: "POST", path: "/v1/webhooks", body: body)
    }

    /// Update an existing webhook's URL or event.
    public func updateWebhook(_ webhookId: Int, url: String, event: String) async throws -> ApiResponse<Webhook> {
        let body = CreateWebhookRequest(url: url, event: event)
        return try await client.request(method: "PUT", path: "/v1/webhooks/\(webhookId)", body: body)
    }

    /// Delete a webhook endpoint.
    public func deleteWebhook(_ webhookId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "DELETE", path: "/v1/webhooks/\(webhookId)")
    }
}
