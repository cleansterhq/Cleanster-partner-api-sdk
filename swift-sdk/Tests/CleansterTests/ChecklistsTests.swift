import XCTest
@testable import Cleanster

final class ChecklistsTests: XCTestCase {

    var mock: MockNetworkSession!
    var client: CleansterClient!

    override func setUp() {
        mock   = MockNetworkSession()
        client = CleansterClient(accessKey: "test-key", baseURL: CleansterClient.sandboxBaseURL, session: mock)
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
        _ = try await client.checklists.createChecklist(name: "Standard", items: ["Vacuum"])
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testCreateChecklist_correctPath() async throws {
        mock.succeed(with: ["id": 77, "name": "Standard"])
        _ = try await client.checklists.createChecklist(name: "Standard", items: ["Vacuum"])
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/checklist") == true)
    }

    func testCreateChecklist_encodesName() async throws {
        mock.succeed(with: ["id": 77])
        _ = try await client.checklists.createChecklist(name: "Deep Clean", items: ["Mop"])
        XCTAssertEqual(mock.capturedBody?["name"] as? String, "Deep Clean")
    }

    func testCreateChecklist_encodesItems() async throws {
        mock.succeed(with: ["id": 77])
        _ = try await client.checklists.createChecklist(name: "Test", items: ["Vacuum", "Mop", "Wipe"])
        XCTAssertEqual(mock.capturedBody?["items"] as? [String], ["Vacuum", "Mop", "Wipe"])
    }

    func testCreateChecklist_decodesId() async throws {
        mock.succeed(with: ["id": 42, "name": "My List"])
        let resp = try await client.checklists.createChecklist(name: "My List", items: ["Task 1"])
        XCTAssertEqual(resp.data?.id, 42)
    }

    func testUpdateChecklist_sendsPUT() async throws {
        mock.succeed(with: ["id": 77])
        _ = try await client.checklists.updateChecklist(77, name: "Updated", items: ["New task"])
        XCTAssertEqual(mock.capturedMethod, "PUT")
    }

    func testUpdateChecklist_correctPath() async throws {
        mock.succeed(with: ["id": 77])
        _ = try await client.checklists.updateChecklist(77, name: "Updated", items: ["New task"])
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/checklist/77") == true)
    }

    func testUpdateChecklist_encodesNewName() async throws {
        mock.succeed(with: ["id": 77])
        _ = try await client.checklists.updateChecklist(77, name: "Renamed", items: ["Task"])
        XCTAssertEqual(mock.capturedBody?["name"] as? String, "Renamed")
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
        _ = try await client.checklists.uploadChecklistImage(77, imageData: imageData, fileName: "photo.jpg")
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/checklist/77/upload") == true)
    }

    func testUploadChecklistImage_usesMultipart() async throws {
        mock.succeedEmpty()
        let imageData = Data([0x89, 0x50, 0x4E])
        _ = try await client.checklists.uploadChecklistImage(77, imageData: imageData, fileName: "image.png")
        XCTAssertTrue(mock.capturedHeaders?["Content-Type"]?.contains("multipart/form-data") == true)
    }
}
