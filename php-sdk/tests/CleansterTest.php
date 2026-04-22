<?php

declare(strict_types=1);

namespace Cleanster\Tests;

use Cleanster\Api\BlacklistApi;
use Cleanster\Api\BookingsApi;
use Cleanster\Api\ChecklistsApi;
use Cleanster\Api\OtherApi;
use Cleanster\Api\PaymentMethodsApi;
use Cleanster\Api\PropertiesApi;
use Cleanster\Api\UsersApi;
use Cleanster\Api\WebhooksApi;
use Cleanster\CleansterClient;
use Cleanster\Config;
use Cleanster\Exceptions\ApiException;
use Cleanster\Exceptions\AuthException;
use Cleanster\Exceptions\CleansterException;
use Cleanster\HttpClient;
use Cleanster\Models\Booking;
use Cleanster\Models\Checklist;
use Cleanster\Models\ChecklistItem;
use Cleanster\Models\PaymentMethod;
use Cleanster\Models\Property;
use Cleanster\Models\User;
use PHPUnit\Framework\MockObject\MockObject;
use PHPUnit\Framework\TestCase;

/**
 * Full test suite for the Cleanster PHP SDK.
 */
class CleansterTest extends TestCase
{
    // =========================================================================
    // Helpers
    // =========================================================================

    private function ok(mixed $data = []): array
    {
        return ['status' => 200, 'message' => 'OK', 'data' => $data];
    }

    /** @return array{CleansterClient, MockObject&HttpClient} */
    private function mockClient(): array
    {
        $config  = Config::sandbox('test-key');
        $mockHttp = $this->createMock(HttpClient::class);
        $client  = new CleansterClient($config, $mockHttp);
        return [$client, $mockHttp];
    }

    /** @return MockObject&HttpClient */
    private function mockHttp(): MockObject
    {
        return $this->createMock(HttpClient::class);
    }

    // =========================================================================
    // Config
    // =========================================================================

    public function testSandboxConfigUrl(): void
    {
        $cfg = Config::sandbox('k');
        $this->assertSame(Config::SANDBOX_BASE_URL, $cfg->baseUrl);
    }

    public function testProductionConfigUrl(): void
    {
        $cfg = Config::production('k');
        $this->assertSame(Config::PRODUCTION_BASE_URL, $cfg->baseUrl);
    }

    public function testConfigStoresAccessKey(): void
    {
        $cfg = Config::sandbox('my-key');
        $this->assertSame('my-key', $cfg->accessKey);
    }

    public function testConfigDefaultTimeout(): void
    {
        $cfg = Config::sandbox('k');
        $this->assertSame(Config::DEFAULT_TIMEOUT, $cfg->timeout);
    }

    public function testConfigBlankAccessKeyThrows(): void
    {
        $this->expectException(\InvalidArgumentException::class);
        new Config('', Config::SANDBOX_BASE_URL);
    }

    public function testConfigWhitespaceAccessKeyThrows(): void
    {
        $this->expectException(\InvalidArgumentException::class);
        new Config('   ', Config::SANDBOX_BASE_URL);
    }

    public function testConfigBlankBaseUrlThrows(): void
    {
        $this->expectException(\InvalidArgumentException::class);
        new Config('key', '');
    }

    public function testConfigCustomTimeout(): void
    {
        $cfg = new Config('key', Config::SANDBOX_BASE_URL, 60);
        $this->assertSame(60, $cfg->timeout);
    }

    // =========================================================================
    // CleansterClient — factory and services
    // =========================================================================

    public function testSandboxClientCreation(): void
    {
        $client = CleansterClient::sandbox('my-key');
        $this->assertInstanceOf(CleansterClient::class, $client);
    }

    public function testProductionClientCreation(): void
    {
        $client = CleansterClient::production('my-key');
        $this->assertInstanceOf(CleansterClient::class, $client);
    }

