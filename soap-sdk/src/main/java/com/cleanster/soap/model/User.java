package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a Cleanster platform user. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private Long   id;
    private String name;
    private String email;
    private String phone;
    @JsonProperty("access_token") private String accessToken;
    @JsonProperty("created_at")   private String createdAt;

    public Long   getId()          { return id; }
    public String getName()        { return name; }
    public String getEmail()       { return email; }
    public String getPhone()       { return phone; }
    public String getAccessToken() { return accessToken; }
    public String getCreatedAt()   { return createdAt; }

    public void setId(Long id)               { this.id = id; }
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPhone(String phone)       { this.phone = phone; }
    public void setAccessToken(String t)     { this.accessToken = t; }
    public void setCreatedAt(String s)       { this.createdAt = s; }
}
