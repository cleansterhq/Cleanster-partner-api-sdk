package com.cleanster.soap;

import com.cleanster.soap.model.Cleaner;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/** Implements all cleaner-related SOAP operations. */
public class CleanerService {

    private final SOAPTransport transport;

    public CleanerService(SOAPTransport transport) {
        this.transport = transport;
    }

    public List<Cleaner> listCleaners(String status, int page, int perPage) {
        StringBuilder path = new StringBuilder("/v1/cleaners?page=")
                .append(page).append("&per_page=").append(perPage);
        if (status != null && !status.isBlank())
            path.append("&status=").append(status);

        JsonNode root = transport.get(path.toString());
        JsonNode data = transport.extractData(root);
        List<Cleaner> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, Cleaner.class));
            }
        }
        return list;
    }

    public Cleaner getCleaner(long cleanerId) {
        JsonNode root = transport.get("/v1/cleaners/" + cleanerId);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Cleaner.class);
    }
}
