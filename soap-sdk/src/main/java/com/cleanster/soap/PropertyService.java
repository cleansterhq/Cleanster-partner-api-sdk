package com.cleanster.soap;

import com.cleanster.soap.model.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/** Implements all property-related SOAP operations. */
public class PropertyService {

    private final SOAPTransport transport;

    public PropertyService(SOAPTransport transport) {
        this.transport = transport;
    }

    public Property getProperty(long propertyId) {
        JsonNode root = transport.get("/v1/properties/" + propertyId);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Property.class);
    }

    public List<Property> listProperties(int page, int perPage) {
        JsonNode root = transport.get(
                "/v1/properties?page=" + page + "&per_page=" + perPage);
        JsonNode data = transport.extractData(root);
        List<Property> list = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                list.add(transport.getObjectMapper().convertValue(node, Property.class));
            }
        }
        return list;
    }

    public Property createProperty(CreatePropertyRequest request) {
        JsonNode root = transport.post("/v1/properties", request);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Property.class);
    }

    public Property updateProperty(long propertyId, CreatePropertyRequest request) {
        JsonNode root = transport.put("/v1/properties/" + propertyId, request);
        return transport.getObjectMapper().convertValue(transport.extractData(root), Property.class);
    }
}
