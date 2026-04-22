using System.Text.Json;
using Cleanster;
using Cleanster.Api;
using Cleanster.Exceptions;
using Cleanster.Models;
using Moq;
using Xunit;

namespace Cleanster.Tests;

/// <summary>Full test suite for the Cleanster C# SDK.</summary>
public class CleansterTests
{
    // =========================================================================
    // Helpers
    // =========================================================================

    private static JsonElement MakeResponse(string dataJson = "[]")
    {
        var json = $$"""{"status":200,"message":"OK","data":{{dataJson}}}""";
        using var doc = JsonDocument.Parse(json);
        return doc.RootElement.Clone();
    }

    private static JsonElement BookingJson(int id = 1, string status = "OPEN") =>
        MakeResponse($$"""{"id":{{id}},"status":"{{status}}","date":"2025-06-15","time":"10:00","hours":3.0,"cost":150.0,"propertyId":1004,"cleanerId":null,"planId":2,"roomCount":2,"bathroomCount":1,"extraSupplies":false,"paymentMethodId":10}""");

    private static JsonElement UserJson(int id = 42, string? token = null)
    {
        var tok = token is not null ? $@",""token"":""{token}""" : "";
        return MakeResponse($$"""{"id":{{id}},"email":"jane@example.com","firstName":"Jane","lastName":"Smith"{{tok}}}""");
    }

    private static JsonElement PropertyJson(int id = 1040) =>
        MakeResponse($$"""{"id":{{id}},"name":"Beach House","address":"123 St","city":"Miami","country":"USA","roomCount":3,"bathroomCount":2,"serviceId":1}""");

    private static JsonElement ChecklistJson(int id = 105) =>
        MakeResponse($$"""{"id":{{id}},"name":"Standard","items":[{"id":1,"description":"Vacuum","isCompleted":false},{"id":2,"description":"Mop","isCompleted":true,"imageUrl":"https://img.example.com/1.jpg"}]}""");

    private static JsonElement PaymentMethodListJson() =>
        MakeResponse(@"[{""id"":193,""type"":""card"",""lastFour"":""4242"",""brand"":""visa"",""isDefault"":true}]");

    private static Mock<ICleansterHttpClient> MockHttp() => new(MockBehavior.Strict);

    // =========================================================================
    // CleansterConfig
    // =========================================================================

    [Fact]
    public void Config_Sandbox_SetsCorrectUrl()
    {
        var cfg = CleansterConfig.Sandbox("key");
        Assert.Equal(CleansterConfig.SandboxBaseUrl, cfg.BaseUrl);
    }

    [Fact]
    public void Config_Production_SetsCorrectUrl()
    {
        var cfg = CleansterConfig.Production("key");
        Assert.Equal(CleansterConfig.ProductionBaseUrl, cfg.BaseUrl);
    }

    [Fact]
    public void Config_StoresAccessKey()
    {
        var cfg = CleansterConfig.Sandbox("my-key");
        Assert.Equal("my-key", cfg.AccessKey);
    }

    [Fact]
    public void Config_DefaultTimeout()
    {
        var cfg = CleansterConfig.Sandbox("k");
        Assert.Equal(CleansterConfig.DefaultTimeout, cfg.Timeout);
    }

    [Fact]
    public void Config_CustomTimeout()
    {
        var cfg = new CleansterConfig("k", CleansterConfig.SandboxBaseUrl, TimeSpan.FromSeconds(60));
        Assert.Equal(TimeSpan.FromSeconds(60), cfg.Timeout);
    }

    [Fact]
    public void Config_BlankAccessKey_Throws()
    {
        Assert.Throws<ArgumentException>(() => new CleansterConfig("", CleansterConfig.SandboxBaseUrl));
    }

    [Fact]
    public void Config_WhitespaceAccessKey_Throws()
    {
        Assert.Throws<ArgumentException>(() => new CleansterConfig("   ", CleansterConfig.SandboxBaseUrl));
    }

