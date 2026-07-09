package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for adding an iCal link to a property.
 *
 * The real API accepts (and requires) an array of calendar links, each of which
 * must be a live, publicly fetchable .ics feed URL - it validates the feed content,
 * not just the URL shape.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ICalRequest {

    @JsonProperty("calendarLinks")
    private List<String> calendarLinks;

    public ICalRequest() {}

    public ICalRequest(List<String> calendarLinks) {
        this.calendarLinks = calendarLinks;
    }

    public ICalRequest(String calendarLink) {
        this.calendarLinks = List.of(calendarLink);
    }

    public List<String> getCalendarLinks() { return calendarLinks; }
    public void setCalendarLinks(List<String> calendarLinks) { this.calendarLinks = calendarLinks; }
}
