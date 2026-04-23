package cleanster

import (
	"context"
	"fmt"
)

// PaymentMethodsService manages Stripe and PayPal payment methods.
type PaymentMethodsService struct {
	http *httpClient
}

// GetSetupIntentDetails returns Stripe SetupIntent details for client-side card collection.
// Use the returned clientSecret with Stripe.js on the client side.
func (s *PaymentMethodsService) GetSetupIntentDetails(ctx context.Context) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.get(ctx, "/v1/payment-methods/setup-intent-details", nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// GetPayPalClientToken returns a PayPal client token for rendering the PayPal button client-side.
func (s *PaymentMethodsService) GetPayPalClientToken(ctx context.Context) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.get(ctx, "/v1/payment-methods/paypal-client-token", nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// AddPaymentMethod saves a new payment method after client-side tokenization.
func (s *PaymentMethodsService) AddPaymentMethod(ctx context.Context, req AddPaymentMethodRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, "/v1/payment-methods", req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// GetPaymentMethods returns all saved payment methods for the current user.
func (s *PaymentMethodsService) GetPaymentMethods(ctx context.Context) (APIResponse[[]PaymentMethod], error) {
	raw, err := s.http.get(ctx, "/v1/payment-methods", nil)
	if err != nil {
		return APIResponse[[]PaymentMethod]{}, err
	}
	return decode[[]PaymentMethod](raw)
}

// DeletePaymentMethod removes a saved payment method.
func (s *PaymentMethodsService) DeletePaymentMethod(ctx context.Context, paymentMethodID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.delete(ctx, fmt.Sprintf("/v1/payment-methods/%d", paymentMethodID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// SetDefaultPaymentMethod makes a saved payment method the default for future bookings.
func (s *PaymentMethodsService) SetDefaultPaymentMethod(ctx context.Context, paymentMethodID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.put(ctx, fmt.Sprintf("/v1/payment-methods/%d/default", paymentMethodID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}
