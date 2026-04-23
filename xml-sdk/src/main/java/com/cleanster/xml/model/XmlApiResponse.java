package com.cleanster.xml.model;

import jakarta.xml.bind.annotation.*;

/**
 * Generic XML envelope for every Cleanster API response.
 * <pre>{@code
 * <response>
 *   <success>true</success>
 *   <message>Booking created</message>
 *   <data>...</data>
 * </response>
 * }</pre>
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlApiResponse<T> {

    @XmlElement private boolean success;
    @XmlElement private String  message;
    @XmlElement private T       data;

    public XmlApiResponse() {}

    public XmlApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data    = data;
    }

    public boolean isSuccess() { return success; }
    public String  getMessage() { return message; }
    public T       getData()    { return data; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message)  { this.message = message; }
    public void setData(T data)             { this.data = data; }

    @Override
    public String toString() {
        return "XmlApiResponse{success=" + success + ", message='" + message + "', data=" + data + '}';
    }
}
