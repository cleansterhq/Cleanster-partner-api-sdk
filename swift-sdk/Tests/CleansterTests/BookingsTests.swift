import XCTest
@testable import Cleanster

final class BookingsTests: XCTestCase {

    var mock: MockNetworkSession!
    var client: CleansterClient!

    override func setUp() {
        mock   = MockNetworkSession()
        client = CleansterClient(accessKey: "test-key", baseURL: CleansterClient.sandboxBaseURL, session: mock)
    }

    // MARK: - getBookings

    func testGetBookings_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.bookings.getBookings()
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetBookings_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.bookings.getBookings()
        XCTAssertTrue(mock.capturedURL?.contains("/v1/bookings") == true)
    }

    func testGetBookings_withStatus() async throws {
        mock.succeedWithArray([])
        _ = try await client.bookings.getBookings(status: "OPEN")
        XCTAssertTrue(mock.capturedURL?.contains("status=OPEN") == true)
    }

    func testGetBookings_withPageNo() async throws {
        mock.succeedWithArray([])
        _ = try await client.bookings.getBookings(pageNo: 2)
        XCTAssertTrue(mock.capturedURL?.contains("pageNo=2") == true)
    }

    func testGetBookings_withStatusAndPage() async throws {
        mock.succeedWithArray([])
        _ = try await client.bookings.getBookings(pageNo: 3, status: "COMPLETED")
        XCTAssertTrue(mock.capturedURL?.contains("COMPLETED") == true)
        XCTAssertTrue(mock.capturedURL?.contains("pageNo=3") == true)
    }

    // MARK: - createBooking

    func testCreateBooking_sendsPOST() async throws {
        mock.succeed(with: ["id": 101, "status": "OPEN"])
        let req = CreateBookingRequest(
            date: "2025-09-15", time: "10:00", propertyId: 1004,
            planId: 2, hours: 3.0, roomCount: 2, bathroomCount: 1,
            extraSupplies: false, paymentMethodId: 55
        )
        _ = try await client.bookings.createBooking(req)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testCreateBooking_correctPath() async throws {
        mock.succeed(with: ["id": 101])
        let req = CreateBookingRequest(
            date: "2025-09-15", time: "10:00", propertyId: 1004,
            planId: 2, hours: 3.0, roomCount: 2, bathroomCount: 1,
            extraSupplies: false, paymentMethodId: 55
        )
        _ = try await client.bookings.createBooking(req)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/bookings/create") == true)
    }

    func testCreateBooking_encodesDate() async throws {
        mock.succeed(with: ["id": 101])
        let req = CreateBookingRequest(
            date: "2025-09-15", time: "10:00", propertyId: 1004,
            planId: 2, hours: 3.0, roomCount: 2, bathroomCount: 1,
            extraSupplies: false, paymentMethodId: 55
        )
        _ = try await client.bookings.createBooking(req)
        XCTAssertEqual(mock.capturedBody?["date"] as? String, "2025-09-15")
    }

    func testCreateBooking_encodesPropertyId() async throws {
        mock.succeed(with: ["id": 101])
        let req = CreateBookingRequest(
            date: "2025-09-15", time: "10:00", propertyId: 1004,
            planId: 2, hours: 3.0, roomCount: 2, bathroomCount: 1,
            extraSupplies: false, paymentMethodId: 55
        )
        _ = try await client.bookings.createBooking(req)
        XCTAssertEqual(mock.capturedBody?["propertyId"] as? Int, 1004)
    }

    func testCreateBooking_encodesCouponCode() async throws {
        mock.succeed(with: ["id": 101])
        let req = CreateBookingRequest(
            date: "2025-09-15", time: "10:00", propertyId: 1004,
            planId: 2, hours: 3.0, roomCount: 2, bathroomCount: 1,
            extraSupplies: false, paymentMethodId: 55,
            couponCode: "20POFF"
        )
        _ = try await client.bookings.createBooking(req)
        XCTAssertEqual(mock.capturedBody?["couponCode"] as? String, "20POFF")
    }

    func testCreateBooking_encodesExtras() async throws {
        mock.succeed(with: ["id": 101])
        let req = CreateBookingRequest(
            date: "2025-09-15", time: "10:00", propertyId: 1004,
            planId: 2, hours: 3.0, roomCount: 2, bathroomCount: 1,
            extraSupplies: false, paymentMethodId: 55,
            extras: [3, 7]
        )
        _ = try await client.bookings.createBooking(req)
        XCTAssertEqual(mock.capturedBody?["extras"] as? [Int], [3, 7])
    }

    func testCreateBooking_decodesId() async throws {
        mock.succeed(with: ["id": 16459, "status": "OPEN"])
        let req = CreateBookingRequest(
            date: "2025-09-15", time: "10:00", propertyId: 1004,
            planId: 2, hours: 3.0, roomCount: 2, bathroomCount: 1,
            extraSupplies: false, paymentMethodId: 55
        )
        let resp = try await client.bookings.createBooking(req)
        XCTAssertEqual(resp.data?.id, 16459)
    }

    // MARK: - getBookingDetails

    func testGetBookingDetails_sendsGET() async throws {
        mock.succeed(with: ["id": 16459])
        _ = try await client.bookings.getBookingDetails(16459)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetBookingDetails_interpolatesId() async throws {
        mock.succeed(with: ["id": 16459])
        _ = try await client.bookings.getBookingDetails(16459)
        XCTAssertTrue(mock.capturedURL?.contains("16459") == true)
    }

    // MARK: - cancelBooking

    func testCancelBooking_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.cancelBooking(16459)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testCancelBooking_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.cancelBooking(16459)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/bookings/16459/cancel") == true)
    }

    func testCancelBooking_encodesReason() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.cancelBooking(16459, reason: "Customer request")
        XCTAssertEqual(mock.capturedBody?["reason"] as? String, "Customer request")
    }

    // MARK: - rescheduleBooking

    func testRescheduleBooking_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.rescheduleBooking(16459, date: "2025-10-01", time: "11:00")
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testRescheduleBooking_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.rescheduleBooking(16459, date: "2025-10-01", time: "11:00")
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/bookings/16459/reschedule") == true)
    }

    func testRescheduleBooking_encodesDate() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.rescheduleBooking(16459, date: "2025-10-01", time: "11:00")
        XCTAssertEqual(mock.capturedBody?["date"] as? String, "2025-10-01")
    }

    // MARK: - assignCleaner

    func testAssignCleaner_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.assignCleaner(16459, cleanerId: 789)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testAssignCleaner_encodesCleanerId() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.assignCleaner(16459, cleanerId: 789)
        XCTAssertEqual(mock.capturedBody?["cleanerId"] as? Int, 789)
    }

    // MARK: - removeAssignedCleaner

    func testRemoveAssignedCleaner_sendsDELETE() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.removeAssignedCleaner(16459)
        XCTAssertEqual(mock.capturedMethod, "DELETE")
    }

    func testRemoveAssignedCleaner_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.removeAssignedCleaner(16459)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/bookings/16459/cleaner") == true)
    }

    // MARK: - adjustHours

    func testAdjustHours_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.adjustHours(16459, hours: 4.5)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testAdjustHours_encodesHours() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.adjustHours(16459, hours: 4.5)
        XCTAssertEqual(mock.capturedBody?["hours"] as? Double, 4.5)
    }

    // MARK: - payExpenses

    func testPayExpenses_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.payExpenses(16459, paymentMethodId: 55)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testPayExpenses_encodesPaymentMethodId() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.payExpenses(16459, paymentMethodId: 55)
        XCTAssertEqual(mock.capturedBody?["paymentMethodId"] as? Int, 55)
    }

    // MARK: - submitFeedback

    func testSubmitFeedback_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.submitFeedback(16459, rating: 5)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testSubmitFeedback_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.submitFeedback(16459, rating: 5)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/bookings/16459/feedback") == true)
    }

    func testSubmitFeedback_encodesRating() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.submitFeedback(16459, rating: 4)
        XCTAssertEqual(mock.capturedBody?["rating"] as? Int, 4)
    }

    func testSubmitFeedback_encodesComment() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.submitFeedback(16459, rating: 5, comment: "Great service!")
        XCTAssertEqual(mock.capturedBody?["comment"] as? String, "Great service!")
    }

    // MARK: - addTip

    func testAddTip_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.addTip(16459, amount: 10.0, paymentMethodId: 55)
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testAddTip_encodesAmount() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.addTip(16459, amount: 15.0, paymentMethodId: 55)
        XCTAssertEqual(mock.capturedBody?["amount"] as? Double, 15.0)
    }

    // MARK: - chat

    func testGetChat_sendsGET() async throws {
        mock.succeedWithArray([])
        _ = try await client.bookings.getChat(17142)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetChat_correctPath() async throws {
        mock.succeedWithArray([])
        _ = try await client.bookings.getChat(17142)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/bookings/17142/chat") == true)
    }

    func testSendMessage_sendsPOST() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.sendMessage(17142, message: "On the way!")
        XCTAssertEqual(mock.capturedMethod, "POST")
    }

    func testSendMessage_encodesMessage() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.sendMessage(17142, message: "On the way!")
        XCTAssertEqual(mock.capturedBody?["message"] as? String, "On the way!")
    }

    func testDeleteMessage_sendsDELETE() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.deleteMessage(17142, messageId: "msg-001")
        XCTAssertEqual(mock.capturedMethod, "DELETE")
    }

    func testDeleteMessage_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.deleteMessage(17142, messageId: "msg-001")
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/bookings/17142/chat/msg-001") == true)
    }

    // MARK: - assignChecklistToBooking

    func testAssignChecklist_sendsPUT() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.assignChecklistToBooking(16459, checklistId: 77)
        XCTAssertEqual(mock.capturedMethod, "PUT")
    }

    func testAssignChecklist_correctPath() async throws {
        mock.succeedEmpty()
        _ = try await client.bookings.assignChecklistToBooking(16459, checklistId: 77)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/bookings/16459/checklist/77") == true)
    }

    // MARK: - inspection

    func testGetInspection_sendsGET() async throws {
        mock.succeed(with: [:])
        _ = try await client.bookings.getBookingInspection(16459)
        XCTAssertEqual(mock.capturedMethod, "GET")
    }

    func testGetInspectionDetails_correctPath() async throws {
        mock.succeed(with: [:])
        _ = try await client.bookings.getBookingInspectionDetails(16459)
        XCTAssertTrue(mock.capturedURL?.hasSuffix("/v1/bookings/16459/inspection/details") == true)
    }
}
