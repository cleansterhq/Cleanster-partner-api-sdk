package com.cleanster.xml;

import com.cleanster.xml.api.OtherXmlApi;
import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.XmlApiResponse;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OtherTest {

    private XmlHttpClient http;
    private OtherXmlApi   api;

    private static final String OK_JSON = "{\"success\":true,\"message\":\"OK\",\"data\":{}}";

    @BeforeEach void setUp() {
        XmlHttpClient real = XmlHttpClient.builder().baseUrl("http://dummy").accessKey("key").build();
        http = spy(real);
        api  = new OtherXmlApi(http);
    }

    // ── getServices ────────────────────────────────────────────────────────────

    @Test void getServices_callsGet() {
        doReturn(OK_JSON).when(http).get("/v1/services");
        api.getServices();
        verify(http).get("/v1/services");
    }

    @Test void getServices_returnsSuccess() {
        doReturn(OK_JSON).when(http).get("/v1/services");
        assertTrue(api.getServices().isSuccess());
    }

    // ── getPlans ───────────────────────────────────────────────────────────────

    @Test void getPlans_callsGetWithPropertyId() {
        doReturn(OK_JSON).when(http).get("/v1/plans?propertyId=5");
        api.getPlans(5);
        verify(http).get("/v1/plans?propertyId=5");
    }

    @Test void getPlans_returnsSuccess() {
        doReturn(OK_JSON).when(http).get("/v1/plans?propertyId=1");
        assertTrue(api.getPlans(1).isSuccess());
    }

    // ── getRecommendedHours ────────────────────────────────────────────────────

    @Test void getRecommendedHours_callsGetWithParams() {
        String path = "/v1/recommended-hours?propertyId=5&bathroomCount=2&roomCount=3";
        doReturn(OK_JSON).when(http).get(path);
        api.getRecommendedHours(5, 2, 3);
        verify(http).get(path);
    }

    // ── getCostEstimate ────────────────────────────────────────────────────────

    @Test void getCostEstimate_callsPost() {
        doReturn(OK_JSON).when(http).post(eq("/v1/cost-estimate"), any());
        api.getCostEstimate(Map.of("propertyId", 5, "planId", 1));
        verify(http).post(eq("/v1/cost-estimate"), any());
    }

    @Test void getCostEstimate_returnsSuccess() {
        doReturn(OK_JSON).when(http).post(eq("/v1/cost-estimate"), any());
        assertTrue(api.getCostEstimate(Map.of("propertyId", 5)).isSuccess());
    }

    // ── getCleaningExtras ──────────────────────────────────────────────────────

    @Test void getCleaningExtras_callsGetWithId() {
        doReturn(OK_JSON).when(http).get("/v1/cleaning-extras/3");
        api.getCleaningExtras(3);
        verify(http).get("/v1/cleaning-extras/3");
    }

    // ── getAvailableCleaners ───────────────────────────────────────────────────

    @Test void getAvailableCleaners_callsPost() {
        doReturn(OK_JSON).when(http).post(eq("/v1/available-cleaners"), any());
        api.getAvailableCleaners(Map.of("propertyId", 5, "date", "2025-09-15"));
        verify(http).post(eq("/v1/available-cleaners"), any());
    }

    // ── getCoupons ─────────────────────────────────────────────────────────────

    @Test void getCoupons_callsGet() {
        doReturn(OK_JSON).when(http).get("/v1/coupons");
        api.getCoupons();
        verify(http).get("/v1/coupons");
    }

    // ── listCleaners ───────────────────────────────────────────────────────────

    @Test void listCleaners_noFilters_callsBaseGet() {
        doReturn(OK_JSON).when(http).get("/v1/cleaners");
        api.listCleaners();
        verify(http).get("/v1/cleaners");
    }

    @Test void listCleaners_withStatus_callsGetWithParam() {
        doReturn(OK_JSON).when(http).get("/v1/cleaners?status=active");
        api.listCleaners("active", null);
        verify(http).get("/v1/cleaners?status=active");
    }

    @Test void listCleaners_withStatusAndSearch_callsGetWithBothParams() {
        doReturn(OK_JSON).when(http).get("/v1/cleaners?status=active&search=alice");
        api.listCleaners("active", "alice");
        verify(http).get("/v1/cleaners?status=active&search=alice");
    }

    // ── getCleaner ─────────────────────────────────────────────────────────────

    @Test void getCleaner_callsGetWithId() {
        doReturn(OK_JSON).when(http).get("/v1/cleaners/7");
        api.getCleaner(7);
        verify(http).get("/v1/cleaners/7");
    }

    @Test void getCleaner_returnsSuccess() {
        doReturn(OK_JSON).when(http).get("/v1/cleaners/1");
        assertTrue(api.getCleaner(1).isSuccess());
    }
}
