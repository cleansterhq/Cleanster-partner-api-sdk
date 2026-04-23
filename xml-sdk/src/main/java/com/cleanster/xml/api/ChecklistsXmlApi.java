package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Checklist;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Checklists API — manage cleaning checklists.
 *
 * <h3>Endpoints (5)</h3>
 * <ol>
 *   <li>GET    /checklists       — list checklists</li>
 *   <li>GET    /checklists/{id}  — get checklist</li>
 *   <li>POST   /checklists       — create checklist</li>
 *   <li>PUT    /checklists/{id}  — update checklist</li>
 *   <li>DELETE /checklists/{id}  — delete checklist</li>
 * </ol>
 */
public class ChecklistsXmlApi {

    private final XmlHttpClient http;

    public ChecklistsXmlApi(XmlHttpClient http) { this.http = http; }

    public XmlApiResponse<List<Checklist>> listChecklists() {
        String json = http.get("/checklists");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Checklist>>>(){}.getType());
    }

    public XmlApiResponse<Checklist> getChecklist(int checklistId) {
        String json = http.get("/checklists/" + checklistId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Checklist>>(){}.getType());
    }

    public XmlApiResponse<Checklist> createChecklist(String name, String description,
                                                       List<String> items, Integer propertyId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name",        name);
        body.put("description", description);
        body.put("items",       items != null ? items : List.of());
        if (propertyId != null) body.put("propertyId", propertyId);
        String json = http.post("/checklists", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Checklist>>(){}.getType());
    }

    public XmlApiResponse<Checklist> updateChecklist(int checklistId, Map<String, Object> body) {
        String json = http.put("/checklists/" + checklistId, body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Checklist>>(){}.getType());
    }

    public XmlApiResponse<Checklist> deleteChecklist(int checklistId) {
        String json = http.delete("/checklists/" + checklistId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Checklist>>(){}.getType());
    }
}
