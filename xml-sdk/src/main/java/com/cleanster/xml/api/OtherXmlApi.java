package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.*;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Other API — miscellaneous endpoints (plans, coupons, extras, chat, etc.).
 *
 * <h3>Endpoints (7)</h3>
 * <ol>
 *   <li>GET  /plans                    — list available cleaning plans</li>
 *   <li>GET  /plans/{id}               — get a specific plan</li>
 *   <li>POST /coupons/validate         — validate a coupon code</li>
 *   <li>GET  /extras                   — list available extras</li>
 *   <li>GET  /chat/rules               — get chat window rules</li>
 *   <li>GET  /timeslots                — get available time slots</li>
 *   <li>GET  /config                   — get partner configuration</li>
 * </ol>
 */
public class OtherXmlApi {

    private final XmlHttpClient http;

    public OtherXmlApi(XmlHttpClient http) { this.http = http; }

    public XmlApiResponse<List<Plan>> listPlans() {
        String json = http.get("/plans");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Plan>>>(){}.getType());
    }

    public XmlApiResponse<Plan> getPlan(int planId) {
        String json = http.get("/plans/" + planId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Plan>>(){}.getType());
    }

    public XmlApiResponse<Coupon> validateCoupon(String couponCode) {
        String json = http.post("/coupons/validate", Map.of("code", couponCode));
        return http.fromJson(json, new TypeToken<XmlApiResponse<Coupon>>(){}.getType());
    }

    @SuppressWarnings("rawtypes")
    public XmlApiResponse listExtras() {
        String json = http.get("/extras");
        return http.fromJson(json, XmlApiResponse.class);
    }

    @SuppressWarnings("rawtypes")
    public XmlApiResponse getChatRules() {
        String json = http.get("/chat/rules");
        return http.fromJson(json, XmlApiResponse.class);
    }

    @SuppressWarnings("rawtypes")
    public XmlApiResponse getTimeslots(String date, Integer propertyId) {
        String path = "/timeslots?date=" + date
                + (propertyId != null ? "&propertyId=" + propertyId : "");
        String json = http.get(path);
        return http.fromJson(json, XmlApiResponse.class);
    }

    @SuppressWarnings("rawtypes")
    public XmlApiResponse getConfig() {
        String json = http.get("/config");
        return http.fromJson(json, XmlApiResponse.class);
    }
}