    public function testClientExposesBookings(): void
    {
        [$client] = $this->mockClient();
        $this->assertInstanceOf(BookingsApi::class, $client->bookings());
    }

    public function testClientExposesUsers(): void
    {
        [$client] = $this->mockClient();
        $this->assertInstanceOf(UsersApi::class, $client->users());
    }

    public function testClientExposesProperties(): void
    {
        [$client] = $this->mockClient();
        $this->assertInstanceOf(PropertiesApi::class, $client->properties());
    }

    public function testClientExposesChecklists(): void
    {
        [$client] = $this->mockClient();
        $this->assertInstanceOf(ChecklistsApi::class, $client->checklists());
    }

    public function testClientExposesOther(): void
    {
        [$client] = $this->mockClient();
        $this->assertInstanceOf(OtherApi::class, $client->other());
    }

    public function testClientExposesBlacklist(): void
    {
        [$client] = $this->mockClient();
        $this->assertInstanceOf(BlacklistApi::class, $client->blacklist());
    }

    public function testClientExposesPaymentMethods(): void
    {
        [$client] = $this->mockClient();
        $this->assertInstanceOf(PaymentMethodsApi::class, $client->paymentMethods());
    }

    public function testClientExposesWebhooks(): void
    {
        [$client] = $this->mockClient();
        $this->assertInstanceOf(WebhooksApi::class, $client->webhooks());
    }

    public function testAccessTokenDefaultEmpty(): void
    {
        [$client, $mockHttp] = $this->mockClient();
        $mockHttp->method('getToken')->willReturn('');
        $this->assertSame('', $client->getAccessToken());
    }

    public function testSetAndGetAccessToken(): void
    {
        [$client, $mockHttp] = $this->mockClient();
        $mockHttp->expects($this->once())->method('setToken')->with('bearer-xyz');
        $mockHttp->method('getToken')->willReturn('bearer-xyz');
        $client->setAccessToken('bearer-xyz');
        $this->assertSame('bearer-xyz', $client->getAccessToken());
    }

    // =========================================================================
    // BookingsApi
    // =========================================================================

    public function testGetBookingsNoParams(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/bookings', [])
             ->willReturn($this->ok([]));

        $api  = new BookingsApi($http);
        $resp = $api->getBookings();
        $this->assertSame(200, $resp->status);
        $this->assertIsArray($resp->data);
    }

    public function testGetBookingsWithStatus(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/bookings', ['status' => 'OPEN'])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->getBookings(status: 'OPEN');
    }

    public function testGetBookingsWithPageNo(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/bookings', ['pageNo' => 2])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->getBookings(pageNo: 2);
    }

    public function testGetBookingsWithBothParams(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/bookings', ['pageNo' => 3, 'status' => 'COMPLETED'])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->getBookings(pageNo: 3, status: 'COMPLETED');
    }

    public function testCreateBooking(): void
    {
        $http = $this->mockHttp();
        $req  = ['date' => '2025-06-15', 'time' => '10:00', 'propertyId' => 1004,
                 'roomCount' => 2, 'bathroomCount' => 1, 'planId' => 2,
                 'hours' => 3, 'extraSupplies' => false, 'paymentMethodId' => 10];
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/create', $req)
             ->willReturn($this->ok(['id' => 1, 'status' => 'OPEN', 'date' => '2025-06-15',
                                     'time' => '10:00', 'hours' => 3.0, 'cost' => 150.0,
                                     'propertyId' => 1004, 'planId' => 2,
                                     'roomCount' => 2, 'bathroomCount' => 1,
                                     'extraSupplies' => false, 'paymentMethodId' => 10]));

        $resp = (new BookingsApi($http))->createBooking($req);
        $this->assertInstanceOf(Booking::class, $resp->data);
        $this->assertSame('OPEN', $resp->data->status);
        $this->assertSame('2025-06-15', $resp->data->date);
    }

