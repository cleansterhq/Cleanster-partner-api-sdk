package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for submitting feedback on a completed booking.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackRequest {

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("comment")
    private String comment;

    public FeedbackRequest() {}

    public FeedbackRequest(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
