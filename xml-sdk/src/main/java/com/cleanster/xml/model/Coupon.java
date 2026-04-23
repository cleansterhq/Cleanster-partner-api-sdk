package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "coupon")
@XmlAccessorType(XmlAccessType.FIELD)
public class Coupon {

    @XmlElement private Integer id;
    @XmlElement private String  code;
    @XmlElement private String  type;
    @XmlElement private Double  value;
    @XmlElement private Boolean active;
    @XmlElement private String  expiresAt;
    @XmlElement private Integer usageLimit;
    @XmlElement private Integer usageCount;

    public Coupon() {}

    public Integer getId()          { return id; }
    public String  getCode()        { return code; }
    public String  getType()        { return type; }
    public Double  getValue()       { return value; }
    public Boolean getActive()      { return active; }
    public String  getExpiresAt()   { return expiresAt; }
    public Integer getUsageLimit()  { return usageLimit; }
    public Integer getUsageCount()  { return usageCount; }

    public void setId(Integer id)              { this.id = id; }
    public void setCode(String code)           { this.code = code; }
    public void setType(String type)           { this.type = type; }
    public void setValue(Double value)         { this.value = value; }
    public void setActive(Boolean active)      { this.active = active; }
    public void setExpiresAt(String e)         { this.expiresAt = e; }
    public void setUsageLimit(Integer l)       { this.usageLimit = l; }
    public void setUsageCount(Integer c)       { this.usageCount = c; }

    @Override
    public String toString() {
        return "Coupon{code='" + code + "', type='" + type + "', value=" + value + '}';
    }
}
