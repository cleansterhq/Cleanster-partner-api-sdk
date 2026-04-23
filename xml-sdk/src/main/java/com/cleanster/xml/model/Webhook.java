package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "webhook")
@XmlAccessorType(XmlAccessType.FIELD)
public class Webhook {

    @XmlElement private Integer     id;
    @XmlElement private String      url;
    @XmlElement private Boolean     active;
    @XmlElementWrapper(name = "events")
    @XmlElement(name   = "event")
    private List<String> events;
    @XmlElement private String      secret;
    @XmlElement private String      createdAt;
    @XmlElement private String      updatedAt;

    public Webhook() {}

    public Integer      getId()        { return id; }
    public String       getUrl()       { return url; }
    public Boolean      getActive()    { return active; }
    public List<String> getEvents()    { return events; }
    public String       getSecret()    { return secret; }
    public String       getCreatedAt() { return createdAt; }
    public String       getUpdatedAt() { return updatedAt; }

    public void setId(Integer id)              { this.id = id; }
    public void setUrl(String url)             { this.url = url; }
    public void setActive(Boolean active)      { this.active = active; }
    public void setEvents(List<String> events) { this.events = events; }
    public void setSecret(String secret)       { this.secret = secret; }
    public void setCreatedAt(String c)         { this.createdAt = c; }
    public void setUpdatedAt(String u)         { this.updatedAt = u; }

    @Override
    public String toString() {
        return "Webhook{id=" + id + ", url='" + url + "', active=" + active + '}';
    }
}
