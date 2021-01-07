package com.bartlomiejskura.mymemories.model;


public class Memory {
    private Long id;
    private String shortDescription;
    private String longDescription;
    private String creationDate;
    private String date;
    private int memoryPriority;
    private User memoryOwner;
    private Tag tag;

    public Memory(Long id, String shortDescription, String longDescription, String creationDate, String date, User memoryOwner, int memoryPriority, Tag tag) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.creationDate = creationDate;
        this.date = date;
        this.memoryOwner = memoryOwner;
        this.memoryPriority = memoryPriority;
        this.tag = tag;
    }

    public Memory(String shortDescription, String longDescription, String creationDate, String date, User memoryOwner, int memoryPriority, Tag tag) {
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.creationDate = creationDate;
        this.date = date;
        this.memoryOwner = memoryOwner;
        this.memoryPriority = memoryPriority;
        this.tag = tag;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMemoryPriority() {
        return memoryPriority;
    }

    public void setMemoryPriority(int memoryPriority) {
        this.memoryPriority = memoryPriority;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}