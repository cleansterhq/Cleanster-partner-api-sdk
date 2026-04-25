/**
 * CleansterApiClient — thin axios wrapper over the Cleanster Partner REST API.
 *
 * Each method corresponds to one API operation. The token (API key or OAuth
 * access token) is injected at construction time and attached to every request
 * as a Bearer token.
 *
 * TODO: When OAuth 2.0 + PKCE is ready, swap the static token for a token
 *       refresher here. The tool handlers do not need to change — they all go
 *       through this client, so the auth upgrade is entirely contained here.
 */

import axios, { type AxiosInstance, type AxiosResponse } from 'axios';
import { ENDPOINTS } from './endpoints.js';

export interface ListBookingsParams {
  property_id?: string;
  status?: string;
  date_from?: string;
  date_to?: string;
  limit?: number;
}

export interface CreateBookingParams {
  property_id: string;
  service_type: string;
  scheduled_at: string;
  notes?: string;
  checklist_id?: string;
}

export interface RescheduleBookingParams {
  new_scheduled_at: string;
}

export interface AssignCrewParams {
  cleaner_ids: string[];
}

export interface UpdateChecklistParams {
  cleaner_id: string;
  checklist_items: Array<{ task: string; required: boolean }>;
}

export interface ListPayoutsParams {
  cleaner_id?: string;
  date_from: string;
  date_to: string;
}

export class CleansterApiClient {
  private readonly http: AxiosInstance;

  constructor(token: string, baseUrl?: string) {
    this.http = axios.create({
      baseURL: baseUrl ?? process.env['CLEANSTER_API_BASE_URL'] ?? '',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      timeout: 15_000,
    });
  }

  // ── Bookings ───────────────────────────────────────────────────────────────

  async listBookings(params: ListBookingsParams): Promise<unknown> {
    const res: AxiosResponse = await this.http.get(ENDPOINTS.BOOKINGS_LIST, { params });
    return res.data;
  }

  async getBooking(id: string): Promise<unknown> {
    const res: AxiosResponse = await this.http.get(ENDPOINTS.BOOKING_GET(id));
    return res.data;
  }

  async createBooking(body: CreateBookingParams): Promise<unknown> {
    const res: AxiosResponse = await this.http.post(ENDPOINTS.BOOKING_CREATE, body);
    return res.data;
  }

  async cancelBooking(id: string, reason?: string): Promise<unknown> {
    const res: AxiosResponse = await this.http.post(ENDPOINTS.BOOKING_CANCEL(id), { reason });
    return res.data;
  }

  async rescheduleBooking(id: string, params: RescheduleBookingParams): Promise<unknown> {
    const res: AxiosResponse = await this.http.post(ENDPOINTS.BOOKING_RESCHEDULE(id), params);
    return res.data;
  }

  async assignCrew(bookingId: string, params: AssignCrewParams): Promise<unknown> {
    const res: AxiosResponse = await this.http.post(ENDPOINTS.BOOKING_ASSIGN_CREW(bookingId), params);
    return res.data;
  }

  async updateChecklist(bookingId: string, params: UpdateChecklistParams): Promise<unknown> {
    const res: AxiosResponse = await this.http.put(ENDPOINTS.BOOKING_CHECKLIST(bookingId), params);
    return res.data;
  }

  // ── Properties ─────────────────────────────────────────────────────────────

  async listProperties(params: { account_id?: string; property_type?: string }): Promise<unknown> {
    const res: AxiosResponse = await this.http.get(ENDPOINTS.PROPERTIES_LIST, { params });
    return res.data;
  }

  async getProperty(id: string): Promise<unknown> {
    const res: AxiosResponse = await this.http.get(ENDPOINTS.PROPERTY_GET(id));
    return res.data;
  }

  // ── Cleaners ───────────────────────────────────────────────────────────────

  async listCleaners(params: { region?: string; available_on?: string }): Promise<unknown> {
    const res: AxiosResponse = await this.http.get(ENDPOINTS.CLEANERS_LIST, { params });
    return res.data;
  }

  // ── Payouts ────────────────────────────────────────────────────────────────

  async getPayoutRecords(params: ListPayoutsParams): Promise<unknown> {
    const res: AxiosResponse = await this.http.get(ENDPOINTS.PAYOUTS_LIST, { params });
    return res.data;
  }
}
