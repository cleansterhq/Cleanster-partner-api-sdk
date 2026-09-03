package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.Checklist;
import com.cleanster.xml.model.ChecklistTask;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Checklists API - manage reusable cleaning task lists.
 *
 * <h3>Endpoints (6)</h3>
 * <ol>
 *   <li>GET    /v1/checklist             - list checklists</li>
 *   <li>GET    /v1/checklist/{id}        - get checklist</li>
 *   <li>POST   /v1/checklist             - create checklist</li>
 *   <li>PUT    /v1/checklist/{id}        - update checklist</li>
 *   <li>DELETE /v1/checklist/{id}        - delete checklist</li>
 *   <li>POST   /v1/checklist/upload-image - upload checklist image</li>
 * </ol>
 */
public class ChecklistsXmlApi {

    private final XmlHttpClient http;

    public ChecklistsXmlApi(XmlHttpClient http) { this.http = http; }

    /** Return all checklists for the partner account. */
    public XmlApiResponse<List<Checklist>> listChecklists() {
        String json = http.get("/v1/checklist");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<Checklist>>>(){}.getType());
    }

    /** Get a specific checklist and all its task items. */
    public XmlApiResponse<Checklist> getChecklist(int checklistId) {
        String json = http.get("/v1/checklist/" + checklistId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Checklist>>(){}.getType());
    }

    /** Create a checklist using the live title/tasks contract. */
    public XmlApiResponse<Checklist> createChecklist(String title, List<ChecklistTask> tasks) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        body.put("tasks", tasks != null ? tasks : List.of());
        String json = http.post("/v1/checklist", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Checklist>>(){}.getType());
    }

    /** Replace a checklist's title and structured tasks. */
    public XmlApiResponse<Checklist> updateChecklist(int checklistId, String title, List<ChecklistTask> tasks) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        body.put("tasks", tasks != null ? tasks : List.of());
        String json = http.put("/v1/checklist/" + checklistId, body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Checklist>>(){}.getType());
    }

    /** Permanently delete a checklist. */
    public XmlApiResponse<Checklist> deleteChecklist(int checklistId) {
        String json = http.delete("/v1/checklist/" + checklistId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<Checklist>>(){}.getType());
    }

    /**
     * Upload an image via multipart/form-data.
     * Sends the image in the {@code file} form field.
     *
     * @param imageData  Raw image bytes.
     * @param fileName   File name for the multipart part (e.g. {@code "photo.jpg"}).
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse uploadChecklistImage(byte[] imageData, String fileName) {
        String json = http.postMultipart("/v1/checklist/upload-image", imageData, fileName);
        return http.fromJson(json, XmlApiResponse.class);
    }
}