    public function testGetBookingDetails(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/bookings/16926')
             ->willReturn($this->ok(['id' => 16926, 'status' => 'COMPLETED',
                                     'date' => '2025-06-15', 'time' => '10:00',
                                     'hours' => 3.0, 'cost' => 150.0, 'propertyId' => 1004,
                                     'cleanerId' => null, 'planId' => 2,
                                     'roomCount' => 2, 'bathroomCount' => 1,
                                     'extraSupplies' => false, 'paymentMethodId' => 10]));

        $resp = (new BookingsApi($http))->getBookingDetails(16926);
        $this->assertInstanceOf(Booking::class, $resp->data);
        $this->assertSame(16926, $resp->data->id);
        $this->assertNull($resp->data->cleanerId);
    }

    public function testCancelBookingWithReason(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/16459/cancel', ['reason' => 'Changed plans'])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->cancelBooking(16459, 'Changed plans');
    }

    public function testCancelBookingWithoutReason(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/16459/cancel', null)
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->cancelBooking(16459);
    }

    public function testRescheduleBooking(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/16459/reschedule', ['date' => '2025-07-01', 'time' => '14:00'])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->rescheduleBooking(16459, '2025-07-01', '14:00');
    }

    public function testAssignCleaner(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/16459/cleaner', ['cleanerId' => 5])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->assignCleaner(16459, 5);
    }

    public function testRemoveAssignedCleaner(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('delete')
             ->with('/v1/bookings/16459/cleaner')
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->removeAssignedCleaner(16459);
    }

    public function testAdjustHours(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/16459/hours', ['hours' => 4.0])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->adjustHours(16459, 4.0);
    }

    public function testPayExpenses(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/16926/expenses', ['paymentMethodId' => 10])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->payExpenses(16926, 10);
    }

    public function testGetBookingInspection(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/bookings/16926/inspection')
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->getBookingInspection(16926);
    }

    public function testGetBookingInspectionDetails(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/bookings/16926/inspection/details')
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->getBookingInspectionDetails(16926);
    }

    public function testAssignChecklistToBooking(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('put')
             ->with('/v1/bookings/16926/checklist/105')
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->assignChecklistToBooking(16926, 105);
    }

    public function testSubmitFeedbackWithComment(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/16926/feedback', ['rating' => 5, 'comment' => 'Excellent!'])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->submitFeedback(16926, 5, 'Excellent!');
    }

    public function testSubmitFeedbackWithoutComment(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/16926/feedback', ['rating' => 4])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->submitFeedback(16926, 4);
    }

    public function testAddTip(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/16926/tip', ['amount' => 20.0, 'paymentMethodId' => 10])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->addTip(16926, 20.0, 10);
    }

    public function testGetChat(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/bookings/17142/chat')
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->getChat(17142);
    }

    public function testSendMessage(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/bookings/17142/chat', ['message' => 'Please bring extra towels.'])
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->sendMessage(17142, 'Please bring extra towels.');
    }

    public function testDeleteMessage(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('delete')
             ->with('/v1/bookings/17142/chat/msg-abc-123')
             ->willReturn($this->ok([]));

        (new BookingsApi($http))->deleteMessage(17142, 'msg-abc-123');
    }

    // =========================================================================
    // UsersApi
    // =========================================================================

    public function testCreateUser(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/user/account', ['email' => 'jane@example.com', 'firstName' => 'Jane', 'lastName' => 'Smith'])
             ->willReturn($this->ok(['id' => 42, 'email' => 'jane@example.com', 'firstName' => 'Jane', 'lastName' => 'Smith']));

        $resp = (new UsersApi($http))->createUser('jane@example.com', 'Jane', 'Smith');
        $this->assertInstanceOf(User::class, $resp->data);
        $this->assertSame('jane@example.com', $resp->data->email);
        $this->assertSame(42, $resp->data->id);
    }

