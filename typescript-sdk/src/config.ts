/**
 * Configuration for the Cleanster SDK client.
 */

export const SANDBOX_BASE_URL =
  "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public";

export const PRODUCTION_BASE_URL =
  "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public";

export interface CleansterConfigOptions {
  accessKey: string;
  baseUrl?: string;
  timeoutMs?: number;
}

/**
 * Holds all configuration for a CleansterClient instance.
 * Use the static factory methods or the builder for construction.
 */
export class CleansterConfig {
  public readonly accessKey: string;
  public readonly baseUrl: string;
  public readonly timeoutMs: number;

  constructor({ accessKey, baseUrl = SANDBOX_BASE_URL, timeoutMs = 30_000 }: CleansterConfigOptions) {
    if (!accessKey || !accessKey.trim()) {
      throw new Error("accessKey must not be empty or blank.");
    }
    this.accessKey = accessKey;
    this.baseUrl = baseUrl.replace(/\/$/, "");
    this.timeoutMs = timeoutMs;
  }

  /** Create a config pointing to the sandbox environment. */
  static sandbox(accessKey: string): CleansterConfig {
    return new CleansterConfig({ accessKey, baseUrl: SANDBOX_BASE_URL });
  }

  /** Create a config pointing to the production environment. */
  static production(accessKey: string): CleansterConfig {
    return new CleansterConfig({ accessKey, baseUrl: PRODUCTION_BASE_URL });
  }

  /** Return a builder for custom configuration. */
  static builder(accessKey: string): CleansterConfigBuilder {
    return new CleansterConfigBuilder(accessKey);
  }
}

/**
 * Fluent builder for CleansterConfig.
 *
 * @example
 * const config = CleansterConfig.builder("my-key")
 *   .production()
 *   .timeoutMs(60_000)
 *   .build();
 */
export class CleansterConfigBuilder {
  private _accessKey: string;
  private _baseUrl: string = SANDBOX_BASE_URL;
  private _timeoutMs: number = 30_000;

  constructor(accessKey: string) {
    this._accessKey = accessKey;
  }

  sandbox(): this {
    this._baseUrl = SANDBOX_BASE_URL;
    return this;
  }

  production(): this {
    this._baseUrl = PRODUCTION_BASE_URL;
    return this;
  }

  baseUrl(url: string): this {
    this._baseUrl = url;
    return this;
  }

  timeoutMs(ms: number): this {
    this._timeoutMs = ms;
    return this;
  }

  build(): CleansterConfig {
    return new CleansterConfig({
      accessKey: this._accessKey,
      baseUrl: this._baseUrl,
      timeoutMs: this._timeoutMs,
    });
  }
}
