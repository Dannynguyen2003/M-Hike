package com.example.m_hike.models;

public class Observation {
    private long id;
    private long hikeId;
    private String obsText;
    private String timestamp; // ISO datetime string
    private String comments;

    public Observation() {}

    public Observation(long id, long hikeId, String obsText, String timestamp, String comments) {
        this.id = id;
        this.hikeId = hikeId;
        this.obsText = obsText;
        this.timestamp = timestamp;
        this.comments = comments;
    }

    // getters & setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getHikeId() { return hikeId; }
    public void setHikeId(long hikeId) { this.hikeId = hikeId; }
    public String getObsText() { return obsText; }
    public void setObsText(String obsText) { this.obsText = obsText; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