    public function testCreateUserWithPhone(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/user/account', ['email' => 'a@b.com', 'firstName' => 'A', 'lastName' => 'B', 'phone' => '+15551234567'])
             ->willReturn($this->ok(['id' => 1, 'email' => 'a@b.com', 'firstName' => 'A', 'lastName' => 'B']));

        (new UsersApi($http))->createUser('a@b.com', 'A', 'B', '+15551234567');
    }

    public function testCreateUserOmitsEmptyPhone(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/user/account', $this->callback(fn($b) => !array_key_exists('phone', $b)))
             ->willReturn($this->ok(['id' => 1, 'email' => 'a@b.com', 'firstName' => 'A', 'lastName' => 'B']));

        (new UsersApi($http))->createUser('a@b.com', 'A', 'B');
    }

    public function testFetchAccessToken(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/user/access-token/42')
             ->willReturn($this->ok(['id' => 42, 'email' => 'x@y.com', 'firstName' => 'X', 'lastName' => 'Y', 'token' => 'bearer-jwt']));

        $resp = (new UsersApi($http))->fetchAccessToken(42);
        $this->assertInstanceOf(User::class, $resp->data);
        $this->assertSame('bearer-jwt', $resp->data->token);
    }

    public function testVerifyJwt(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/user/verify-jwt', ['token' => 'eyJhbGci...'])
             ->willReturn($this->ok([]));

        (new UsersApi($http))->verifyJwt('eyJhbGci...');
    }

    // =========================================================================
    // PropertiesApi
    // =========================================================================

    public function testListPropertiesNoFilter(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/properties', [])
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->listProperties();
    }

    public function testListPropertiesWithServiceId(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/properties', ['serviceId' => 1])
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->listProperties(1);
    }

    public function testAddProperty(): void
    {
        $req  = ['name' => 'Beach House', 'address' => '123 St', 'city' => 'Miami',
                 'country' => 'USA', 'roomCount' => 3, 'bathroomCount' => 2, 'serviceId' => 1];
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/properties', $req)
             ->willReturn($this->ok(array_merge(['id' => 1040], $req)));

        $resp = (new PropertiesApi($http))->addProperty($req);
        $this->assertInstanceOf(Property::class, $resp->data);
        $this->assertSame(1040, $resp->data->id);
        $this->assertSame('Beach House', $resp->data->name);
    }

    public function testGetProperty(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/properties/1040')
             ->willReturn($this->ok(['id' => 1040, 'name' => 'Condo', 'address' => '456 Ave',
                                     'city' => 'NYC', 'country' => 'USA',
                                     'roomCount' => 2, 'bathroomCount' => 1, 'serviceId' => 1]));

        $resp = (new PropertiesApi($http))->getProperty(1040);
        $this->assertInstanceOf(Property::class, $resp->data);
        $this->assertSame('NYC', $resp->data->city);
    }

    public function testUpdateProperty(): void
    {
        $http = $this->mockHttp();
        $req  = ['name' => 'Updated', 'address' => '456 Ave', 'city' => 'NYC',
                 'country' => 'USA', 'roomCount' => 3, 'bathroomCount' => 1, 'serviceId' => 1];
        $http->expects($this->once())
             ->method('put')
             ->with('/v1/properties/1040', $req)
             ->willReturn($this->ok(array_merge(['id' => 1040], $req)));

        $resp = (new PropertiesApi($http))->updateProperty(1040, $req);
        $this->assertInstanceOf(Property::class, $resp->data);
    }

    public function testEnableProperty(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/properties/1040/enable-disable', ['enabled' => true])
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->enableOrDisableProperty(1040, true);
    }

    public function testDisableProperty(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/properties/1040/enable-disable', ['enabled' => false])
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->enableOrDisableProperty(1040, false);
    }

    public function testDeleteProperty(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('delete')
             ->with('/v1/properties/1040')
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->deleteProperty(1040);
    }

    public function testGetPropertyCleaners(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/properties/1040/cleaners')
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->getPropertyCleaners(1040);
    }

    public function testAssignCleanerToProperty(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/properties/1040/cleaners', ['cleanerId' => 5])
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->assignCleanerToProperty(1040, 5);
    }

