package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name = "pagedBookings")
@XmlAccessorType(XmlAccessType.FIELD)
public class PagedBookings {

    @XmlElement private List<Booking> content;

    public PagedBookings() {}

    public List<Booking> getContent() { return content; }

    public void setContent(List<Booking> content) { this.content = content; }

    @Override
    public String toString() {
        return "PagedBookings{content=" + content + "}";
    }
}
