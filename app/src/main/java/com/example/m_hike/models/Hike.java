package com.example.m_hike.models;

public class Hike {
    private long id;
    private String name;
    private String location;
    private String date;
    private boolean parkingAvailable;
    private String length;
    private String difficulty;
    private String description;
    private String extra1;
    private String extra2;
    private String imagePath;
    public Hike() {}

    public Hike(long id, String name, String location, String date, boolean parkingAvailable, String length, String difficulty, String description, String extra1, String extra2, String imagePath) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.parkingAvailable = parkingAvailable;
        this.length = length;
        this.difficulty = difficulty;
        this.description = description;
        this.extra1 = extra1;
        this.extra2 = extra2;
        this.imagePath = imagePath;
    }

    public Hike(String name, String location, String date, boolean parkingAvailable, String length, String difficulty, String description, String extra1, String extra2, String imagePath) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.parkingAvailable = parkingAvailable;
        this.length = length;
        this.difficulty = difficulty;
        this.description = description;
        this.extra1 = extra1;
        this.extra2 = extra2;
        this.imagePath = imagePath;
    }

    // getters & setters
    // ... (include for all fields)
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public boolean isParkingAvailable() { return parkingAvailable; }
    public void setParkingAvailable(boolean parkingAvailable) { this.parkingAvailable = parkingAvailable; }
    public String getLength() { return length; }
    public void setLength(String length) { this.length = length; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getExtra1() { return extra1; }
    public void setExtra1(String extra1) { this.extra1 = extra1; }
    public String getExtra2() { return extra2; }
    public void setExtra2(String extra2) { this.extra2 = extra2; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