    public function testUnassignCleanerFromProperty(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('delete')
             ->with('/v1/properties/1040/cleaners/5')
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->unassignCleanerFromProperty(1040, 5);
    }

    public function testAddICalLink(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('put')
             ->with('/v1/properties/1040/ical', ['icalLink' => 'https://cal.example.com/feed.ics'])
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->addICalLink(1040, 'https://cal.example.com/feed.ics');
    }

    public function testGetICalLink(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/properties/1040/ical')
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->getICalLink(1040);
    }

    public function testRemoveICalLink(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('delete')
             ->with('/v1/properties/1040/ical', ['icalLink' => 'https://cal.example.com/feed.ics'])
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->removeICalLink(1040, 'https://cal.example.com/feed.ics');
    }

    public function testAssignChecklistToPropertyTrue(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('put')
             ->with($this->stringContains('updateUpcomingBookings=true'))
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->assignChecklistToProperty(1040, 105, true);
    }

    public function testAssignChecklistToPropertyFalse(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('put')
             ->with($this->stringContains('updateUpcomingBookings=false'))
             ->willReturn($this->ok([]));

        (new PropertiesApi($http))->assignChecklistToProperty(1040, 105, false);
    }

    // =========================================================================
    // ChecklistsApi
    // =========================================================================

    public function testListChecklists(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/checklist')
             ->willReturn($this->ok([]));

        $resp = (new ChecklistsApi($http))->listChecklists();
        $this->assertSame(200, $resp->status);
    }

    public function testGetChecklist(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/checklist/105')
             ->willReturn($this->ok([
                 'id' => 105, 'name' => 'Standard',
                 'items' => [
                     ['id' => 1, 'description' => 'Vacuum floors', 'isCompleted' => false],
                     ['id' => 2, 'description' => 'Mop kitchen', 'isCompleted' => true],
                 ],
             ]));

        $resp = (new ChecklistsApi($http))->getChecklist(105);
        $this->assertInstanceOf(Checklist::class, $resp->data);
        $this->assertSame(105, $resp->data->id);
        $this->assertCount(2, $resp->data->items);
        $this->assertInstanceOf(ChecklistItem::class, $resp->data->items[0]);
        $this->assertSame('Vacuum floors', $resp->data->items[0]->description);
        $this->assertFalse($resp->data->items[0]->isCompleted);
        $this->assertTrue($resp->data->items[1]->isCompleted);
    }

    public function testCreateChecklist(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/checklist', ['name' => 'Deep Clean', 'items' => ['Mop floors', 'Wipe counters']])
             ->willReturn($this->ok(['id' => 200, 'name' => 'Deep Clean', 'items' => []]));

        $resp = (new ChecklistsApi($http))->createChecklist('Deep Clean', ['Mop floors', 'Wipe counters']);
        $this->assertInstanceOf(Checklist::class, $resp->data);
        $this->assertSame('Deep Clean', $resp->data->name);
    }

    public function testUpdateChecklist(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('put')
             ->with('/v1/checklist/105', ['name' => 'Updated', 'items' => ['New task']])
             ->willReturn($this->ok(['id' => 105, 'name' => 'Updated', 'items' => []]));

        (new ChecklistsApi($http))->updateChecklist(105, 'Updated', ['New task']);
    }

    public function testDeleteChecklist(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('delete')
             ->with('/v1/checklist/105')
             ->willReturn($this->ok([]));

        (new ChecklistsApi($http))->deleteChecklist(105);
    }

    // =========================================================================
    // OtherApi
    // =========================================================================

    public function testGetServices(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/services')
             ->willReturn($this->ok([]));

        (new OtherApi($http))->getServices();
    }

    public function testGetPlans(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/plans', ['propertyId' => 1004])
             ->willReturn($this->ok([]));

        (new OtherApi($http))->getPlans(1004);
    }

