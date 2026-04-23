package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a Cleanster cleaner (service provider). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cleaner {

    private Long   id;
    private String name;
    private String email;
    private String phone;
    private String status;
    private Double rating;
    @JsonProperty("total_jobs") private Integer totalJobs;
    @JsonProperty("created_at") private String  createdAt;

    public Long    getId()        { return id; }
    public String  getName()      { return name; }
    public String  getEmail()     { return email; }
    public String  getPhone()     { return phone; }
    public String  getStatus()    { return status; }
    public Double  getRating()    { return rating; }
    public Integer getTotalJobs() { return totalJobs; }
    public String  getCreatedAt() { return createdAt; }

    public void setId(Long id)               { this.id = id; }
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPhone(String phone)       { this.phone = phone; }
    public void setStatus(String status)     { this.status = status; }
    public void setRating(Double rating)     { this.rating = rating; }
    public void setTotalJobs(Integer jobs)   { this.totalJobs = jobs; }
    public void setCreatedAt(String s)       { this.createdAt = s; }
}
