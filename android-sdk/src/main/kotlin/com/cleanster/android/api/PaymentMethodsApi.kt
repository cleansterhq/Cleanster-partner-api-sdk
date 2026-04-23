package com.cleanster.android.api

import com.cleanster.android.model.*
import retrofit2.Retrofit
import retrofit2.http.*

internal interface PaymentMethodsService {
    @GET("v1/payment-methods/setup-intent-details")
    suspend fun getSetupIntentDetails(): ApiResponse<Any>

    @GET("v1/payment-methods/paypal-client-token")
    suspend fun getPayPalClientToken(): ApiResponse<Any>

    @POST("v1/payment-methods")
    suspend fun addPaymentMethod(@Body body: AddPaymentMethodRequest): ApiResponse<PaymentMethod>

    @GET("v1/payment-methods")
    suspend fun getPaymentMethods(): ApiResponse<List<PaymentMethod>>

    @PUT("v1/payment-methods/{paymentMethodId}/default")
    suspend fun setDefaultPaymentMethod(
        @Path("paymentMethodId") paymentMethodId: Int,
    ): ApiResponse<Any>

    @DELETE("v1/payment-methods/{paymentMethodId}")
    suspend fun deletePaymentMethod(
        @Path("paymentMethodId") paymentMethodId: Int,
    ): ApiResponse<Any>
}

class PaymentMethodsApi(retrofit: Retrofit) {
    private val service = retrofit.create(PaymentMethodsService::class.java)

    suspend fun getSetupIntentDetails() = wrap { service.getSetupIntentDetails() }

    suspend fun getPayPalClientToken() = wrap { service.getPayPalClientToken() }

    suspend fun addPaymentMethod(paymentMethodId: String) =
        wrap { service.addPaymentMethod(AddPaymentMethodRequest(paymentMethodId)) }

    suspend fun getPaymentMethods() = wrap { service.getPaymentMethods() }

    suspend fun setDefaultPaymentMethod(paymentMethodId: Int) =
        wrap { service.setDefaultPaymentMethod(paymentMethodId) }

    suspend fun deletePaymentMethod(paymentMethodId: Int) =
        wrap { service.deletePaymentMethod(paymentMethodId) }
}
