/**
 * PaymentMethod model — saved card or PayPal account.
 */
export interface PaymentMethod {
  id: number;
  type: "card" | "paypal" | string;
  lastFour?: string;
  brand?: string;
  isDefault: boolean;
}

/** Request body for adding a new payment method after client-side tokenization. */
export interface AddPaymentMethodRequest {
  paymentMethodId: string;  // Stripe PM token or PayPal nonce
  [key: string]: unknown;
}
