import XCTest
@testable import Cleanster

final class UsersTests: XCTestCase {

    var mock: MockNetworkSession!
    var client: CleansterClient!

    override func setUp() {
        mock   = MockNetworkSession()
        client = CleansterClient(accessKey: "test-key", baseURL: CleansterClient.sandboxBaseURL, session: mock)
    }

    // MARK: - createUser

    func testCreateUser_sendsCorrectMethod() async throws {
        mock.succeed(with: ["id": 1, "email": "a@b.com"])
        _ = try await client.users.createUser(email: "a@b.com", firstName: "A", lastName: "B")
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testCreateUser_sendsCorrectPath() async throws {
        mock.succeed(with: ["id": 1, "email": "a@b.com"])
        _ = try await client.users.createUser(email: "a@b.com", firstName: "A", lastName: "B")
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/user/account") == true)
    }

    func testCreateUser_sendsAccessKeyHeader() async throws {
        mock.succeed(with: ["id": 1, "email": "a@b.com"])
        _ = try await client.users.createUser(email: "a@b.com", firstName: "A", lastName: "B")
        XCTAssertEqual(mock.capturedHeaders?["access-key"], "test-key")
    }

    func testCreateUser_encodesEmail() async throws {
        mock.succeed(with: ["id": 1])
        _ = try await client.users.createUser(email: "alice@example.com", firstName: "Alice", lastName: "Smith")
        XCTAssertEqual(mock.capturedBody?["email"] as? String, "alice@example.com")
    }

    func testCreateUser_encodesFirstName() async throws {
        mock.succeed(with: ["id": 1])
        _ = try await client.users.createUser(email: "a@b.com", firstName: "Alice", lastName: "Smith")
        XCTAssertEqual(mock.capturedBody?["firstName"] as? String, "Alice")
    }

    func testCreateUser_encodesLastName() async throws {
        mock.succeed(with: ["id": 1])
        _ = try await client.users.createUser(email: "a@b.com", firstName: "Alice", lastName: "Smith")
        XCTAssertEqual(mock.capturedBody?["lastName"] as? String, "Smith")
    }

    func testCreateUser_encodesOptionalPhone() async throws {
        mock.succeed(with: ["id": 1])
        _ = try await client.users.createUser(email: "a@b.com", firstName: "A", lastName: "B", phone: "+14155551234")
        XCTAssertEqual(mock.capturedBody?["phone"] as? String, "+14155551234")
    }

    func testCreateUser_decodesResponseId() async throws {
        mock.succeed(with: ["id": 42, "email": "a@b.com"])
        let resp = try await client.users.createUser(email: "a@b.com", firstName: "A", lastName: "B")
        XCTAssertEqual(resp.data?.id, 42)
    }

    // MARK: - fetchAccessToken

    func testFetchAccessToken_sendsCorrectMethod() async throws {
        mock.succeed(with: ["token": "jwt-abc"])
        _ = try await client.users.fetchAccessToken(123)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testFetchAccessToken_sendsCorrectPath() async throws {
        mock.succeed(with: ["token": "jwt-abc"])
        _ = try await client.users.fetchAccessToken(123)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/user/access-token/123") == true)
    }

    func testFetchAccessToken_decodesToken() async throws {
        mock.succeed(with: ["token": "eyJhbGciOi"])
        let resp = try await client.users.fetchAccessToken(123)
        XCTAssertEqual(resp.data?.token, "eyJhbGciOi")
    }

    func testFetchAccessToken_interpolatesUserId() async throws {
        mock.succeed(with: ["token": "jwt"])
        _ = try await client.users.fetchAccessToken(9999)
        XCTAssertTrue(mock.capturedURL?.contains("9999") == true)
    }

    // MARK: - verifyJwt

    func testVerifyJwt_sendsCorrectMethod() async throws {
        mock.succeedEmpty()
        _ = try await client.users.verifyJwt("jwt-token")
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testVerifyJwt_sendsCorrectPath() async throws {
        mock.succeedEmpty()
        _ = try await client.users.verifyJwt("jwt-token")
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/user/verify-jwt") == true)
    }

    func testVerifyJwt_encodesToken() async throws {
        mock.succeedEmpty()
        _ = try await client.users.verifyJwt("my-secret-jwt")
        XCTAssertEqual(mock.capturedBody?["token"] as? String, "my-secret-jwt")
    }

    // MARK: - setToken

    func testSetToken_updatesClientToken() {
        client.setToken("new-jwt")
        XCTAssertEqual(client.token, "new-jwt")
    }

    func testSetToken_sentAsHeader() async throws {
        client.setToken("user-jwt-123")
        mock.succeed(with: ["id": 1, "email": "a@b.com"])
        _ = try await client.users.createUser(email: "a@b.com", firstName: "A", lastName: "B")
        XCTAssertEqual(mock.capturedHeaders?["token"], "user-jwt-123")
    }
}
