/**
 * OtherApi — utility endpoints for services, plans, cost estimates, extras, and coupons.
 */

import { HttpClient } from "../http-client";
import { ApiResponse } from "../models/response";

export interface CostEstimateRequest {
  propertyId: number;
  planId: number;
  hours: number;
  couponCode?: string;
  extras?: number[];
}

export interface AvailableCleanersRequest {
  propertyId: number;
  date: string;  // YYYY-MM-DD
  time: string;  // HH:mm
}

export class OtherApi {
  constructor(private readonly http: HttpClient) {}

  /**
   * Return all available cleaning service types (e.g. Residential, Commercial, Airbnb).
   */
  getServices(): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/services");
  }

  /**
   * Return available booking plans for a given property.
   * @param propertyId  The property ID.
   */
  getPlans(propertyId: number): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/plans", { propertyId });
  }

  /**
   * Get the system-recommended cleaning hours based on property size.
   * Use this to pre-fill the hours field when creating a booking.
   * @param propertyId     The property ID.
   * @param bathroomCount  Number of bathrooms.
   * @param roomCount      Number of rooms/bedrooms.
   */
  getRecommendedHours(propertyId: number, bathroomCount: number, roomCount: number): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/recommended-hours", { propertyId, bathroomCount, roomCount });
  }

  /**
   * Calculate the estimated cost for a potential booking.
   * Use this to show a price preview before the user commits.
   * @param request  propertyId, planId, hours, and optional couponCode / extras.
   */
  getCostEstimate(request: CostEstimateRequest): Promise<ApiResponse<unknown>> {
    return this.http.post("/v1/cost-estimate", request);
  }

  /**
   * Get available add-on services for a given service type.
   * @param serviceId  The service type ID (from getServices).
   */
  getCleaningExtras(serviceId: number): Promise<ApiResponse<unknown>> {
    return this.http.get(`/v1/cleaning-extras/${serviceId}`);
  }

  /**
   * Find cleaners available for a specific property, date, and time slot.
   * @param request  propertyId, date (YYYY-MM-DD), and time (HH:mm).
   */
  getAvailableCleaners(request: AvailableCleanersRequest): Promise<ApiResponse<unknown>> {
    return this.http.post("/v1/available-cleaners", request);
  }

  /**
   * Return all valid coupon codes available for use at booking creation.
   */
  getCoupons(): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/coupons");
  }

  /**
   * List all cleaners, with optional status and search filters.
   * @param status  Filter by cleaner status ('active', 'inactive', 'pending').
   * @param search  Partial match against cleaner name or email.
   */
  listCleaners(status?: string, search?: string): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/cleaners", { status, search });
  }

  /**
   * Retrieve a single cleaner by their ID.
   * @param cleanerId  The cleaner's unique ID.
   */
  getCleaner(cleanerId: number): Promise<ApiResponse<unknown>> {
    return this.http.get(`/v1/cleaners/${cleanerId}`);
  }
}
