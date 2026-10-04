package com.courier.model;

public class Parcel {

    private String trackingId;
    private String senderName;
    private String receiverName;
    private String source;
    private String destination;
    private String status;

    public Parcel(String trackingId, String senderName, String receiverName,
                  String source, String destination, String status) {

        this.trackingId = trackingId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.source = source;
        this.destination = destination;
        this.status = status;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
