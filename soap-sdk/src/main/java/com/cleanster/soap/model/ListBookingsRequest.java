package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Request object for the ListBookings SOAP operation. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListBookingsRequest {

    private String  status;
    @JsonProperty("property_id") private Long    propertyId;
    private Integer page;
    @JsonProperty("per_page")    private Integer perPage;

    public String  getStatus()     { return status; }
    public Long    getPropertyId() { return propertyId; }
    public Integer getPage()       { return page; }
    public Integer getPerPage()    { return perPage; }

    public ListBookingsRequest setStatus(String status)       { this.status = status; return this; }
    public ListBookingsRequest setPropertyId(Long id)         { this.propertyId = id; return this; }
    public ListBookingsRequest setPage(Integer page)          { this.page = page; return this; }
    public ListBookingsRequest setPerPage(Integer perPage)    { this.perPage = perPage; return this; }
}
