/**
 * CleansterApiClient — thin axios wrapper over the Cleanster Partner REST API.
 *
 * Authentication uses two headers per the Cleanster Partner API specification:
 *   access-key : the partner API access key (constant per deployment)
 *   token      : the user-level authorization token (per session)
 *
 * Both credentials are injected at construction time and attached to every
 * request. The tool handlers do not need to know about auth — it is entirely
 * encapsulated here.
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
  cleaner_id: string;
}

export interface AssignChecklistParams {
  checklist_id: string;
}

export class CleansterApiClient {
  private readonly http: AxiosInstance;

  constructor(accessKey: string, token?: string, baseUrl?: string) {
    this.http = axios.create({
      baseURL: baseUrl ?? process.env['CLEANSTER_API_BASE_URL'] ?? '',
      headers: {
        'access-key': accessKey,
        'token': token ?? '',
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

  async assignChecklist(bookingId: string, params: AssignChecklistParams): Promise<unknown> {
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

  async listCleaners(params: { status?: string; search?: string }): Promise<unknown> {
    const res: AxiosResponse = await this.http.get(ENDPOINTS.CLEANERS_LIST, { params });
    return res.data;
  }

  async getCleaner(id: string): Promise<unknown> {
    const res: AxiosResponse = await this.http.get(ENDPOINTS.CLEANER_GET(id));
    return res.data;
  }
}
