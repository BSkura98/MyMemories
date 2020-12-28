package com.bartlomiejskura.mymemories.model;

import java.time.LocalDateTime;

public class Memory {
    private Long id;
    private String shortDescription;
    private String longDescription;
    private String creationDate;
    private String date;
    private User memoryOwner;

    public Memory(String shortDescription, String longDescription, String creationDate, String date, User memoryOwner) {
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.creationDate = creationDate;
        this.date = date;
        this.memoryOwner = memoryOwner;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getMemoryOwner() {
        return memoryOwner;
    }

    public void setMemoryOwner(User memoryOwner) {
        this.memoryOwner = memoryOwner;
    }
}
