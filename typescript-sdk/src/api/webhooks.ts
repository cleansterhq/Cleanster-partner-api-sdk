/**
 * WebhooksApi — manage real-time event notification endpoints.
 */

import { HttpClient } from "../http-client";
import { ApiResponse } from "../models/response";

export interface WebhookRequest {
  url: string;
  event?: string;
  [key: string]: unknown;
}

export class WebhooksApi {
  constructor(private readonly http: HttpClient) {}

  /**
   * Return all configured webhook endpoints.
   */
  listWebhooks(): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/webhooks");
  }

  /**
   * Register a new webhook endpoint.
   * @param request  url (HTTPS endpoint), event type, and any extra fields.
   */
  createWebhook(request: WebhookRequest): Promise<ApiResponse<unknown>> {
    return this.http.post("/v1/webhooks", request);
  }

  /**
   * Update an existing webhook endpoint configuration.
   * @param webhookId  The webhook ID.
   * @param request    Updated fields (url, event, etc.).
   */
  updateWebhook(webhookId: number, request: WebhookRequest): Promise<ApiResponse<unknown>> {
    return this.http.put(`/v1/webhooks/${webhookId}`, request);
  }

  /**
   * Delete a webhook endpoint.
   * @param webhookId  The webhook ID.
   */
  deleteWebhook(webhookId: number): Promise<ApiResponse<unknown>> {
    return this.http.delete(`/v1/webhooks/${webhookId}`);
  }
}
