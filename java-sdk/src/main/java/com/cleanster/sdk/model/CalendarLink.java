package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single calendar (iCal) link attached to a property.
 */
public class CalendarLink {

    @JsonProperty("id")
    private int id;

    @JsonProperty("calendarLink")
    private String calendarLink;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCalendarLink() { return calendarLink; }
    public void setCalendarLink(String calendarLink) { this.calendarLink = calendarLink; }
}
