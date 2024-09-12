package com.task05;

public class Response {

    private int statusCode;
    private DynamoDbItem event;

    public Response(int statusCode, DynamoDbItem event) {
        this.statusCode = statusCode;
        this.event = event;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public DynamoDbItem getEvent() {
        return event;
    }

    public void setEvent(DynamoDbItem event) {
        this.event = event;
    }
}
