import XCTest
@testable import Cleanster

final class BlacklistTests: XCTestCase {

    var mock: MockNetworkSession!
    var client: CleansterClient!

    override func setUp() {
        mock   = MockNetworkSession()
        client = CleansterClient(accessKey: "test-key", baseURL: CleansterClient.sandboxBaseURL, session: mock)
    }

    func testListBlacklistedCleaners_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.blacklist.listBlacklistedCleaners()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testListBlacklistedCleaners_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.blacklist.listBlacklistedCleaners()
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/blacklist/cleaner") == true)
    }

    func testAddToBlacklist_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.blacklist.addToBlacklist(cleanerId: 789)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testAddToBlacklist_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.blacklist.addToBlacklist(cleanerId: 789)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/blacklist/cleaner") == true)
    }

    func testAddToBlacklist_encodesCleanerId() async throws {
        mock.succeedEmpty()
        _ = try await client.blacklist.addToBlacklist(cleanerId: 789)
        XCTAssertEqual(mock.capturedBody?["cleanerId"] as? Int, 789)
    }

    func testAddToBlacklist_encodesOptionalReason() async throws {
        mock.succeedEmpty()
        _ = try await client.blacklist.addToBlacklist(cleanerId: 789, reason: "Repeated issues")
        XCTAssertEqual(mock.capturedBody?["reason"] as? String, "Repeated issues")
    }

    func testRemoveFromBlacklist_sendsDELETE() async throws {
        mock.succeedEmpty()
        _ = try await client.blacklist.removeFromBlacklist(cleanerId: 789)
        XCTAssertEqual(mock.capturedMethod, "DELETE")
    }

    func testRemoveFromBlacklist_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.blacklist.removeFromBlacklist(cleanerId: 789)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/blacklist/cleaner") == true)
    }

    func testRemoveFromBlacklist_encodesCleanerId() async throws {
        mock.succeedEmpty()
        _ = try await client.blacklist.removeFromBlacklist(cleanerId: 789)
        XCTAssertEqual(mock.capturedBody?["cleanerId"] as? Int, 789)
    }
}
