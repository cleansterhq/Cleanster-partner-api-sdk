package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Represents a single chat message on a Cleanster booking. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage {

    @JsonProperty("message_id") private String messageId;
    @JsonProperty("sender_id") private String senderId;
    private String content;
    private String timestamp;
    @JsonProperty("message_type") private String messageType;
    private List<Attachment> attachments;
    @JsonProperty("is_read") private Boolean isRead;
    @JsonProperty("sender_type") private String senderType;

    // Legacy response fields retained for compatibility.
    private Long   id;
    @JsonProperty("booking_id") private Long   bookingId;
    private String message;
    private String sender;
    @JsonProperty("sent_at")    private String sentAt;

    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public String getMessageType() { return messageType; }
    public List<Attachment> getAttachments() { return attachments; }
    public Boolean getIsRead() { return isRead; }
    public String getSenderType() { return senderType; }
    public Long   getId()        { return id; }
    public Long   getBookingId() { return bookingId; }
    public String getMessage()   { return message; }
    public String getSender()    { return sender; }
    public String getSentAt()    { return sentAt; }

    public void setMessageId(String value) { this.messageId = value; }
    public void setSenderId(String value) { this.senderId = value; }
    public void setContent(String value) { this.content = value; }
    public void setTimestamp(String value) { this.timestamp = value; }
    public void setMessageType(String value) { this.messageType = value; }
    public void setAttachments(List<Attachment> value) { this.attachments = value; }
    public void setIsRead(Boolean value) { this.isRead = value; }
    public void setSenderType(String value) { this.senderType = value; }
    public void setId(Long id)             { this.id = id; }
    public void setBookingId(Long id)      { this.bookingId = id; }
    public void setMessage(String message) { this.message = message; }
    public void setSender(String sender)   { this.sender = sender; }
    public void setSentAt(String sentAt)   { this.sentAt = sentAt; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attachment {
        private String type;
        private String url;
        @JsonProperty("thumb_url") private String thumbUrl;

        public String getType() { return type; }
        public String getUrl() { return url; }
        public String getThumbUrl() { return thumbUrl; }
        public void setType(String value) { this.type = value; }
        public void setUrl(String value) { this.url = value; }
        public void setThumbUrl(String value) { this.thumbUrl = value; }
    }
}
