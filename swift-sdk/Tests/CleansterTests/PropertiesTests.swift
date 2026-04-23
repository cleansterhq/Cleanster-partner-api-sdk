import XCTest
@testable import Cleanster

final class PropertiesTests: XCTestCase {

    var mock: MockNetworkSession!
    var client: CleansterClient!

    override func setUp() {
        mock   = MockNetworkSession()
        client = CleansterClient(accessKey: "test-key", baseURL: CleansterClient.sandboxBaseURL, session: mock)
    }

    // MARK: - listProperties

    func testListProperties_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.properties.listProperties()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testListProperties_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.properties.listProperties()
        XCTAssertTrue(mock.capturedURL?.contains("/v1/properties") == true)
    }

    func testListProperties_withServiceId() async throws {
        mock.succeedWithArray([])
        _ = try await client.properties.listProperties(serviceId: 1)
        XCTAssertTrue(mock.capturedURL?.contains("serviceId=1") == true)
    }

    func testListProperties_noServiceId_noQueryParam() async throws {
        mock.succeedWithArray([])
        _ = try await client.properties.listProperties()
        XCTAssertFalse(mock.capturedURL?.contains("serviceId") == true)
    }

    // MARK: - addProperty

    func testAddProperty_sendsPOST() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Home", address: "123 Main", city: "Atlanta",
            country: "US", roomCount: 2, bathroomCount: 1, serviceId: 1
        )
        _ = try await client.properties.addProperty(req)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testAddProperty_correctPath() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Home", address: "123 Main", city: "Atlanta",
            country: "US", roomCount: 2, bathroomCount: 1, serviceId: 1
        )
        _ = try await client.properties.addProperty(req)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/properties") == true)
    }

    func testAddProperty_encodesName() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Downtown Loft", address: "123 Main", city: "Atlanta",
            country: "US", roomCount: 2, bathroomCount: 1, serviceId: 1
        )
        _ = try await client.properties.addProperty(req)
        XCTAssertEqual(mock.capturedBody?["name"] as? String, "Downtown Loft")
    }

    func testAddProperty_encodesOptionalState() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Home", address: "123 Main", city: "Atlanta",
            country: "US", roomCount: 2, bathroomCount: 1, serviceId: 1,
            state: "GA"
        )
        _ = try await client.properties.addProperty(req)
        XCTAssertEqual(mock.capturedBody?["state"] as? String, "GA")
    }

    func testAddProperty_encodesOptionalZip() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Home", address: "123 Main", city: "Atlanta",
            country: "US", roomCount: 2, bathroomCount: 1, serviceId: 1,
            zip: "30301"
        )
        _ = try await client.properties.addProperty(req)
        XCTAssertEqual(mock.capturedBody?["zip"] as? String, "30301")
    }

    func testAddProperty_encodesOptionalTimezone() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Home", address: "123 Main", city: "Atlanta",
            country: "US", roomCount: 2, bathroomCount: 1, serviceId: 1,
            timezone: "America/New_York"
        )
        _ = try await client.properties.addProperty(req)
        XCTAssertEqual(mock.capturedBody?["timezone"] as? String, "America/New_York")
    }

    func testAddProperty_encodesLatLng() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Home", address: "123 Main", city: "Atlanta",
            country: "US", roomCount: 2, bathroomCount: 1, serviceId: 1,
            latitude: 33.749, longitude: -84.388
        )
        _ = try await client.properties.addProperty(req)
        XCTAssertEqual(mock.capturedBody?["latitude"] as? Double, 33.749, accuracy: 0.001)
        XCTAssertEqual(mock.capturedBody?["longitude"] as? Double, -84.388, accuracy: 0.001)
    }

    func testAddProperty_decodesId() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Home", address: "123 Main", city: "Atlanta",
            country: "US", roomCount: 2, bathroomCount: 1, serviceId: 1
        )
        let resp = try await client.properties.addProperty(req)
        XCTAssertEqual(resp.data?.id, 1004)
    }

    // MARK: - getProperty

    func testGetProperty_sendsGET() async throws {
        mock.succeed(with: ["id": 1004])
        _ = try await client.properties.getProperty(1004)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetProperty_interpolatesId() async throws {
        mock.succeed(with: ["id": 1004])
        _ = try await client.properties.getProperty(1004)
        XCTAssertTrue(mock.capturedURL?.contains("1004") == true)
    }

    // MARK: - updateProperty

    func testUpdateProperty_sendsPUT() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Updated", address: "456 Oak", city: "Atlanta",
            country: "US", roomCount: 3, bathroomCount: 2, serviceId: 1
        )
        _ = try await client.properties.updateProperty(1004, request: req)
        XCTAssertEqual(mock.capturedMethod, "PUT")
    }

    func testUpdateProperty_correctPath() async throws {
        mock.succeed(with: ["id": 1004])
        let req = CreatePropertyRequest(
            name: "Updated", address: "456 Oak", city: "Atlanta",
            country: "US", roomCount: 3, bathroomCount: 2, serviceId: 1
        )
        _ = try await client.properties.updateProperty(1004, request: req)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/properties/1004") == true)
    }

    // MARK: - deleteProperty

    func testDeleteProperty_sendsDELETE() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.deleteProperty(1004)
        XCTAssertEqual(mock.capturedMethod, "DELETE")
    }

    func testDeleteProperty_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.deleteProperty(1004)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/properties/1004") == true)
    }

    // MARK: - enableOrDisableProperty

    func testEnableProperty_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.enableOrDisableProperty(1004, enabled: true)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testEnableProperty_encodesEnabled() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.enableOrDisableProperty(1004, enabled: false)
        XCTAssertEqual(mock.capturedBody?["enabled"] as? Bool, false)
    }

    func testEnableProperty_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.enableOrDisableProperty(1004, enabled: true)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/properties/1004/enable-disable") == true)
    }

    // MARK: - cleaners

    func testGetPropertyCleaners_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.properties.getPropertyCleaners(1004)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testAddCleanerToProperty_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.addCleanerToProperty(1004, cleanerId: 789)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testAddCleanerToProperty_encodesCleanerId() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.addCleanerToProperty(1004, cleanerId: 789)
        XCTAssertEqual(mock.capturedBody?["cleanerId"] as? Int, 789)
    }

    func testRemoveCleanerFromProperty_sendsDELETE() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.removeCleanerFromProperty(1004, cleanerId: 789)
        XCTAssertEqual(mock.capturedMethod, "DELETE")
    }

    func testRemoveCleanerFromProperty_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.removeCleanerFromProperty(1004, cleanerId: 789)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/properties/1004/cleaners/789") == true)
    }

    // MARK: - iCal

    func testSetICalLink_sendsPUT() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.setICalLink(1004, icalLink: "https://airbnb.com/ical.ics")
        XCTAssertEqual(mock.capturedMethod, "PUT")
    }

    func testSetICalLink_encodesLink() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.setICalLink(1004, icalLink: "https://airbnb.com/ical.ics")
        XCTAssertEqual(mock.capturedBody?["icalLink"] as? String, "https://airbnb.com/ical.ics")
    }

    func testGetICalLink_sendsGET() async throws {
        mock.succeed(with: ["icalLink": "https://example.com/cal.ics"])
        _ = try await client.properties.getICalLink(1004)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testDeleteICalLink_sendsDELETE() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.deleteICalLink(1004, icalLink: "https://airbnb.com/ical.ics")
        XCTAssertEqual(mock.capturedMethod, "DELETE")
    }

    // MARK: - setDefaultChecklist

    func testSetDefaultChecklist_sendsPUT() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.setDefaultChecklist(1004, checklistId: 77)
        XCTAssertEqual(mock.capturedMethod, "PUT")
    }

    func testSetDefaultChecklist_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.setDefaultChecklist(1004, checklistId: 77)
        XCTAssertTrue(mock.capturedURL?.contains("/v1/properties/1004/checklist/77") == true)
    }

    func testSetDefaultChecklist_updateUpcoming() async throws {
        mock.succeedEmpty()
        _ = try await client.properties.setDefaultChecklist(1004, checklistId: 77, updateUpcomingBookings: true)
        XCTAssertTrue(mock.capturedURL?.contains("updateUpcomingBookings=true") == true)
    }
}
