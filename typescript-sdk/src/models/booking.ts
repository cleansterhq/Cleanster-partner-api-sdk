/**
 * Booking model — represents a single cleaning appointment.
 */
export interface Booking {
  id: number;
  status: "OPEN" | "CLEANER_ASSIGNED" | "COMPLETED" | "CANCELLED" | "REMOVED";
  date: string;         // YYYY-MM-DD
  time: string;         // HH:mm
  hours: number;
  cost: number;
  propertyId: number;
  cleanerId: number | null;
  planId: number;
  roomCount: number;
  bathroomCount: number;
  extraSupplies: boolean;
  paymentMethodId: number;
  postedBy?: number;
}

/** Request body for creating a new booking. */
export interface CreateBookingRequest {
  date: string;           // Required — YYYY-MM-DD
  time: string;           // Required — HH:mm (24-hour)
  propertyId: number;     // Required
  roomCount: number;      // Required
  bathroomCount: number;  // Required
  planId: number;         // Required — from getPlans()
  hours: number;          // Required — from getRecommendedHours()
  extraSupplies: boolean; // Required
  paymentMethodId: number; // Required
  couponCode?: string;    // Optional
  extras?: number[];      // Optional — extra service IDs
}

/** Request body for cancelling a booking. */
export interface CancelBookingRequest {
  reason?: string;
}

/** Request body for rescheduling a booking. */
export interface RescheduleBookingRequest {
  date: string;   // YYYY-MM-DD
  time: string;   // HH:mm
}

/** Request body for adjusting booking hours. */
export interface AdjustHoursRequest {
  hours: number;
}

/** Request body for assigning a cleaner to a booking. */
export interface AssignCleanerRequest {
  cleanerId: number;
}

/** Request body for paying booking expenses. */
export interface PayExpensesRequest {
  paymentMethodId: number;
}

/** Request body for submitting feedback. */
export interface FeedbackRequest {
  rating: number;     // 1–5
  comment?: string;
}

/** Request body for adding a tip. */
export interface TipRequest {
  amount: number;
  paymentMethodId: number;
}

/** Request body for sending a chat message. */
export interface SendMessageRequest {
  message: string;
}
