using Xunit;

namespace Cleanster.Tests;

public class WebhookUtilsTests
{
    private const string Secret   = "test-secret-key";
    private const string Payload  = "{\"event\":\"BOOKING_CREATED\",\"bookingId\":42}";
    private const string Expected = "50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364";

    [Fact]
    public void ComputeSignature_ReturnsCorrectHex()
        => Assert.Equal(Expected, WebhookUtils.ComputeSignature(Secret, Payload));

    [Fact]
    public void VerifySignature_ValidSignatureReturnsTrue()
        => Assert.True(WebhookUtils.VerifySignature(Secret, Payload, Expected));

    [Fact]
    public void VerifySignature_WrongSignatureReturnsFalse()
        => Assert.False(WebhookUtils.VerifySignature(Secret, Payload, "badsignature"));

    [Fact]
    public void VerifySignature_WrongSecretReturnsFalse()
    {
        var wrongSig = WebhookUtils.ComputeSignature("wrong-secret", Payload);
        Assert.False(WebhookUtils.VerifySignature(Secret, Payload, wrongSig));
    }
}
