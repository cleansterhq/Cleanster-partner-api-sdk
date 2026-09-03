import Foundation

/// API methods for managing reusable cleaning task lists.
public final class ChecklistsApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// List all checklists on the partner account.
    public func listChecklists() async throws -> ApiResponse<[Checklist]> {
        return try await client.request(method: "GET", path: "/v1/checklist")
    }

    /// Retrieve a single checklist and all its tasks.
    public func getChecklist(_ checklistId: Int) async throws -> ApiResponse<Checklist> {
        return try await client.request(method: "GET", path: "/v1/checklist/\(checklistId)")
    }

    /// Create a new checklist.
    ///
    /// - Parameters:
    ///   - title: Display title of the checklist.
    ///   - tasks: Structured checklist tasks and their subtasks.
    public func createChecklist(title: String, tasks: [ChecklistTask]) async throws -> ApiResponse<Checklist> {
        let body = CreateChecklistRequest(title: title, tasks: tasks)
        return try await client.request(method: "POST", path: "/v1/checklist", body: body)
    }

    /// Replace an existing checklist's title and tasks entirely.
    public func updateChecklist(_ checklistId: Int, title: String, tasks: [ChecklistTask]) async throws -> ApiResponse<Checklist> {
        let body = CreateChecklistRequest(title: title, tasks: tasks)
        return try await client.request(method: "PUT", path: "/v1/checklist/\(checklistId)", body: body)
    }

    /// Permanently delete a checklist.
    public func deleteChecklist(_ checklistId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "DELETE", path: "/v1/checklist/\(checklistId)")
    }

    /// Upload an image via multipart/form-data.
    ///
    /// Sends the image as multipart/form-data in the `file` form field.
    ///
    /// - Parameters:
    ///   - imageData:   Raw bytes of the image to upload.
    ///   - fileName:    File name for the multipart part (e.g. "photo.jpg").
    public func uploadChecklistImage(
        imageData: Data,
        fileName: String = "image.jpg"
    ) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestMultipart(
            path:      "/v1/checklist/upload-image",
            imageData: imageData,
            fileName:  fileName
        )
    }
}
