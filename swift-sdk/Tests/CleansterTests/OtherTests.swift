import XCTest
@testable import Cleanster

final class OtherTests: XCTestCase {

    var mock: MockNetworkSession!
    var client: CleansterClient!

    override func setUp() {
        mock   = MockNetworkSession()
        client = CleansterClient(accessKey: "test-key", baseURL: CleansterClient.sandboxBaseURL, session: mock)
    }

    func testGetServices_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.other.getServices()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetServices_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.other.getServices()
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/services") == true)
    }

    func testGetPlans_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.other.getPlans(propertyId: 1004)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetPlans_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.other.getPlans(propertyId: 1004)
        XCTAssertTrue(mock.capturedURL?.contains("/v1/plans") == true)
    }

    func testGetPlans_encodesPropertyId() async throws {
        mock.succeedWithArray([])
        _ = try await client.other.getPlans(propertyId: 1004)
        XCTAssertTrue(mock.capturedURL?.contains("propertyId=1004") == true)
    }

    func testGetRecommendedHours_sendsGET() async throws {
        mock.succeed(with: ["hours": 3.0])
        _ = try await client.other.getRecommendedHours(propertyId: 1004, roomCount: 2, bathroomCount: 1)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetRecommendedHours_correctPath() async throws {
        mock.succeed(with: ["hours": 3.0])
        _ = try await client.other.getRecommendedHours(propertyId: 1004, roomCount: 2, bathroomCount: 1)
        XCTAssertTrue(mock.capturedURL?.contains("/v1/recommended-hours") == true)
    }

    func testGetRecommendedHours_encodesRoomCount() async throws {
        mock.succeed(with: ["hours": 3.0])
        _ = try await client.other.getRecommendedHours(propertyId: 1004, roomCount: 3, bathroomCount: 2)
        XCTAssertTrue(mock.capturedURL?.contains("roomCount=3") == true)
    }

    func testGetRecommendedHours_encodesBathroomCount() async throws {
        mock.succeed(with: ["hours": 3.0])
        _ = try await client.other.getRecommendedHours(propertyId: 1004, roomCount: 2, bathroomCount: 2)
        XCTAssertTrue(mock.capturedURL?.contains("bathroomCount=2") == true)
    }

    func testGetRecommendedHours_decodesHours() async throws {
        mock.succeed(with: ["hours": 4.5])
        let resp = try await client.other.getRecommendedHours(propertyId: 1004, roomCount: 3, bathroomCount: 2)
        XCTAssertEqual(resp.data?.hours, 4.5)
    }

    func testGetCostEstimate_sendsPOST() async throws {
        mock.succeed(with: ["subtotal": 105.0, "discount": 21.0, "total": 84.0])
        let req = CostEstimateRequest(propertyId: 1004, planId: 2, hours: 3.0)
        _ = try await client.other.getCostEstimate(req)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testGetCostEstimate_correctPath() async throws {
        mock.succeed(with: ["subtotal": 105.0, "discount": 0.0, "total": 105.0])
        let req = CostEstimateRequest(propertyId: 1004, planId: 2, hours: 3.0)
        _ = try await client.other.getCostEstimate(req)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/cost-estimate") == true)
    }

    func testGetCostEstimate_encodesCoupon() async throws {
        mock.succeed(with: ["subtotal": 105.0, "discount": 52.5, "total": 52.5])
        let req = CostEstimateRequest(propertyId: 1004, planId: 2, hours: 3.0, couponCode: "50POFF")
        _ = try await client.other.getCostEstimate(req)
        XCTAssertEqual(mock.capturedBody?["couponCode"] as? String, "50POFF")
    }

    func testGetCostEstimate_decodesTotal() async throws {
        mock.succeed(with: ["subtotal": 105.0, "discount": 21.0, "total": 84.0])
        let req = CostEstimateRequest(propertyId: 1004, planId: 2, hours: 3.0)
        let resp = try await client.other.getCostEstimate(req)
        XCTAssertEqual(resp.data?.total, 84.0)
    }

    func testGetCleaningExtras_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.other.getCleaningExtras(serviceId: 1)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetCleaningExtras_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.other.getCleaningExtras(serviceId: 1)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/cleaning-extras/1") == true)
    }

    func testGetAvailableCleaners_sendsPOST() async throws {
        mock.succeedWithArray([])
        let req = AvailableCleanersRequest(propertyId: 1004, date: "2025-09-15", time: "09:00")
        _ = try await client.other.getAvailableCleaners(req)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testGetAvailableCleaners_correctPath() async throws {
        mock.succeedWithArray([])
        let req = AvailableCleanersRequest(propertyId: 1004, date: "2025-09-15", time: "09:00")
        _ = try await client.other.getAvailableCleaners(req)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/available-cleaners") == true)
    }

    func testGetAvailableCleaners_encodesDate() async throws {
        mock.succeedWithArray([])
        let req = AvailableCleanersRequest(propertyId: 1004, date: "2025-09-15", time: "09:00")
        _ = try await client.other.getAvailableCleaners(req)
        XCTAssertEqual(mock.capturedBody?["date"] as? String, "2025-09-15")
    }

    func testGetCoupons_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.other.getCoupons()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetCoupons_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.other.getCoupons()
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/coupons") == true)
    }
}
