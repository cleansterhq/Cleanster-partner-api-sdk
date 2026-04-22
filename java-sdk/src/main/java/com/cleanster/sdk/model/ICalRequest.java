package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for adding or removing an iCal link to a property.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ICalRequest {

    @JsonProperty("icalLink")
    private String icalLink;

    public ICalRequest() {}

    public ICalRequest(String icalLink) {
        this.icalLink = icalLink;
    }

    public String getIcalLink() { return icalLink; }
    public void setIcalLink(String icalLink) { this.icalLink = icalLink; }
}
