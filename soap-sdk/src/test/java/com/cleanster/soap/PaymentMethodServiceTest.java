package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock private SOAPTransport transport;
    private CleansterSOAPClient client;

    @BeforeEach
    void setUp() {
        lenient().when(transport.getObjectMapper()).thenReturn(MAPPER);
        lenient().when(transport.extractData(any())).thenAnswer(inv -> inv.getArgument(0));
        client = new CleansterSOAPClient(transport);
    }

    // GetSetupIntentDetails
    @Test
    @DisplayName("getSetupIntentDetails calls GET /v1/payment-methods/setup-intent-details")
    void getSetupIntentCallsCorrectPath() {
        when(transport.get("/v1/payment-methods/setup-intent-details")).thenReturn(MAPPER.createObjectNode());
        client.getSetupIntentDetails();
        verify(transport).get("/v1/payment-methods/setup-intent-details");
    }

    @Test
    @DisplayName("getSetupIntentDetails returns non-null node")
    void getSetupIntentReturnsNode() {
        when(transport.get("/v1/payment-methods/setup-intent-details")).thenReturn(MAPPER.createObjectNode());
        JsonNode result = client.getSetupIntentDetails();
        assertNotNull(result);
    }

    // GetPaypalClientToken
    @Test
    @DisplayName("getPaypalClientToken calls GET /v1/payment-methods/paypal-client-token")
    void getPaypalTokenCallsCorrectPath() {
        when(transport.get("/v1/payment-methods/paypal-client-token")).thenReturn(MAPPER.createObjectNode());
        client.getPaypalClientToken();
        verify(transport).get("/v1/payment-methods/paypal-client-token");
    }

    @Test
    @DisplayName("getPaypalClientToken returns non-null node")
    void getPaypalTokenReturnsNode() {
        when(transport.get("/v1/payment-methods/paypal-client-token")).thenReturn(MAPPER.createObjectNode());
        assertNotNull(client.getPaypalClientToken());
    }

    // AddPaymentMethod
    @Test
    @DisplayName("addPaymentMethod calls POST /v1/payment-methods")
    void addPaymentMethodCallsCorrectPath() {
        when(transport.post(eq("/v1/payment-methods"), any())).thenReturn(paymentMethodNode(10L));
        client.addPaymentMethod("pm_test_123");
        verify(transport).post(eq("/v1/payment-methods"), any());
    }

    @Test
    @DisplayName("addPaymentMethod returns PaymentMethod")
    void addPaymentMethodReturnsMethod() {
        when(transport.post(eq("/v1/payment-methods"), any())).thenReturn(paymentMethodNode(10L));
        PaymentMethod result = client.addPaymentMethod("pm_test_123");
        assertEquals(10L, result.getId());
    }

    // GetPaymentMethods
    @Test
    @DisplayName("getPaymentMethods calls GET /v1/payment-methods")
    void getPaymentMethodsCallsCorrectPath() {
        when(transport.get("/v1/payment-methods")).thenReturn(MAPPER.createArrayNode());
        client.getPaymentMethods();
        verify(transport).get("/v1/payment-methods");
    }

    @Test
    @DisplayName("getPaymentMethods returns list")
    void getPaymentMethodsReturnsList() {
        when(transport.get("/v1/payment-methods")).thenReturn(MAPPER.createArrayNode());
        List<PaymentMethod> result = client.getPaymentMethods();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // DeletePaymentMethod
    @Test
    @DisplayName("deletePaymentMethod calls DELETE /v1/payment-methods/{id}")
    void deletePaymentMethodCallsCorrectPath() {
        when(transport.delete("/v1/payment-methods/10")).thenReturn(okNode());
        client.deletePaymentMethod(10L);
        verify(transport).delete("/v1/payment-methods/10");
    }

    @Test
    @DisplayName("deletePaymentMethod returns success response")
    void deletePaymentMethodReturnsSuccess() {
        when(transport.delete("/v1/payment-methods/10")).thenReturn(okNode());
        ApiResponse resp = client.deletePaymentMethod(10L);
        assertTrue(resp.isSuccess());
    }

    // SetDefaultPaymentMethod
    @Test
    @DisplayName("setDefaultPaymentMethod calls PUT /v1/payment-methods/{id}/default")
    void setDefaultCallsCorrectPath() {
        when(transport.put(eq("/v1/payment-methods/10/default"), any())).thenReturn(okNode());
        client.setDefaultPaymentMethod(10L);
        verify(transport).put(eq("/v1/payment-methods/10/default"), any());
    }

    @Test
    @DisplayName("setDefaultPaymentMethod returns success response")
    void setDefaultReturnsSuccess() {
        when(transport.put(eq("/v1/payment-methods/10/default"), any())).thenReturn(okNode());
        ApiResponse resp = client.setDefaultPaymentMethod(10L);
        assertTrue(resp.isSuccess());
    }

    private JsonNode paymentMethodNode(long id) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("id", id);
        n.put("brand", "visa");
        n.put("last4", "4242");
        return n;
    }

    private JsonNode okNode() {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("status", 200);
        n.put("message", "OK");
        return n;
    }
}
