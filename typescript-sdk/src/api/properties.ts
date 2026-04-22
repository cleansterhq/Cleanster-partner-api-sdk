/**
 * PropertiesApi — CRUD for cleaning locations, plus cleaner assignment, iCal sync, and checklists.
 */

import { HttpClient } from "../http-client";
import {
  Property,
  CreatePropertyRequest,
  EnableDisablePropertyRequest,
  AssignCleanerToPropertyRequest,
  ICalRequest,
} from "../models/property";
import { ApiResponse } from "../models/response";

export class PropertiesApi {
  constructor(private readonly http: HttpClient) {}

  /**
   * List all properties, optionally filtering by service type.
   * @param serviceId  Filter by service type ID. Omit to return all.
   */
  listProperties(serviceId?: number): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/properties", serviceId !== undefined ? { serviceId } : undefined);
  }

  /**
   * Add a new property.
   * @param request  name, address, city, country, roomCount, bathroomCount, serviceId.
   * @returns ApiResponse with the created Property.
   */
  addProperty(request: CreatePropertyRequest): Promise<ApiResponse<Property>> {
    return this.http.post<Property>("/v1/properties", request);
  }

  /**
   * Get details of a specific property.
   * @param propertyId  The property ID.
   */
  getProperty(propertyId: number): Promise<ApiResponse<Property>> {
    return this.http.get<Property>(`/v1/properties/${propertyId}`);
  }

  /**
   * Update an existing property.
   * @param propertyId  The property ID.
   * @param request     Updated property fields.
   */
  updateProperty(propertyId: number, request: CreatePropertyRequest): Promise<ApiResponse<Property>> {
    return this.http.put<Property>(`/v1/properties/${propertyId}`, request);
  }

  /**
   * Update additional/supplemental information fields for a property.
   * @param propertyId  The property ID.
   * @param data        Key-value pairs of additional fields.
   */
  updateAdditionalInformation(propertyId: number, data: Record<string, unknown>): Promise<ApiResponse<unknown>> {
    return this.http.put(`/v1/properties/${propertyId}/additional-information`, data);
  }

  /**
   * Enable or disable a property.
   * @param propertyId  The property ID.
   * @param request     { enabled: true | false }
   */
  enableOrDisableProperty(propertyId: number, request: EnableDisablePropertyRequest): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/properties/${propertyId}/enable-disable`, request);
  }

  /**
   * Permanently delete a property.
   * @param propertyId  The property ID.
   */
  deleteProperty(propertyId: number): Promise<ApiResponse<unknown>> {
    return this.http.delete(`/v1/properties/${propertyId}`);
  }

  /**
   * Get the list of cleaners assigned to a property.
   * @param propertyId  The property ID.
   */
  getPropertyCleaners(propertyId: number): Promise<ApiResponse<unknown>> {
    return this.http.get(`/v1/properties/${propertyId}/cleaners`);
  }

  /**
   * Assign a cleaner to a property's default cleaner pool.
   * @param propertyId  The property ID.
   * @param request     The cleaner ID to assign.
   */
  assignCleanerToProperty(propertyId: number, request: AssignCleanerToPropertyRequest): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/properties/${propertyId}/cleaners`, request);
  }

  /**
   * Remove a cleaner from a property's default cleaner pool.
   * @param propertyId  The property ID.
   * @param cleanerId   The cleaner's user ID.
   */
  unassignCleanerFromProperty(propertyId: number, cleanerId: number): Promise<ApiResponse<unknown>> {
    return this.http.delete(`/v1/properties/${propertyId}/cleaners/${cleanerId}`);
  }

  /**
   * Add an iCal calendar link for availability syncing (Airbnb, VRBO, etc.).
   * @param propertyId  The property ID.
   * @param request     The iCal feed URL.
   */
  addICalLink(propertyId: number, request: ICalRequest): Promise<ApiResponse<unknown>> {
    return this.http.put(`/v1/properties/${propertyId}/ical`, request);
  }

  /**
   * Get the current iCal link for a property.
   * @param propertyId  The property ID.
   */
  getICalLink(propertyId: number): Promise<ApiResponse<unknown>> {
    return this.http.get(`/v1/properties/${propertyId}/ical`);
  }

  /**
   * Remove the iCal calendar link from a property.
   * @param propertyId  The property ID.
   * @param request     The iCal feed URL to remove.
   */
  removeICalLink(propertyId: number, request: ICalRequest): Promise<ApiResponse<unknown>> {
    return this.http.delete(`/v1/properties/${propertyId}/ical`, request);
  }

  /**
   * Set a default checklist for a property.
   * @param propertyId               The property ID.
   * @param checklistId              The checklist ID.
   * @param updateUpcomingBookings   If true, applies to all upcoming bookings too.
   */
  assignChecklistToProperty(
    propertyId: number,
    checklistId: number,
    updateUpcomingBookings = false,
  ): Promise<ApiResponse<unknown>> {
    return this.http.put(
      `/v1/properties/${propertyId}/checklist/${checklistId}?updateUpcomingBookings=${updateUpcomingBookings}`,
    );
  }
}
