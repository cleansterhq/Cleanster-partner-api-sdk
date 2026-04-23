package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Request object for the CreateUser SOAP operation. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateUserRequest {

    private String name;
    private String email;
    private String phone;
    private String password;
    @JsonProperty("property_id") private Long propertyId;

    public String getName()       { return name; }
    public String getEmail()      { return email; }
    public String getPhone()      { return phone; }
    public String getPassword()   { return password; }
    public Long   getPropertyId() { return propertyId; }

    public CreateUserRequest setName(String name)         { this.name = name; return this; }
    public CreateUserRequest setEmail(String email)       { this.email = email; return this; }
    public CreateUserRequest setPhone(String phone)       { this.phone = phone; return this; }
    public CreateUserRequest setPassword(String pw)       { this.password = pw; return this; }
    public CreateUserRequest setPropertyId(Long id)       { this.propertyId = id; return this; }
}
