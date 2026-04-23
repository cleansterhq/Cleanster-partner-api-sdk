package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "blacklistEntry")
@XmlAccessorType(XmlAccessType.FIELD)
public class BlacklistEntry {

    @XmlElement private Integer id;
    @XmlElement private Integer userId;
    @XmlElement private String  reason;
    @XmlElement private String  createdAt;

    public BlacklistEntry() {}

    public Integer getId()        { return id; }
    public Integer getUserId()    { return userId; }
    public String  getReason()    { return reason; }
    public String  getCreatedAt() { return createdAt; }

    public void setId(Integer id)          { this.id = id; }
    public void setUserId(Integer userId)  { this.userId = userId; }
    public void setReason(String reason)   { this.reason = reason; }
    public void setCreatedAt(String c)     { this.createdAt = c; }

    @Override
    public String toString() {
        return "BlacklistEntry{id=" + id + ", userId=" + userId + ", reason='" + reason + "'}";
    }
}
