import Foundation

/// API methods for attaching and managing Stripe and PayPal payment methods.
public final class PaymentMethodsApi {
    private let client: CleansterClient
    init(client: CleansterClient) { self.client = client }

    /// Get the Stripe Setup Intent client secret.
    ///
    /// Pass the returned `clientSecret` to `Stripe.confirmCardSetup()` in your iOS app.
    /// After Stripe.js / the Stripe iOS SDK confirms the setup, pass the resulting
    /// `paymentMethod.id` to `addPaymentMethod(_:)`.
    public func getSetupIntentDetails() async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(
            method: "GET",
            path: "/v1/payment-methods/setup-intent-details"
        )
    }

    /// Get a PayPal/Braintree client token for the PayPal Vault SDK.
    public func getPayPalClientToken() async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(
            method: "GET",
            path: "/v1/payment-methods/paypal-client-token"
        )
    }

    /// Save a tokenized payment method to the user's profile.
    ///
    /// - Parameter paymentMethodId: A Stripe `pm_xxx` token or a PayPal Braintree nonce
    ///   obtained from the respective client SDK.
    public func addPaymentMethod(_ paymentMethodId: String) async throws -> ApiResponse<PaymentMethod> {
        let body = AddPaymentMethodRequest(paymentMethodId: paymentMethodId)
        return try await client.request(method: "POST", path: "/v1/payment-methods", body: body)
    }

    /// List all saved payment methods for the current user.
    public func getPaymentMethods() async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(method: "GET", path: "/v1/payment-methods")
    }

    /// Mark a payment method as the default for this user.
    public func setDefaultPaymentMethod(_ paymentMethodId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(
            method: "PUT",
            path: "/v1/payment-methods/\(paymentMethodId)/default"
        )
    }

    /// Remove a saved payment method permanently.
    public func deletePaymentMethod(_ paymentMethodId: Int) async throws -> ApiResponse<AnyCodable> {
        return try await client.requestRaw(
            method: "DELETE",
            path: "/v1/payment-methods/\(paymentMethodId)"
        )
    }
}
