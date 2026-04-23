package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "paymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMethod {

    @XmlElement private Integer id;
    @XmlElement private String  type;
    @XmlElement private String  brand;
    @XmlElement private String  last4;
    @XmlElement private Integer expMonth;
    @XmlElement private Integer expYear;
    @XmlElement private Boolean isDefault;
    @XmlElement private String  holderName;
    @XmlElement private String  createdAt;

    public PaymentMethod() {}

    public Integer getId()         { return id; }
    public String  getType()       { return type; }
    public String  getBrand()      { return brand; }
    public String  getLast4()      { return last4; }
    public Integer getExpMonth()   { return expMonth; }
    public Integer getExpYear()    { return expYear; }
    public Boolean getIsDefault()  { return isDefault; }
    public String  getHolderName() { return holderName; }
    public String  getCreatedAt()  { return createdAt; }

    public void setId(Integer id)           { this.id = id; }
    public void setType(String type)        { this.type = type; }
    public void setBrand(String brand)      { this.brand = brand; }
    public void setLast4(String last4)      { this.last4 = last4; }
    public void setExpMonth(Integer m)      { this.expMonth = m; }
    public void setExpYear(Integer y)       { this.expYear = y; }
    public void setIsDefault(Boolean d)     { this.isDefault = d; }
    public void setHolderName(String n)     { this.holderName = n; }
    public void setCreatedAt(String c)      { this.createdAt = c; }

    @Override
    public String toString() {
        return "PaymentMethod{id=" + id + ", brand='" + brand + "', last4='" + last4 + "'}";
    }
}
