package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "plan")
@XmlAccessorType(XmlAccessType.FIELD)
public class Plan {

    @XmlElement private Integer id;
    @XmlElement private String  name;
    @XmlElement private String  description;
    @XmlElement private Double  basePrice;
    @XmlElement private Double  hourlyRate;
    @XmlElement private String  currency;
    @XmlElement private Boolean active;

    public Plan() {}

    public Integer getId()          { return id; }
    public String  getName()        { return name; }
    public String  getDescription() { return description; }
    public Double  getBasePrice()   { return basePrice; }
    public Double  getHourlyRate()  { return hourlyRate; }
    public String  getCurrency()    { return currency; }
    public Boolean getActive()      { return active; }

    public void setId(Integer id)               { this.id = id; }
    public void setName(String name)            { this.name = name; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setBasePrice(Double p)          { this.basePrice = p; }
    public void setHourlyRate(Double r)         { this.hourlyRate = r; }
    public void setCurrency(String currency)    { this.currency = currency; }
    public void setActive(Boolean active)       { this.active = active; }

    @Override
    public String toString() {
        return "Plan{id=" + id + ", name='" + name + "', basePrice=" + basePrice + '}';
    }
}
