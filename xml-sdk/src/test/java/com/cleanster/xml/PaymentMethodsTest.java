package com.cleanster.xml;

import com.cleanster.xml.api.PaymentMethodsXmlApi;
import com.cleanster.xml.client.XmlConverter;
import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.PaymentMethod;
import com.cleanster.xml.model.XmlApiResponse;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentMethodsTest {

    private XmlHttpClient        http;
    private PaymentMethodsXmlApi api;

    private static final String PM_JSON   = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":{\"id\":10,\"brand\":\"Visa\",\"last4\":\"4242\"}}";
    private static final String LIST_JSON = "{\"success\":true,\"message\":\"OK\","
            + "\"data\":[{\"id\":10,\"brand\":\"Visa\",\"last4\":\"4242\"}]}";
    private static final String OK_JSON   = "{\"success\":true,\"message\":\"OK\",\"data\":{}}";

    @BeforeEach void setUp() {
        XmlHttpClient real = XmlHttpClient.builder().baseUrl("http://dummy").accessKey("key").build();
        http = spy(real);
        api  = new PaymentMethodsXmlApi(http);
    }

    // ── getSetupIntentDetails ──────────────────────────────────────────────────

    @Test void getSetupIntentDetails_callsGet() {
        doReturn(OK_JSON).when(http).get("/v1/payment-methods/setup-intent-details");
        api.getSetupIntentDetails();
        verify(http).get("/v1/payment-methods/setup-intent-details");
    }

    @Test void getSetupIntentDetails_returnsSuccess() {
        doReturn(OK_JSON).when(http).get("/v1/payment-methods/setup-intent-details");
        assertTrue(api.getSetupIntentDetails().isSuccess());
    }

    // ── getPaypalClientToken ───────────────────────────────────────────────────

    @Test void getPaypalClientToken_callsGet() {
        doReturn(OK_JSON).when(http).get("/v1/payment-methods/paypal-client-token");
        api.getPaypalClientToken();
        verify(http).get("/v1/payment-methods/paypal-client-token");
    }

    @Test void getPaypalClientToken_returnsSuccess() {
        doReturn(OK_JSON).when(http).get("/v1/payment-methods/paypal-client-token");
        assertTrue(api.getPaypalClientToken().isSuccess());
    }

    // ── addPaymentMethod ───────────────────────────────────────────────────────

    @Test void addPaymentMethod_callsPost() {
        doReturn(PM_JSON).when(http).post(eq("/v1/payment-methods"), any());
        api.addPaymentMethod(Map.of("paymentMethodId", "pm_test_123"));
        verify(http).post(eq("/v1/payment-methods"), any());
    }

    @Test void addPaymentMethod_returnsParsedPaymentMethod() {
        doReturn(PM_JSON).when(http).post(eq("/v1/payment-methods"), any());
        XmlApiResponse<PaymentMethod> resp = api.addPaymentMethod(Map.of("paymentMethodId", "pm_test"));
        assertTrue(resp.isSuccess());
        assertEquals(10, (int) resp.getData().getId());
    }

    // ── listPaymentMethods ─────────────────────────────────────────────────────

    @Test void listPaymentMethods_callsGet() {
        doReturn(LIST_JSON).when(http).get("/v1/payment-methods");
        api.listPaymentMethods();
        verify(http).get("/v1/payment-methods");
    }

    @Test void listPaymentMethods_returnsList() {
        doReturn(LIST_JSON).when(http).get("/v1/payment-methods");
        XmlApiResponse<List<PaymentMethod>> resp = api.listPaymentMethods();
        assertTrue(resp.isSuccess());
        assertEquals(1, resp.getData().size());
        assertEquals("Visa", resp.getData().get(0).getBrand());
    }

    // ── deletePaymentMethod ────────────────────────────────────────────────────

    @Test void deletePaymentMethod_callsDeleteWithId() {
        doReturn(OK_JSON).when(http).delete("/v1/payment-methods/10");
        api.deletePaymentMethod(10);
        verify(http).delete("/v1/payment-methods/10");
    }

    @Test void deletePaymentMethod_returnsSuccess() {
        doReturn(OK_JSON).when(http).delete("/v1/payment-methods/10");
        assertTrue(api.deletePaymentMethod(10).isSuccess());
    }

    // ── setDefaultPaymentMethod ────────────────────────────────────────────────

    @Test void setDefaultPaymentMethod_callsPutWithId() {
        doReturn(OK_JSON).when(http).put(eq("/v1/payment-methods/10/default"), any());
        api.setDefaultPaymentMethod(10);
        verify(http).put(eq("/v1/payment-methods/10/default"), any());
    }

    @Test void setDefaultPaymentMethod_returnsSuccess() {
        doReturn(OK_JSON).when(http).put(eq("/v1/payment-methods/10/default"), any());
        assertTrue(api.setDefaultPaymentMethod(10).isSuccess());
    }

    // ── JAXB ───────────────────────────────────────────────────────────────────

    @Test void paymentMethod_toXml_isValidXml() {
        PaymentMethod pm = new PaymentMethod(); pm.setId(1); pm.setBrand("Visa"); pm.setLast4("4242");
        assertTrue(XmlConverter.isXml(XmlConverter.toXml(pm)));
    }

    @Test void paymentMethod_fromXml_roundTrip() {
        PaymentMethod o = new PaymentMethod(); o.setId(5); o.setBrand("MC"); o.setLast4("5678");
        PaymentMethod r = XmlConverter.fromXml(XmlConverter.toXml(o), PaymentMethod.class);
        assertEquals(5, (int) r.getId());
        assertEquals("MC", r.getBrand());
        assertEquals("5678", r.getLast4());
    }
}
