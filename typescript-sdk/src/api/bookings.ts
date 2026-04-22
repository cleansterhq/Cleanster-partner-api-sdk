/**
 * BookingsApi — manage the full lifecycle of cleaning appointments.
 */

import { HttpClient } from "../http-client";
import {
  Booking,
  CreateBookingRequest,
  CancelBookingRequest,
  RescheduleBookingRequest,
  AdjustHoursRequest,
  AssignCleanerRequest,
  PayExpensesRequest,
  FeedbackRequest,
  TipRequest,
  SendMessageRequest,
} from "../models/booking";
import { ApiResponse } from "../models/response";

export class BookingsApi {
  constructor(private readonly http: HttpClient) {}

  /**
   * List upcoming and past bookings.
   * @param pageNo   Page number (1-based). Omit to use default.
   * @param status   Filter by status: OPEN | CLEANER_ASSIGNED | COMPLETED | CANCELLED | REMOVED
   */
  getBookings(pageNo?: number, status?: string): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/bookings", {
      pageNo,
      status,
    });
  }

  /**
   * Schedule a new cleaning appointment.
   * @param request  Full booking details including date, time, propertyId, planId, hours, etc.
   */
  createBooking(request: CreateBookingRequest): Promise<ApiResponse<Booking>> {
    return this.http.post<Booking>("/v1/bookings/create", request);
  }

  /**
   * Get full details of a specific booking.
   * @param bookingId  The booking ID.
   */
  getBookingDetails(bookingId: number): Promise<ApiResponse<Booking>> {
    return this.http.get<Booking>(`/v1/bookings/${bookingId}`);
  }

  /**
   * Cancel a booking.
   * @param bookingId  The booking ID.
   * @param request    Optional cancellation reason.
   */
  cancelBooking(bookingId: number, request: CancelBookingRequest = {}): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/bookings/${bookingId}/cancel`, request);
  }

  /**
   * Reschedule a booking to a new date and time.
   * @param bookingId  The booking ID.
   * @param request    New date (YYYY-MM-DD) and time (HH:mm).
   */
  rescheduleBooking(bookingId: number, request: RescheduleBookingRequest): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/bookings/${bookingId}/reschedule`, request);
  }

  /**
   * Manually assign a specific cleaner to a booking.
   * @param bookingId  The booking ID.
   * @param request    The cleaner ID to assign.
   */
  assignCleaner(bookingId: number, request: AssignCleanerRequest): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/bookings/${bookingId}/cleaner`, request);
  }

  /**
   * Remove the currently assigned cleaner from a booking.
   * @param bookingId  The booking ID.
   */
  removeAssignedCleaner(bookingId: number): Promise<ApiResponse<unknown>> {
    return this.http.delete(`/v1/bookings/${bookingId}/cleaner`);
  }

  /**
   * Adjust the number of hours for a booking.
   * @param bookingId  The booking ID.
   * @param request    New hours value.
   */
  adjustHours(bookingId: number, request: AdjustHoursRequest): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/bookings/${bookingId}/hours`, request);
  }

  /**
   * Pay outstanding expenses for a completed booking (within 72h of completion).
   * @param bookingId  The booking ID.
   * @param request    Payment method to charge.
   */
  payExpenses(bookingId: number, request: PayExpensesRequest): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/bookings/${bookingId}/expenses`, request);
  }

  /**
   * Get the inspection report for a completed booking.
   * @param bookingId  The booking ID.
   */
  getBookingInspection(bookingId: number): Promise<ApiResponse<unknown>> {
    return this.http.get(`/v1/bookings/${bookingId}/inspection`);
  }

  /**
   * Get detailed inspection information for a completed booking.
   * @param bookingId  The booking ID.
   */
  getBookingInspectionDetails(bookingId: number): Promise<ApiResponse<unknown>> {
    return this.http.get(`/v1/bookings/${bookingId}/inspection/details`);
  }

  /**
   * Attach a cleaning checklist to a specific booking.
   * Overrides the property's default checklist for this booking.
   * @param bookingId    The booking ID.
   * @param checklistId  The checklist ID.
   */
  assignChecklistToBooking(bookingId: number, checklistId: number): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/bookings/${bookingId}/checklist/${checklistId}`);
  }

  /**
   * Submit a star rating and optional comment after a booking completes.
   * @param bookingId  The booking ID.
   * @param request    Rating (1–5) and optional comment.
   */
  submitFeedback(bookingId: number, request: FeedbackRequest): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/bookings/${bookingId}/feedback`, request);
  }

  /**
   * Add a tip for the cleaner (within 72h of booking completion).
   * @param bookingId  The booking ID.
   * @param request    Tip amount and payment method ID.
   */
  addTip(bookingId: number, request: TipRequest): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/bookings/${bookingId}/tip`, request);
  }

  /**
   * Retrieve all chat messages for a booking thread.
   * @param bookingId  The booking ID.
   */
  getChat(bookingId: number): Promise<ApiResponse<unknown>> {
    return this.http.get(`/v1/bookings/${bookingId}/chat`);
  }

  /**
   * Post a chat message in a booking thread.
   * @param bookingId  The booking ID.
   * @param request    Message text to send.
   */
  sendMessage(bookingId: number, request: SendMessageRequest): Promise<ApiResponse<unknown>> {
    return this.http.post(`/v1/bookings/${bookingId}/chat`, request);
  }

  /**
   * Delete a specific chat message.
   * @param bookingId  The booking ID.
   * @param messageId  The message ID string.
   */
  deleteMessage(bookingId: number, messageId: string): Promise<ApiResponse<unknown>> {
    return this.http.delete(`/v1/bookings/${bookingId}/chat/${messageId}`);
  }
}
