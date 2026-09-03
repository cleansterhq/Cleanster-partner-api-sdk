import XCTest
@testable import Cleanster

final class ChecklistsTests: XCTestCase {

    var mock: MockNetworkSession!
    var client: CleansterClient!

    override func setUp() {
        mock   = MockNetworkSession()
        client = CleansterClient(accessKey: "test-key", baseURL: CleansterClient.sandboxBaseURL, session: mock)
    }

    private func task() -> ChecklistTask {
        ChecklistTask(
            imageName: "kitchen.png",
            title: "Kitchen",
            totalSubtasks: 1,
            subtasks: [
                ChecklistSubtask(
                    description: "Photograph oven",
                    flagRequestPhotos: true,
                    photos: ["https://cdn.example/oven.jpg"]
                )
            ]
        )
    }

    func testListChecklists_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.checklists.listChecklists()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testListChecklists_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.checklists.listChecklists()
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/checklist") == true)
    }

    func testListChecklists_decodesStructuredChecklists() async throws {
        mock.succeedWithArray([[
            "id": 77, "is_default": true, "disabled": false, "title": "Deep Clean",
            "type": "CUSTOM", "totalTasks": 1, "totalSubTasks": 1,
            "tasks": [[
                "image_name": "kitchen.png", "title": "Kitchen", "totalSubtasks": 1,
                "subtasks": [[
                    "description": "Photograph oven", "flag_request_photos": true,
                    "photos": ["https://cdn.example/oven.jpg"]
                ]]
            ]]
        ]])
        let response = try await client.checklists.listChecklists()
        let checklist = try XCTUnwrap(response.data?.first)
        XCTAssertEqual(checklist.title, "Deep Clean")
        XCTAssertTrue(checklist.isDefault == true)
        XCTAssertEqual(checklist.tasks?.first?.imageName, "kitchen.png")
        XCTAssertTrue(checklist.tasks?.first?.subtasks.first?.flagRequestPhotos == true)
        XCTAssertEqual(checklist.tasks?.first?.subtasks.first?.photos, ["https://cdn.example/oven.jpg"])
    }

    func testGetChecklist_sendsGET() async throws {
        mock.succeed(with: ["id": 77, "name": "Deep Clean"])
        _ = try await client.checklists.getChecklist(77)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetChecklist_interpolatesId() async throws {
        mock.succeed(with: ["id": 77, "name": "Deep Clean"])
        _ = try await client.checklists.getChecklist(77)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/checklist/77") == true)
    }

    func testCreateChecklist_sendsPOST() async throws {
        mock.succeed(with: ["id": 77, "name": "Standard"])
        _ = try await client.checklists.createChecklist(title: "Standard", tasks: [task()])
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testCreateChecklist_correctPath() async throws {
        mock.succeed(with: ["id": 77, "name": "Standard"])
        _ = try await client.checklists.createChecklist(title: "Standard", tasks: [task()])
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/checklist") == true)
    }

    func testCreateChecklist_encodesExactStructuredPayload() async throws {
        mock.succeed(with: ["id": 77])
        _ = try await client.checklists.createChecklist(title: "Deep Clean", tasks: [task()])
        let body = try XCTUnwrap(mock.capturedBody)
        XCTAssertEqual(Set(body.keys), Set(["title", "tasks"]))
        XCTAssertEqual(body["title"] as? String, "Deep Clean")
        let taskBody = try XCTUnwrap((body["tasks"] as? [[String: Any]])?.first)
        XCTAssertEqual(Set(taskBody.keys), Set(["image_name", "title", "totalSubtasks", "subtasks"]))
        let subtaskBody = try XCTUnwrap((taskBody["subtasks"] as? [[String: Any]])?.first)
        XCTAssertEqual(Set(subtaskBody.keys), Set(["description", "flag_request_photos", "photos"]))
    }

    func testCreateChecklist_decodesId() async throws {
        mock.succeed(with: ["id": 42, "name": "My List"])
        let resp = try await client.checklists.createChecklist(title: "My List", tasks: [task()])
        XCTAssertEqual(resp.data?.id, 42)
    }

    func testChecklistItem_decodesImageURL() async throws {
        mock.succeed(with: [
            "id": 77,
            "name": "Standard",
            "items": [[
                "id": 1,
                "description": "Photograph oven",
                "isCompleted": true,
                "imageUrl": "https://cdn.example/checklist/oven.jpg"
            ]]
        ])
        let response = try await client.checklists.getChecklist(77)
        XCTAssertEqual(response.data?.items?.first?.imageUrl, "https://cdn.example/checklist/oven.jpg")
    }

    func testUpdateChecklist_sendsPUT() async throws {
        mock.succeed(with: ["id": 77])
        _ = try await client.checklists.updateChecklist(77, title: "Updated", tasks: [task()])
        XCTAssertEqual(mock.capturedMethod, "PUT")
    }

    func testUpdateChecklist_correctPath() async throws {
        mock.succeed(with: ["id": 77])
        _ = try await client.checklists.updateChecklist(77, title: "Updated", tasks: [task()])
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/checklist/77") == true)
    }

    func testUpdateChecklist_encodesOnlyTitleAndTasks() async throws {
        mock.succeed(with: ["id": 77])
        _ = try await client.checklists.updateChecklist(77, title: "Renamed", tasks: [task()])
        let body = try XCTUnwrap(mock.capturedBody)
        XCTAssertEqual(Set(body.keys), Set(["title", "tasks"]))
    }

    func testDeleteChecklist_sendsDELETE() async throws {
        mock.succeedEmpty()
        _ = try await client.checklists.deleteChecklist(77)
        XCTAssertEqual(mock.capturedMethod, "DELETE")
    }

    func testDeleteChecklist_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.checklists.deleteChecklist(77)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/checklist/77") == true)
    }

    func testUploadChecklistImage_correctPath() async throws {
        mock.succeedEmpty()
        let imageData = Data([0xFF, 0xD8, 0xFF])
        _ = try await client.checklists.uploadChecklistImage(imageData: imageData, fileName: "photo.jpg")
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/checklist/upload-image") == true)
    }

    func testUploadChecklistImage_usesMultipart() async throws {
        mock.succeedEmpty()
        let imageData = Data([0x89, 0x50, 0x4E])
        _ = try await client.checklists.uploadChecklistImage(imageData: imageData, fileName: "image.png")
        XCTAssertTrue(mock.capturedHeaders?["Content-Type"]?.contains("multipart/form-data") == true)
    }
}
