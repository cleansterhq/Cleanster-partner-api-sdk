<?php

declare(strict_types=1);

namespace Cleanster\Exceptions;

/**
 * Base exception for all Cleanster SDK errors.
 *
 * Thrown for network failures, cURL errors, timeouts, and JSON parse errors.
 * Catch this class to handle any Cleanster-related error in one place.
 */
class CleansterException extends \RuntimeException {}
