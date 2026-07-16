<?php

declare(strict_types=1);

namespace Cleanster\Tests;

use Cleanster\WebhookUtils;
use PHPUnit\Framework\TestCase;

class WebhookUtilsTest extends TestCase
{
    private const SECRET   = 'test-secret-key';
    private const PAYLOAD  = '{"event":"BOOKING_CREATED","bookingId":42}';
    private const EXPECTED = '50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364';

    public function testComputeSignatureReturnsCorrectHex(): void
    {
        $this->assertSame(self::EXPECTED, WebhookUtils::computeSignature(self::SECRET, self::PAYLOAD));
    }

    public function testVerifySignatureValidReturnsTrue(): void
    {
        $this->assertTrue(WebhookUtils::verifySignature(self::SECRET, self::PAYLOAD, self::EXPECTED));
    }

    public function testVerifySignatureWrongSignatureReturnsFalse(): void
    {
        $this->assertFalse(WebhookUtils::verifySignature(self::SECRET, self::PAYLOAD, 'badsignature'));
    }

    public function testVerifySignatureWrongSecretReturnsFalse(): void
    {
        $wrongSig = WebhookUtils::computeSignature('wrong-secret', self::PAYLOAD);
        $this->assertFalse(WebhookUtils::verifySignature(self::SECRET, self::PAYLOAD, $wrongSig));
    }
}