    public function testGetRecommendedHours(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/recommended-hours', ['propertyId' => 1004, 'bathroomCount' => 2, 'roomCount' => 3])
             ->willReturn($this->ok([]));

        (new OtherApi($http))->getRecommendedHours(1004, 2, 3);
    }

    public function testCalculateCost(): void
    {
        $http = $this->mockHttp();
        $req  = ['propertyId' => 1004, 'planId' => 2, 'hours' => 3, 'couponCode' => '20POFF'];
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/cost-estimate', $req)
             ->willReturn($this->ok([]));

        (new OtherApi($http))->calculateCost($req);
    }

    public function testGetCleaningExtras(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/cleaning-extras/1')
             ->willReturn($this->ok([]));

        (new OtherApi($http))->getCleaningExtras(1);
    }

    public function testGetAvailableCleaners(): void
    {
        $http = $this->mockHttp();
        $req  = ['propertyId' => 1004, 'date' => '2025-06-15', 'time' => '10:00'];
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/available-cleaners', $req)
             ->willReturn($this->ok([]));

        (new OtherApi($http))->getAvailableCleaners($req);
    }

    public function testGetCoupons(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/coupons')
             ->willReturn($this->ok([]));

        (new OtherApi($http))->getCoupons();
    }

    // =========================================================================
    // BlacklistApi
    // =========================================================================

    public function testListBlacklistedCleaners(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/blacklist/cleaner')
             ->willReturn($this->ok([]));

        (new BlacklistApi($http))->listBlacklistedCleaners();
    }

    public function testAddToBlacklist(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/blacklist/cleaner', ['cleanerId' => 7, 'reason' => 'Damaged furniture'])
             ->willReturn($this->ok([]));

        (new BlacklistApi($http))->addToBlacklist(7, 'Damaged furniture');
    }

    public function testAddToBlacklistWithoutReason(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/blacklist/cleaner', $this->callback(fn($b) => !array_key_exists('reason', $b)))
             ->willReturn($this->ok([]));

        (new BlacklistApi($http))->addToBlacklist(7);
    }

    public function testRemoveFromBlacklist(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('delete')
             ->with('/v1/blacklist/cleaner', ['cleanerId' => 7])
             ->willReturn($this->ok([]));

        (new BlacklistApi($http))->removeFromBlacklist(7);
    }

    // =========================================================================
    // PaymentMethodsApi
    // =========================================================================

    public function testGetSetupIntentDetails(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/payment-methods/setup-intent-details')
             ->willReturn($this->ok([]));

        (new PaymentMethodsApi($http))->getSetupIntentDetails();
    }

    public function testGetPaypalClientToken(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/payment-methods/paypal-client-token')
             ->willReturn($this->ok([]));

        (new PaymentMethodsApi($http))->getPaypalClientToken();
    }

    public function testAddPaymentMethod(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/payment-methods', ['paymentMethodId' => 'pm_xxx'])
             ->willReturn($this->ok([]));

        (new PaymentMethodsApi($http))->addPaymentMethod('pm_xxx');
    }

    public function testGetPaymentMethods(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/payment-methods')
             ->willReturn($this->ok([
                 ['id' => 193, 'type' => 'card', 'lastFour' => '4242', 'brand' => 'visa', 'isDefault' => true],
             ]));

        $resp = (new PaymentMethodsApi($http))->getPaymentMethods();
        $this->assertCount(1, $resp->data);
        $this->assertInstanceOf(PaymentMethod::class, $resp->data[0]);
        $this->assertSame(193, $resp->data[0]->id);
        $this->assertSame('4242', $resp->data[0]->lastFour);
        $this->assertTrue($resp->data[0]->isDefault);
    }

    public function testDeletePaymentMethod(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('delete')
             ->with('/v1/payment-methods/193')
             ->willReturn($this->ok([]));

        (new PaymentMethodsApi($http))->deletePaymentMethod(193);
    }

