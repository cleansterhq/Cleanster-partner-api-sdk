/**
 * PaymentMethodsApi — manage Stripe and PayPal payment methods.
 */

import { HttpClient } from "../http-client";
import { AddPaymentMethodRequest } from "../models/payment-method";
import { ApiResponse } from "../models/response";

export class PaymentMethodsApi {
  constructor(private readonly http: HttpClient) {}

  /**
   * Get Stripe SetupIntent details for collecting card information client-side.
   * Use the returned clientSecret with Stripe.js or the Stripe mobile SDK.
   */
  getSetupIntentDetails(): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/payment-methods/setup-intent-details");
  }

  /**
   * Get a PayPal client token for rendering the PayPal button client-side.
   */
  getPayPalClientToken(): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/payment-methods/paypal-client-token");
  }

  /**
   * Save a new payment method after client-side tokenization.
   * @param request  Stripe paymentMethodId string or PayPal nonce.
   */
  addPaymentMethod(request: AddPaymentMethodRequest): Promise<ApiResponse<unknown>> {
    return this.http.post("/v1/payment-methods", request);
  }

  /**
   * Return all saved payment methods for the current user.
   */
  getPaymentMethods(): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/payment-methods");
  }

  /**
   * Delete a saved payment method.
   * @param paymentMethodId  The payment method ID.
   */
  deletePaymentMethod(paymentMethodId: number): Promise<ApiResponse<unknown>> {
    return this.http.delete(`/v1/payment-methods/${paymentMethodId}`);
  }

  /**
   * Set a payment method as the default for future bookings.
   * @param paymentMethodId  The payment method ID.
   */
  setDefaultPaymentMethod(paymentMethodId: number): Promise<ApiResponse<unknown>> {
    return this.http.put(`/v1/payment-methods/${paymentMethodId}/default`);
  }
}
