package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "createUserResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateUserResponse {

    @XmlElement private Integer userId;
    @XmlElement private String  accessToken;

    public CreateUserResponse() {}

    public Integer getUserId()      { return userId; }
    public String  getAccessToken() { return accessToken; }

    public void setUserId(Integer userId)         { this.userId = userId; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    @Override
    public String toString() {
        return "CreateUserResponse{userId=" + userId + ", accessToken='" + accessToken + "'}";
    }
}
