package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for removing calendar links from a property.
 *
 * The real API deletes by numeric link ID (as returned from {@code getICalLink}),
 * not by URL.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteICalLinkRequest {

    @JsonProperty("ids")
    private List<Integer> ids;

    public DeleteICalLinkRequest() {}

    public DeleteICalLinkRequest(List<Integer> ids) {
        this.ids = ids;
    }

    public DeleteICalLinkRequest(int id) {
        this.ids = List.of(id);
    }

    public List<Integer> getIds() { return ids; }
    public void setIds(List<Integer> ids) { this.ids = ids; }
}
