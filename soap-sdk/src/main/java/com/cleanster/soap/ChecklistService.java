package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implements all checklist-related SOAP operations. */
public class ChecklistService {

    private final SOAPTransport transport;

    public ChecklistService(SOAPTransport transport) {
        this.transport = transport;
    }

    public List<Checklist> listChecklists(int page, int perPage) {
        JsonNode root = transport.get(
                "/v1/checklist?page=" + page + "&per_page=" + perPage);
        JsonNode data = transport.extractData(root);
        List<Checklist> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, Checklist.class));
            }
        }
        return list;
    }

    public Checklist getChecklist(long checklistId) {
        JsonNode root = transport.get("/v1/checklist/" + checklistId);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Checklist.class);
    }

    public Checklist createChecklist(String title, List<ChecklistTask> tasks) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("tasks", tasks != null ? tasks : List.of());
        JsonNode root = transport.post("/v1/checklist", body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Checklist.class);
    }

    public Checklist updateChecklist(long checklistId, String title, List<ChecklistTask> tasks) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("tasks", tasks != null ? tasks : List.of());
        JsonNode root = transport.put("/v1/checklist/" + checklistId, body);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Checklist.class);
    }

    public ApiResponse deleteChecklist(long checklistId) {
        JsonNode root = transport.delete("/v1/checklist/" + checklistId);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        return new ApiResponse(status, "OK");
    }

    public ApiResponse uploadChecklistImage(byte[] imageData, String fileName) {
        JsonNode root = transport.postMultipart(
                "/v1/checklist/upload-image", imageData, fileName);
        int status = root.has("status") ? root.get("status").asInt(200) : 200;
        String message = root.has("message") ? root.get("message").asText() : "OK";
        return new ApiResponse(status, message);
    }
}