    [Fact]
    public void Config_BlankBaseUrl_Throws()
    {
        Assert.Throws<ArgumentException>(() => new CleansterConfig("key", ""));
    }

    // =========================================================================
    // CleansterClient
    // =========================================================================

    [Fact]
    public void Client_Sandbox_CreatesClient()
    {
        var client = CleansterClient.Sandbox("key");
        Assert.NotNull(client);
        client.Dispose();
    }

    [Fact]
    public void Client_Production_CreatesClient()
    {
        var client = CleansterClient.Production("key");
        Assert.NotNull(client);
        client.Dispose();
    }

    [Fact]
    public void Client_ExposesBookings()
    {
        var http = new Mock<ICleansterHttpClient>();
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        Assert.IsType<BookingsApi>(client.Bookings);
    }

    [Fact]
    public void Client_ExposesUsers()
    {
        var http = new Mock<ICleansterHttpClient>();
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        Assert.IsType<UsersApi>(client.Users);
    }

    [Fact]
    public void Client_ExposesProperties()
    {
        var http = new Mock<ICleansterHttpClient>();
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        Assert.IsType<PropertiesApi>(client.Properties);
    }

    [Fact]
    public void Client_ExposesChecklists()
    {
        var http = new Mock<ICleansterHttpClient>();
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        Assert.IsType<ChecklistsApi>(client.Checklists);
    }

    [Fact]
    public void Client_ExposesOther()
    {
        var http = new Mock<ICleansterHttpClient>();
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        Assert.IsType<OtherApi>(client.Other);
    }

    [Fact]
    public void Client_ExposesBlacklist()
    {
        var http = new Mock<ICleansterHttpClient>();
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        Assert.IsType<BlacklistApi>(client.Blacklist);
    }

    [Fact]
    public void Client_ExposesPaymentMethods()
    {
        var http = new Mock<ICleansterHttpClient>();
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        Assert.IsType<PaymentMethodsApi>(client.PaymentMethods);
    }

    [Fact]
    public void Client_ExposesWebhooks()
    {
        var http = new Mock<ICleansterHttpClient>();
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        Assert.IsType<WebhooksApi>(client.Webhooks);
    }

    [Fact]
    public void Client_SetAccessToken_CallsSetToken()
    {
        var http = new Mock<ICleansterHttpClient>();
        http.Setup(h => h.SetToken("bearer-xyz"));
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        client.SetAccessToken("bearer-xyz");
        http.Verify(h => h.SetToken("bearer-xyz"), Times.Once);
    }

    [Fact]
    public void Client_GetAccessToken_ReturnsCurrentToken()
    {
        var http = new Mock<ICleansterHttpClient>();
        http.Setup(h => h.GetToken()).Returns("my-token");
        using var client = new CleansterClient(CleansterConfig.Sandbox("k"), http.Object);
        Assert.Equal("my-token", client.GetAccessToken());
    }

    // =========================================================================
    // BookingsApi
    // =========================================================================

