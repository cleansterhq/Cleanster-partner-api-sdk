package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

    @XmlElement private Integer id;
    @XmlElement private String  email;
    @XmlElement private String  firstName;
    @XmlElement private String  lastName;
    @XmlElement private String  phone;
    @XmlElement private String  token;
    @XmlElement private String  role;
    @XmlElement private Boolean active;
    @XmlElement private String  createdAt;
    @XmlElement private String  updatedAt;

    public User() {}

    public Integer getId()        { return id; }
    public String  getEmail()     { return email; }
    public String  getFirstName() { return firstName; }
    public String  getLastName()  { return lastName; }
    public String  getPhone()     { return phone; }
    public String  getToken()     { return token; }
    public String  getRole()      { return role; }
    public Boolean getActive()    { return active; }
    public String  getCreatedAt() { return createdAt; }
    public String  getUpdatedAt() { return updatedAt; }

    public void setId(Integer id)           { this.id = id; }
    public void setEmail(String email)      { this.email = email; }
    public void setFirstName(String fn)     { this.firstName = fn; }
    public void setLastName(String ln)      { this.lastName = ln; }
    public void setPhone(String phone)      { this.phone = phone; }
    public void setToken(String token)      { this.token = token; }
    public void setRole(String role)        { this.role = role; }
    public void setActive(Boolean active)   { this.active = active; }
    public void setCreatedAt(String c)      { this.createdAt = c; }
    public void setUpdatedAt(String u)      { this.updatedAt = u; }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', name='" + firstName + " " + lastName + "'}";
    }
}
