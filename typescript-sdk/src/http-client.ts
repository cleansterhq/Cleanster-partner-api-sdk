/**
 * Internal HTTP transport layer.
 * Wraps the native fetch API, attaches auth headers, and maps HTTP errors to SDK exceptions.
 */

import { CleansterApiException, CleansterAuthException, CleansterException } from "./exceptions";
import { CleansterConfig } from "./config";
import { ApiResponse } from "./models/response";

export class HttpClient {
  private _bearerToken: string | null = null;

  constructor(private readonly config: CleansterConfig) {}

  get bearerToken(): string | null {
    return this._bearerToken;
  }

  set bearerToken(token: string | null) {
    this._bearerToken = token;
  }

  private buildHeaders(): Record<string, string> {
    return {
      "Content-Type": "application/json",
      "access-key": this.config.accessKey,
      "token": this._bearerToken ?? "",
    };
  }

  private buildUrl(path: string, params?: Record<string, string | number | boolean | undefined>): string {
    const url = new URL(this.config.baseUrl + path);
    if (params) {
      for (const [key, value] of Object.entries(params)) {
        if (value !== undefined && value !== null) {
          url.searchParams.set(key, String(value));
        }
      }
    }
    return url.toString();
  }

  private async handleResponse<T>(response: Response): Promise<ApiResponse<T>> {
    let body = "";
    try {
      body = await response.text();
    } catch {
      // ignore
    }

    if (response.status === 401) {
      throw new CleansterAuthException(
        "Unauthorized — invalid or missing access key or user token.",
        body,
      );
    }
    if (!response.ok) {
      throw new CleansterApiException(
        response.status,
        `API request failed with status ${response.status}`,
        body,
      );
    }
    try {
      return JSON.parse(body) as ApiResponse<T>;
    } catch (err) {
      throw new CleansterException(`Failed to parse JSON response: ${err}`);
    }
  }

  async get<T = unknown>(
    path: string,
    params?: Record<string, string | number | boolean | undefined>,
  ): Promise<ApiResponse<T>> {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.config.timeoutMs);
    try {
      const response = await fetch(this.buildUrl(path, params), {
        method: "GET",
        headers: this.buildHeaders(),
        signal: controller.signal,
      });
      return this.handleResponse<T>(response);
    } catch (err) {
      if (err instanceof CleansterException) throw err;
      throw new CleansterException(`Network error: ${err}`);
    } finally {
      clearTimeout(timeoutId);
    }
  }

  async post<T = unknown>(path: string, body?: unknown): Promise<ApiResponse<T>> {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.config.timeoutMs);
    try {
      const response = await fetch(this.config.baseUrl + path, {
        method: "POST",
        headers: this.buildHeaders(),
        body: body !== undefined ? JSON.stringify(body) : undefined,
        signal: controller.signal,
      });
      return this.handleResponse<T>(response);
    } catch (err) {
      if (err instanceof CleansterException) throw err;
      throw new CleansterException(`Network error: ${err}`);
    } finally {
      clearTimeout(timeoutId);
    }
  }

  async put<T = unknown>(path: string, body?: unknown): Promise<ApiResponse<T>> {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.config.timeoutMs);
    try {
      const response = await fetch(this.config.baseUrl + path, {
        method: "PUT",
        headers: this.buildHeaders(),
        body: body !== undefined ? JSON.stringify(body) : undefined,
        signal: controller.signal,
      });
      return this.handleResponse<T>(response);
    } catch (err) {
      if (err instanceof CleansterException) throw err;
      throw new CleansterException(`Network error: ${err}`);
    } finally {
      clearTimeout(timeoutId);
    }
  }

  async postMultipart<T = unknown>(path: string, imageData: Uint8Array | Buffer, fileName: string): Promise<ApiResponse<T>> {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.config.timeoutMs);
    try {
      const formData = new FormData();
      const blob = new Blob([imageData], { type: "image/*" });
      formData.append("image", blob, fileName);
      const headers: Record<string, string> = {
        "access-key": this.config.accessKey,
        "token": this._bearerToken ?? "",
      };
      const response = await fetch(this.config.baseUrl + path, {
        method: "POST",
        headers,
        body: formData,
        signal: controller.signal,
      });
      return this.handleResponse<T>(response);
    } catch (err) {
      if (err instanceof CleansterException) throw err;
      throw new CleansterException(`Network error: ${err}`);
    } finally {
      clearTimeout(timeoutId);
    }
  }

  async delete<T = unknown>(path: string, body?: unknown): Promise<ApiResponse<T>> {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.config.timeoutMs);
    try {
      const response = await fetch(this.config.baseUrl + path, {
        method: "DELETE",
        headers: this.buildHeaders(),
        body: body !== undefined ? JSON.stringify(body) : undefined,
        signal: controller.signal,
      });
      return this.handleResponse<T>(response);
    } catch (err) {
      if (err instanceof CleansterException) throw err;
      throw new CleansterException(`Network error: ${err}`);
    } finally {
      clearTimeout(timeoutId);
    }
  }
}
