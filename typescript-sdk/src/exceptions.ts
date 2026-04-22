/**
 * Exception hierarchy for the Cleanster TypeScript SDK.
 */

/**
 * Base error class for all Cleanster SDK errors.
 * Thrown for network failures, timeouts, and unexpected SDK-level issues.
 */
export class CleansterException extends Error {
  constructor(message: string) {
    super(message);
    this.name = "CleansterException";
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

/**
 * Thrown when the API returns HTTP 401 Unauthorized.
 * Indicates an invalid or missing access key or user token.
 */
export class CleansterAuthException extends CleansterException {
  public readonly statusCode: 401 = 401;
  public readonly responseBody: string;

  constructor(message: string, responseBody = "") {
    super(message);
    this.name = "CleansterAuthException";
    this.responseBody = responseBody;
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

/**
 * Thrown when the API returns a non-2xx HTTP response (other than 401).
 * Contains the HTTP status code and raw response body for debugging.
 */
export class CleansterApiException extends CleansterException {
  public readonly statusCode: number;
  public readonly responseBody: string;

  constructor(statusCode: number, message: string, responseBody = "") {
    super(message);
    this.name = "CleansterApiException";
    this.statusCode = statusCode;
    this.responseBody = responseBody;
    Object.setPrototypeOf(this, new.target.prototype);
  }
}