    [Fact]
    public async Task Bookings_GetBookings_NoParams()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/bookings", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        var resp = await new BookingsApi(http.Object).GetBookingsAsync();
        Assert.Equal(200, resp.Status);
        Assert.Empty(resp.Data);
    }

    [Fact]
    public async Task Bookings_GetBookings_WithStatus()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/bookings",
            It.Is<IDictionary<string, string>?>(q => q != null && q["status"] == "OPEN"),
            It.IsAny<CancellationToken>())).ReturnsAsync(MakeResponse("[]"));
        await new BookingsApi(http.Object).GetBookingsAsync(status: "OPEN");
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_GetBookings_WithPageNo()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/bookings",
            It.Is<IDictionary<string, string>?>(q => q != null && q["pageNo"] == "2"),
            It.IsAny<CancellationToken>())).ReturnsAsync(MakeResponse("[]"));
        await new BookingsApi(http.Object).GetBookingsAsync(pageNo: 2);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_CreateBooking_PostsToCorrectPath()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/create", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(BookingJson());
        var resp = await new BookingsApi(http.Object).CreateBookingAsync(
            "2025-06-15", "10:00", 1004, 2, 1, 2, 3.0, false, 10);
        Assert.IsType<Booking>(resp.Data);
        Assert.Equal("OPEN", resp.Data.Status);
        Assert.Equal("2025-06-15", resp.Data.Date);
    }

    [Fact]
    public async Task Bookings_GetBookingDetails_ParsesModel()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/bookings/16926", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(BookingJson(16926, "COMPLETED"));
        var resp = await new BookingsApi(http.Object).GetBookingDetailsAsync(16926);
        Assert.Equal(16926, resp.Data.Id);
        Assert.Equal("COMPLETED", resp.Data.Status);
        Assert.Null(resp.Data.CleanerId);
    }

    [Fact]
    public async Task Bookings_CancelBooking_WithReason()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/16459/cancel",
            It.Is<object?>(b => b != null), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).CancelBookingAsync(16459, "Changed plans");
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_CancelBooking_WithoutReason_PassesNullBody()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/16459/cancel", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).CancelBookingAsync(16459);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_RescheduleBooking()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/16459/reschedule",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).RescheduleBookingAsync(16459, "2025-07-01", "14:00");
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_AssignCleaner()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/16459/cleaner",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).AssignCleanerAsync(16459, 5);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_RemoveAssignedCleaner()
    {
        var http = MockHttp();
        http.Setup(h => h.DeleteAsync("/v1/bookings/16459/cleaner", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).RemoveAssignedCleanerAsync(16459);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_AdjustHours()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/16459/hours",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).AdjustHoursAsync(16459, 4.0);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_PayExpenses()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/16926/expenses",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).PayExpensesAsync(16926, 10);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_GetBookingInspection()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/bookings/16926/inspection", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).GetBookingInspectionAsync(16926);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_GetBookingInspectionDetails()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/bookings/16926/inspection/details", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).GetBookingInspectionDetailsAsync(16926);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_AssignChecklistToBooking()
    {
        var http = MockHttp();
        http.Setup(h => h.PutAsync("/v1/bookings/16926/checklist/105", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).AssignChecklistToBookingAsync(16926, 105);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_SubmitFeedback_WithComment()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/16926/feedback",
            It.Is<object?>(b => b != null), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).SubmitFeedbackAsync(16926, 5, "Excellent!");
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_SubmitFeedback_WithoutComment_BodyHasNoComment()
    {
        var http = MockHttp();
        object? capturedBody = null;
        http.Setup(h => h.PostAsync("/v1/bookings/16926/feedback",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .Callback<string, object?, CancellationToken>((_, body, _) => capturedBody = body)
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).SubmitFeedbackAsync(16926, 4);
        var json = JsonSerializer.Serialize(capturedBody);
        Assert.DoesNotContain("comment", json);
    }

    [Fact]
    public async Task Bookings_AddTip()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/16926/tip",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).AddTipAsync(16926, 20.0, 10);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_GetChat()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/bookings/17142/chat", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        await new BookingsApi(http.Object).GetChatAsync(17142);
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_SendMessage()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/bookings/17142/chat",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).SendMessageAsync(17142, "Focus on the kitchen.");
        http.VerifyAll();
    }

    [Fact]
    public async Task Bookings_DeleteMessage()
    {
        var http = MockHttp();
        http.Setup(h => h.DeleteAsync("/v1/bookings/17142/chat/msg-abc-123",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BookingsApi(http.Object).DeleteMessageAsync(17142, "msg-abc-123");
        http.VerifyAll();
    }

    // =========================================================================
    // UsersApi
    // =========================================================================

    [Fact]
    public async Task Users_CreateUser_ParsesModel()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/user/account", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(UserJson(42));
        var resp = await new UsersApi(http.Object).CreateUserAsync("jane@example.com", "Jane", "Smith");
        Assert.Equal(42, resp.Data.Id);
        Assert.Equal("jane@example.com", resp.Data.Email);
    }

    [Fact]
    public async Task Users_CreateUser_WithPhone_IncludesPhone()
    {
        var http = MockHttp();
        object? capturedBody = null;
        http.Setup(h => h.PostAsync("/v1/user/account", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .Callback<string, object?, CancellationToken>((_, b, _) => capturedBody = b)
            .ReturnsAsync(UserJson());
        await new UsersApi(http.Object).CreateUserAsync("a@b.com", "A", "B", "+15551234567");
        var json = JsonSerializer.Serialize(capturedBody);
        Assert.Contains("phone", json);
    }

    [Fact]
    public async Task Users_CreateUser_WithoutPhone_OmitsPhone()
    {
        var http = MockHttp();
        object? capturedBody = null;
        http.Setup(h => h.PostAsync("/v1/user/account", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .Callback<string, object?, CancellationToken>((_, b, _) => capturedBody = b)
            .ReturnsAsync(UserJson());
        await new UsersApi(http.Object).CreateUserAsync("a@b.com", "A", "B");
        var json = JsonSerializer.Serialize(capturedBody);
        Assert.DoesNotContain("phone", json);
    }

    [Fact]
    public async Task Users_FetchAccessToken_ReturnsToken()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/user/access-token/42", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(UserJson(42, "bearer-jwt"));
        var resp = await new UsersApi(http.Object).FetchAccessTokenAsync(42);
        Assert.Equal("bearer-jwt", resp.Data.Token);
    }

    [Fact]
    public async Task Users_VerifyJwt()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/user/verify-jwt", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new UsersApi(http.Object).VerifyJwtAsync("eyJhbGci...");
        http.VerifyAll();
    }

    // =========================================================================
    // PropertiesApi
    // =========================================================================

    [Fact]
    public async Task Properties_ListProperties_NoFilter()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/properties", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        var resp = await new PropertiesApi(http.Object).ListPropertiesAsync();
        Assert.Empty(resp.Data);
    }

    [Fact]
    public async Task Properties_ListProperties_WithServiceId()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/properties",
            It.Is<IDictionary<string, string>?>(q => q != null && q["serviceId"] == "1"),
            It.IsAny<CancellationToken>())).ReturnsAsync(MakeResponse("[]"));
        await new PropertiesApi(http.Object).ListPropertiesAsync(1);
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_AddProperty_ParsesModel()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/properties", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(PropertyJson(1040));
        var resp = await new PropertiesApi(http.Object)
            .AddPropertyAsync("Beach House", "123 St", "Miami", "USA", 3, 2, 1);
        Assert.Equal(1040, resp.Data.Id);
        Assert.Equal("Miami", resp.Data.City);
    }

    [Fact]
    public async Task Properties_GetProperty()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/properties/1040", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(PropertyJson(1040));
        var resp = await new PropertiesApi(http.Object).GetPropertyAsync(1040);
        Assert.IsType<Property>(resp.Data);
    }

    [Fact]
    public async Task Properties_UpdateProperty()
    {
        var http = MockHttp();
        http.Setup(h => h.PutAsync("/v1/properties/1040", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(PropertyJson(1040));
        var resp = await new PropertiesApi(http.Object)
            .UpdatePropertyAsync(1040, "Updated", "123 St", "Miami", "USA", 3, 2, 1);
        Assert.IsType<Property>(resp.Data);
    }

    [Fact]
    public async Task Properties_EnableProperty()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/properties/1040/enable-disable",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).EnableOrDisablePropertyAsync(1040, true);
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_DisableProperty()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/properties/1040/enable-disable",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).EnableOrDisablePropertyAsync(1040, false);
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_DeleteProperty()
    {
        var http = MockHttp();
        http.Setup(h => h.DeleteAsync("/v1/properties/1040", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).DeletePropertyAsync(1040);
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_GetPropertyCleaners()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/properties/1040/cleaners", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        await new PropertiesApi(http.Object).GetPropertyCleanersAsync(1040);
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_AssignCleanerToProperty()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/properties/1040/cleaners",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).AssignCleanerToPropertyAsync(1040, 5);
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_UnassignCleanerFromProperty()
    {
        var http = MockHttp();
        http.Setup(h => h.DeleteAsync("/v1/properties/1040/cleaners/5",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).UnassignCleanerFromPropertyAsync(1040, 5);
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_AddICalLink()
    {
        var http = MockHttp();
        http.Setup(h => h.PutAsync("/v1/properties/1040/ical",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).AddICalLinkAsync(1040, "https://cal.example.com/feed.ics");
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_GetICalLink()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/properties/1040/ical", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).GetICalLinkAsync(1040);
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_RemoveICalLink()
    {
        var http = MockHttp();
        http.Setup(h => h.DeleteAsync("/v1/properties/1040/ical",
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).RemoveICalLinkAsync(1040, "https://cal.example.com/feed.ics");
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_AssignChecklistToProperty_True()
    {
        var http = MockHttp();
        http.Setup(h => h.PutAsync(It.Is<string>(p => p.Contains("updateUpcomingBookings=true")),
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).AssignChecklistToPropertyAsync(1040, 105, true);
        http.VerifyAll();
    }

    [Fact]
    public async Task Properties_AssignChecklistToProperty_False()
    {
        var http = MockHttp();
        http.Setup(h => h.PutAsync(It.Is<string>(p => p.Contains("updateUpcomingBookings=false")),
            It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PropertiesApi(http.Object).AssignChecklistToPropertyAsync(1040, 105, false);
        http.VerifyAll();
    }

    // =========================================================================
    // ChecklistsApi
    // =========================================================================

    [Fact]
    public async Task Checklists_ListChecklists()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/checklist", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        var resp = await new ChecklistsApi(http.Object).ListChecklistsAsync();
        Assert.Equal(200, resp.Status);
        Assert.Empty(resp.Data);
    }

    [Fact]
    public async Task Checklists_GetChecklist_ParsesTypedItems()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/checklist/105", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(ChecklistJson(105));
        var resp = await new ChecklistsApi(http.Object).GetChecklistAsync(105);
        Assert.Equal(105, resp.Data.Id);
        Assert.Equal("Standard", resp.Data.Name);
        Assert.Equal(2, resp.Data.Items.Count);
        Assert.IsType<ChecklistItem>(resp.Data.Items[0]);
        Assert.Equal("Vacuum", resp.Data.Items[0].Description);
        Assert.False(resp.Data.Items[0].IsCompleted);
        Assert.True(resp.Data.Items[1].IsCompleted);
        Assert.Equal("https://img.example.com/1.jpg", resp.Data.Items[1].ImageUrl);
        Assert.Null(resp.Data.Items[0].ImageUrl);
    }

    [Fact]
    public async Task Checklists_CreateChecklist()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/checklist", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(ChecklistJson(200));
        var resp = await new ChecklistsApi(http.Object)
            .CreateChecklistAsync("Deep Clean", ["Vacuum", "Mop"]);
        Assert.IsType<Checklist>(resp.Data);
    }

    [Fact]
    public async Task Checklists_UpdateChecklist()
    {
        var http = MockHttp();
        http.Setup(h => h.PutAsync("/v1/checklist/105", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(ChecklistJson(105));
        await new ChecklistsApi(http.Object).UpdateChecklistAsync(105, "Updated", ["Task 1"]);
        http.VerifyAll();
    }

    [Fact]
    public async Task Checklists_DeleteChecklist()
    {
        var http = MockHttp();
        http.Setup(h => h.DeleteAsync("/v1/checklist/105", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new ChecklistsApi(http.Object).DeleteChecklistAsync(105);
        http.VerifyAll();
    }

    // =========================================================================
    // OtherApi
    // =========================================================================

    [Fact]
    public async Task Other_GetServices()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/services", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        await new OtherApi(http.Object).GetServicesAsync();
        http.VerifyAll();
    }

    [Fact]
    public async Task Other_GetPlans()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/plans",
            It.Is<IDictionary<string, string>?>(q => q != null && q["propertyId"] == "1004"),
            It.IsAny<CancellationToken>())).ReturnsAsync(MakeResponse("[]"));
        await new OtherApi(http.Object).GetPlansAsync(1004);
        http.VerifyAll();
    }

    [Fact]
    public async Task Other_GetRecommendedHours()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/recommended-hours",
            It.Is<IDictionary<string, string>?>(q =>
                q != null && q["propertyId"] == "1004" && q["bathroomCount"] == "2" && q["roomCount"] == "3"),
            It.IsAny<CancellationToken>())).ReturnsAsync(MakeResponse("{}"));
        await new OtherApi(http.Object).GetRecommendedHoursAsync(1004, 2, 3);
        http.VerifyAll();
    }

    [Fact]
    public async Task Other_CalculateCost()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/cost-estimate", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new OtherApi(http.Object).CalculateCostAsync(1004, 2, 3.0, "20POFF");
        http.VerifyAll();
    }

    [Fact]
    public async Task Other_GetCleaningExtras()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/cleaning-extras/1", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        await new OtherApi(http.Object).GetCleaningExtrasAsync(1);
        http.VerifyAll();
    }

    [Fact]
    public async Task Other_GetAvailableCleaners()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/available-cleaners", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        await new OtherApi(http.Object).GetAvailableCleanersAsync(1004, "2025-06-15", "10:00");
        http.VerifyAll();
    }

    [Fact]
    public async Task Other_GetCoupons()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/coupons", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        await new OtherApi(http.Object).GetCouponsAsync();
        http.VerifyAll();
    }

    // =========================================================================
    // BlacklistApi
    // =========================================================================

    [Fact]
    public async Task Blacklist_ListBlacklistedCleaners()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/blacklist/cleaner", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        await new BlacklistApi(http.Object).ListBlacklistedCleanersAsync();
        http.VerifyAll();
    }

    [Fact]
    public async Task Blacklist_AddToBlacklist_WithReason()
    {
        var http = MockHttp();
        object? capturedBody = null;
        http.Setup(h => h.PostAsync("/v1/blacklist/cleaner", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .Callback<string, object?, CancellationToken>((_, b, _) => capturedBody = b)
            .ReturnsAsync(MakeResponse("{}"));
        await new BlacklistApi(http.Object).AddToBlacklistAsync(7, "Damaged furniture");
        var json = JsonSerializer.Serialize(capturedBody);
        Assert.Contains("reason", json);
    }

    [Fact]
    public async Task Blacklist_AddToBlacklist_WithoutReason()
    {
        var http = MockHttp();
        object? capturedBody = null;
        http.Setup(h => h.PostAsync("/v1/blacklist/cleaner", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .Callback<string, object?, CancellationToken>((_, b, _) => capturedBody = b)
            .ReturnsAsync(MakeResponse("{}"));
        await new BlacklistApi(http.Object).AddToBlacklistAsync(7);
        var json = JsonSerializer.Serialize(capturedBody);
        Assert.DoesNotContain("reason", json);
    }

    [Fact]
    public async Task Blacklist_RemoveFromBlacklist()
    {
        var http = MockHttp();
        http.Setup(h => h.DeleteAsync("/v1/blacklist/cleaner", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new BlacklistApi(http.Object).RemoveFromBlacklistAsync(7);
        http.VerifyAll();
    }

    // =========================================================================
    // PaymentMethodsApi
    // =========================================================================

    [Fact]
    public async Task PaymentMethods_GetSetupIntentDetails()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/payment-methods/setup-intent-details", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PaymentMethodsApi(http.Object).GetSetupIntentDetailsAsync();
        http.VerifyAll();
    }

    [Fact]
    public async Task PaymentMethods_GetPaypalClientToken()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/payment-methods/paypal-client-token", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PaymentMethodsApi(http.Object).GetPaypalClientTokenAsync();
        http.VerifyAll();
    }

    [Fact]
    public async Task PaymentMethods_AddPaymentMethod()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/payment-methods", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PaymentMethodsApi(http.Object).AddPaymentMethodAsync("pm_xxx");
        http.VerifyAll();
    }

    [Fact]
    public async Task PaymentMethods_GetPaymentMethods_ParsesModels()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/payment-methods", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(PaymentMethodListJson());
        var resp = await new PaymentMethodsApi(http.Object).GetPaymentMethodsAsync();
        Assert.Single(resp.Data);
        Assert.IsType<PaymentMethod>(resp.Data[0]);
        Assert.Equal(193, resp.Data[0].Id);
        Assert.Equal("4242", resp.Data[0].LastFour);
        Assert.Equal("visa", resp.Data[0].Brand);
        Assert.True(resp.Data[0].IsDefault);
    }

    [Fact]
    public async Task PaymentMethods_DeletePaymentMethod()
    {
        var http = MockHttp();
        http.Setup(h => h.DeleteAsync("/v1/payment-methods/193", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PaymentMethodsApi(http.Object).DeletePaymentMethodAsync(193);
        http.VerifyAll();
    }

    [Fact]
    public async Task PaymentMethods_SetDefaultPaymentMethod()
    {
        var http = MockHttp();
        http.Setup(h => h.PutAsync("/v1/payment-methods/193/default", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new PaymentMethodsApi(http.Object).SetDefaultPaymentMethodAsync(193);
        http.VerifyAll();
    }

    // =========================================================================
    // WebhooksApi
    // =========================================================================

    [Fact]
    public async Task Webhooks_ListWebhooks()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync("/v1/webhooks", null, It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("[]"));
        await new WebhooksApi(http.Object).ListWebhooksAsync();
        http.VerifyAll();
    }

    [Fact]
    public async Task Webhooks_CreateWebhook()
    {
        var http = MockHttp();
        http.Setup(h => h.PostAsync("/v1/webhooks", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new WebhooksApi(http.Object).CreateWebhookAsync("https://app.example.com/hooks", "booking.status_changed");
        http.VerifyAll();
    }

    [Fact]
    public async Task Webhooks_UpdateWebhook()
    {
        var http = MockHttp();
        http.Setup(h => h.PutAsync("/v1/webhooks/50", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new WebhooksApi(http.Object).UpdateWebhookAsync(50, "https://app.example.com/v2", "booking.status_changed");
        http.VerifyAll();
    }

    [Fact]
    public async Task Webhooks_DeleteWebhook()
    {
        var http = MockHttp();
        http.Setup(h => h.DeleteAsync("/v1/webhooks/50", It.IsAny<object?>(), It.IsAny<CancellationToken>()))
            .ReturnsAsync(MakeResponse("{}"));
        await new WebhooksApi(http.Object).DeleteWebhookAsync(50);
        http.VerifyAll();
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    [Fact]
    public void AuthException_HasCorrectStatusCode()
    {
        var ex = new AuthException(401, @"{""message"":""Unauthorized""}");
        Assert.Equal(401, ex.StatusCode);
        Assert.Contains("401", ex.Message);
    }

    [Fact]
    public void AuthException_HasResponseBody()
    {
        var ex = new AuthException(401, "body");
        Assert.Equal("body", ex.ResponseBody);
    }

    [Fact]
    public void ApiException_HasCorrectStatusCode()
    {
        var ex = new ApiException(422, "body");
        Assert.Equal(422, ex.StatusCode);
        Assert.Contains("422", ex.Message);
    }

    [Fact]
    public void ApiException_CustomMessage()
    {
        var ex = new ApiException(404, "body", "Custom message");
        Assert.Equal("Custom message", ex.Message);
    }

    [Fact]
    public void ApiException_HasResponseBody()
    {
        var ex = new ApiException(500, "raw-body");
        Assert.Equal("raw-body", ex.ResponseBody);
    }

    [Fact]
    public void CleansterException_Message()
    {
        var ex = new CleansterException("network error");
        Assert.Equal("network error", ex.Message);
    }

    [Fact]
    public void AuthException_IsCleansterException()
    {
        Assert.IsAssignableFrom<CleansterException>(new AuthException());
    }

    [Fact]
    public void ApiException_IsCleansterException()
    {
        Assert.IsAssignableFrom<CleansterException>(new ApiException(500));
    }

    [Fact]
    public async Task HttpClient_Throws_AuthException_On401()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync(It.IsAny<string>(), It.IsAny<IDictionary<string, string>?>(), It.IsAny<CancellationToken>()))
            .ThrowsAsync(new AuthException(401, "Unauthorized"));
        await Assert.ThrowsAsync<AuthException>(() =>
            new BookingsApi(http.Object).GetBookingsAsync());
    }

    [Fact]
    public async Task HttpClient_Throws_ApiException_On404()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync(It.IsAny<string>(), It.IsAny<IDictionary<string, string>?>(), It.IsAny<CancellationToken>()))
            .ThrowsAsync(new ApiException(404, "Not found"));
        await Assert.ThrowsAsync<ApiException>(() =>
            new BookingsApi(http.Object).GetBookingDetailsAsync(99999));
    }

    [Fact]
    public async Task HttpClient_Throws_CleansterException_OnNetworkError()
    {
        var http = MockHttp();
        http.Setup(h => h.GetAsync(It.IsAny<string>(), It.IsAny<IDictionary<string, string>?>(), It.IsAny<CancellationToken>()))
            .ThrowsAsync(new CleansterException("Connection refused"));
        await Assert.ThrowsAsync<CleansterException>(() =>
            new BookingsApi(http.Object).GetBookingsAsync());
    }

    // =========================================================================
    // Models
    // =========================================================================

    [Fact]
    public void Booking_NullableCleanerId()
    {
        var b = new Booking { Id = 1, CleanerId = null };
        Assert.Null(b.CleanerId);
    }

    [Fact]
    public void Booking_WithCleaner()
    {
        var b = new Booking { Id = 1, CleanerId = 7 };
        Assert.Equal(7, b.CleanerId);
    }

    [Fact]
    public void User_NullableToken()
    {
        var u = new User { Id = 1, Email = "x@y.com", Token = null };
        Assert.Null(u.Token);
    }

    [Fact]
    public void User_WithToken()
    {
        var u = new User { Id = 1, Email = "x@y.com", Token = "jwt" };
        Assert.Equal("jwt", u.Token);
    }

    [Fact]
    public void Property_NullableIsEnabled()
    {
        var p = new Property { Id = 1, IsEnabled = null };
        Assert.Null(p.IsEnabled);
    }

    [Fact]
    public void Checklist_TypedItems()
    {
        var cl = new Checklist
        {
            Id    = 1,
            Name  = "Test",
            Items = [
                new ChecklistItem { Id = 1, Description = "Task 1", IsCompleted = false },
                new ChecklistItem { Id = 2, Description = "Task 2", IsCompleted = true, ImageUrl = "https://example.com/img.jpg" },
            ],
        };
        Assert.Equal(2, cl.Items.Count);
        Assert.Null(cl.Items[0].ImageUrl);
        Assert.Equal("https://example.com/img.jpg", cl.Items[1].ImageUrl);
    }

    [Fact]
    public void PaymentMethod_NullableFields()
    {
        var pm = new PaymentMethod { Id = 1, Type = "paypal", LastFour = null, Brand = null };
        Assert.Null(pm.LastFour);
        Assert.Null(pm.Brand);
    }

    [Fact]
    public void ApiResponse_Record_AllFields()
    {
        var resp = new ApiResponse<string>(200, "OK", "hello");
        Assert.Equal(200,     resp.Status);
        Assert.Equal("OK",    resp.Message);
        Assert.Equal("hello", resp.Data);
    }
}
