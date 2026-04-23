import Foundation

/// API methods for managing reusable cleaning task lists.
public final class ChecklistsApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// List all checklists on the partner account.
    public func listChecklists() async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/checklist")
    }

    /// Retrieve a single checklist and all its items.
    public func getChecklist(_ checklistId: Int) async throws -> ApiResponse<Checklist> {
        return try await client.request(method: "GET", path: "/v1/checklist/\(checklistId)")
    }

    /// Create a new checklist.
    ///
    /// - Parameters:
    ///   - name: Display name of the checklist.
    ///   - items: Array of task description strings.
    public func createChecklist(name: String, items: [String]) async throws -> ApiResponse<Checklist> {
        let body = CreateChecklistRequest(name: name, items: items)
        return try await client.request(method: "POST", path: "/v1/checklist", body: body)
    }

    /// Replace an existing checklist's name and items entirely.
    public func updateChecklist(_ checklistId: Int, name: String, items: [String]) async throws -> ApiResponse<Checklist> {
        let body = CreateChecklistRequest(name: name, items: items)
        return try await client.request(method: "PUT", path: "/v1/checklist/\(checklistId)", body: body)
    }

    /// Permanently delete a checklist.
    public func deleteChecklist(_ checklistId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "DELETE", path: "/v1/checklist/\(checklistId)")
    }
}
