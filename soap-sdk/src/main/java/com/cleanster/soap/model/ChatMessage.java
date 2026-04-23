package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a single chat message on a Cleanster booking. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage {

    private Long   id;
    @JsonProperty("booking_id") private Long   bookingId;
    private String message;
    private String sender;
    @JsonProperty("sent_at")    private String sentAt;

    public Long   getId()        { return id; }
    public Long   getBookingId() { return bookingId; }
    public String getMessage()   { return message; }
    public String getSender()    { return sender; }
    public String getSentAt()    { return sentAt; }

    public void setId(Long id)             { this.id = id; }
    public void setBookingId(Long id)      { this.bookingId = id; }
    public void setMessage(String message) { this.message = message; }
    public void setSender(String sender)   { this.sender = sender; }
    public void setSentAt(String sentAt)   { this.sentAt = sentAt; }
}
