<?php

declare(strict_types=1);

namespace Cleanster;

/**
 * Configuration for the CleansterClient.
 */
final class Config
{
    /** Base URL for the sandbox environment (development/testing — no real charges). */
    public const SANDBOX_BASE_URL = 'https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public';

    /** Base URL for the production environment (live traffic — real cleaners and charges). */
    public const PRODUCTION_BASE_URL = 'https://partner-dot-official-tidyio-project.ue.r.appspot.com/public';

    /** Default cURL timeout in seconds. */
    public const DEFAULT_TIMEOUT = 30;

    /**
     * @param string $accessKey  Your partner access key (sent as the "access-key" header).
     * @param string $baseUrl    API base URL — use SANDBOX_BASE_URL or PRODUCTION_BASE_URL.
     * @param int    $timeout    HTTP request timeout in seconds (default: 30).
     *
     * @throws \InvalidArgumentException if accessKey or baseUrl is blank.
     */
    public function __construct(
        public readonly string $accessKey,
        public readonly string $baseUrl,
        public readonly int    $timeout = self::DEFAULT_TIMEOUT,
    ) {
        if (trim($accessKey) === '') {
            throw new \InvalidArgumentException('Cleanster: accessKey must not be empty.');
        }
        if ($baseUrl === '') {
            throw new \InvalidArgumentException('Cleanster: baseUrl must not be empty.');
        }
    }

    /** Returns a Config pre-configured for the sandbox environment. */
    public static function sandbox(string $accessKey): self
    {
        return new self($accessKey, self::SANDBOX_BASE_URL);
    }

    /** Returns a Config pre-configured for the production environment. */
    public static function production(string $accessKey): self
    {
        return new self($accessKey, self::PRODUCTION_BASE_URL);
    }
}