    public function testSetDefaultPaymentMethod(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('put')
             ->with('/v1/payment-methods/193/default')
             ->willReturn($this->ok([]));

        (new PaymentMethodsApi($http))->setDefaultPaymentMethod(193);
    }

    // =========================================================================
    // WebhooksApi
    // =========================================================================

    public function testListWebhooks(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('get')
             ->with('/v1/webhooks')
             ->willReturn($this->ok([]));

        (new WebhooksApi($http))->listWebhooks();
    }

    public function testCreateWebhook(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('post')
             ->with('/v1/webhooks', ['url' => 'https://app.example.com/hooks', 'event' => 'booking.status_changed'])
             ->willReturn($this->ok([]));

        (new WebhooksApi($http))->createWebhook('https://app.example.com/hooks', 'booking.status_changed');
    }

    public function testUpdateWebhook(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('put')
             ->with('/v1/webhooks/50', ['url' => 'https://app.example.com/v2/hooks', 'event' => 'booking.status_changed'])
             ->willReturn($this->ok([]));

        (new WebhooksApi($http))->updateWebhook(50, 'https://app.example.com/v2/hooks', 'booking.status_changed');
    }

    public function testDeleteWebhook(): void
    {
        $http = $this->mockHttp();
        $http->expects($this->once())
             ->method('delete')
             ->with('/v1/webhooks/50')
             ->willReturn($this->ok([]));

        (new WebhooksApi($http))->deleteWebhook(50);
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    public function testAuthExceptionAttributes(): void
    {
        $e = new AuthException(401, '{"message":"Unauthorized"}');
        $this->assertSame(401, $e->statusCode);
        $this->assertSame('{"message":"Unauthorized"}', $e->responseBody);
        $this->assertStringContainsString('401', $e->getMessage());
    }

    public function testApiExceptionAttributes(): void
    {
        $e = new ApiException(422, '{"message":"Validation failed"}', 'Validation error');
        $this->assertSame(422, $e->statusCode);
        $this->assertSame('{"message":"Validation failed"}', $e->responseBody);
        $this->assertSame('Validation error', $e->getMessage());
        $this->assertSame(422, $e->getCode());
    }

    public function testApiExceptionAutoMessage(): void
    {
        $e = new ApiException(500, 'body');
        $this->assertStringContainsString('500', $e->getMessage());
    }

    public function testCleansterExceptionMessage(): void
    {
        $e = new CleansterException('network timeout');
        $this->assertSame('network timeout', $e->getMessage());
    }

    public function testAuthExceptionIsCleansterException(): void
    {
        $e = new AuthException(401, 'body');
        $this->assertInstanceOf(CleansterException::class, $e);
    }

    public function testApiExceptionIsCleansterException(): void
    {
        $e = new ApiException(404, 'body');
        $this->assertInstanceOf(CleansterException::class, $e);
    }

    public function testHttpClientThrowsAuthExceptionOn401(): void
    {
        $http = $this->mockHttp();
        $http->method('get')
             ->willThrowException(new AuthException(401, '{"message":"Unauthorized"}'));

        $this->expectException(AuthException::class);
        (new BookingsApi($http))->getBookings();
    }

    public function testHttpClientThrowsApiExceptionOn404(): void
    {
        $http = $this->mockHttp();
        $http->method('get')
             ->willThrowException(new ApiException(404, '{"message":"Not found"}'));

        $this->expectException(ApiException::class);
        (new BookingsApi($http))->getBookingDetails(99999);
    }

    public function testHttpClientThrowsApiExceptionOn422(): void
    {
        $http = $this->mockHttp();
        $http->method('post')
             ->willThrowException(new ApiException(422, 'Validation error'));

        $this->expectException(ApiException::class);
        (new BookingsApi($http))->createBooking([]);
    }

    public function testHttpClientThrowsCleansterExceptionOnNetworkError(): void
    {
        $http = $this->mockHttp();
        $http->method('get')
             ->willThrowException(new CleansterException('Connection refused'));

        $this->expectException(CleansterException::class);
        (new BookingsApi($http))->getBookings();
    }

    // =========================================================================
    // Models
    // =========================================================================

    public function testBookingModelFieldMapping(): void
    {
        $b = new Booking([
            'id' => 1, 'status' => 'OPEN', 'date' => '2025-06-15', 'time' => '10:00',
            'hours' => 3.0, 'cost' => 150.0, 'propertyId' => 1004, 'cleanerId' => null,
            'planId' => 2, 'roomCount' => 2, 'bathroomCount' => 1,
            'extraSupplies' => false, 'paymentMethodId' => 10,
        ]);

        $this->assertSame(1, $b->id);
        $this->assertSame('OPEN', $b->status);
        $this->assertSame('2025-06-15', $b->date);
        $this->assertSame(150.0, $b->cost);
        $this->assertNull($b->cleanerId);
        $this->assertFalse($b->extraSupplies);
    }

    public function testBookingModelWithCleaner(): void
    {
        $b = new Booking(['id' => 1, 'cleanerId' => 7, 'status' => '', 'date' => '', 'time' => '',
                          'hours' => 0, 'cost' => 0, 'propertyId' => 0, 'planId' => 0,
                          'roomCount' => 0, 'bathroomCount' => 0, 'extraSupplies' => false,
                          'paymentMethodId' => 0]);
        $this->assertSame(7, $b->cleanerId);
    }

    public function testUserModelFieldMapping(): void
    {
        $u = new User(['id' => 42, 'email' => 'jane@example.com', 'firstName' => 'Jane',
                       'lastName' => 'Smith', 'phone' => null, 'token' => 'bearer-jwt']);
        $this->assertSame(42, $u->id);
        $this->assertSame('jane@example.com', $u->email);
        $this->assertNull($u->phone);
        $this->assertSame('bearer-jwt', $u->token);
    }

    public function testPropertyModelFieldMapping(): void
    {
        $p = new Property(['id' => 1040, 'name' => 'Condo', 'address' => '456 Ave',
                           'city' => 'NYC', 'country' => 'USA', 'roomCount' => 2,
                           'bathroomCount' => 1, 'serviceId' => 1, 'isEnabled' => true]);
        $this->assertSame(1040, $p->id);
        $this->assertSame('NYC', $p->city);
        $this->assertTrue($p->isEnabled);
    }

    public function testChecklistModelItems(): void
    {
        $cl = new Checklist([
            'id' => 105, 'name' => 'Standard',
            'items' => [
                ['id' => 1, 'description' => 'Vacuum', 'isCompleted' => false],
                ['id' => 2, 'description' => 'Mop',    'isCompleted' => true, 'imageUrl' => 'https://img.example.com/1.jpg'],
            ],
        ]);

        $this->assertSame(105, $cl->id);
        $this->assertCount(2, $cl->items);
        $this->assertFalse($cl->items[0]->isCompleted);
        $this->assertTrue($cl->items[1]->isCompleted);
        $this->assertSame('https://img.example.com/1.jpg', $cl->items[1]->imageUrl);
        $this->assertNull($cl->items[0]->imageUrl);
    }

    public function testPaymentMethodModel(): void
    {
        $pm = new PaymentMethod(['id' => 193, 'type' => 'card', 'lastFour' => '4242',
                                 'brand' => 'visa', 'isDefault' => true]);
        $this->assertSame(193, $pm->id);
        $this->assertSame('4242', $pm->lastFour);
        $this->assertSame('visa', $pm->brand);
        $this->assertTrue($pm->isDefault);
    }

    public function testApiResponseFields(): void
    {
        $http = $this->mockHttp();
        $http->method('get')
             ->willReturn(['status' => 200, 'message' => 'OK', 'data' => []]);

        $resp = (new BookingsApi($http))->getBookings();
        $this->assertSame(200, $resp->status);
        $this->assertSame('OK', $resp->message);
    }
}
