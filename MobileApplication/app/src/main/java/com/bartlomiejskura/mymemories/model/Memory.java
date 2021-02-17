package com.bartlomiejskura.mymemories.model;


import java.util.List;

public class Memory {
    private Long id;
    private String shortDescription;
    private String longDescription;
    private String creationDate;
    private String date;
    private int memoryPriority;
    private User memoryOwner;
    private List<Tag> tags;
    private String imageUrl;
    private List<User> memoryFriends;
    private Boolean publicToFriends;

    public Memory(){}

    public Memory(Long id, String shortDescription, String longDescription, String creationDate, String date, User memoryOwner, int memoryPriority, List<Tag> tags, List<User> memoryFriends, Boolean publicToFriends) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.creationDate = creationDate;
        this.date = date;
        this.memoryOwner = memoryOwner;
        this.memoryPriority = memoryPriority;
        this.tags = tags;
        this.memoryFriends = memoryFriends;
        this.publicToFriends = publicToFriends;
    }

    public Memory(String shortDescription, String longDescription, String creationDate, String date, User memoryOwner, int memoryPriority, List<Tag> tags, List<User> memoryFriends, Boolean publicToFriends) {
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.creationDate = creationDate;
        this.date = date;
        this.memoryOwner = memoryOwner;
        this.memoryPriority = memoryPriority;
        this.tags = tags;
        this.memoryFriends = memoryFriends;
        this.publicToFriends = publicToFriends;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<User> getMemoryFriends() {
        return memoryFriends;
    }

    public void setMemoryFriends(List<User> memoryFriends) {
        this.memoryFriends = memoryFriends;
    }

    public void removeMemoryFriend(Long userId){
        for(int i=0;i<memoryFriends.size();i++){
            if(memoryFriends.get(i).getId().equals(userId)){
                memoryFriends.remove(i);
                break;
            }
        }
    }

    public Boolean getPublicToFriends() {
        return publicToFriends;
    }

    public void setPublicToFriends(Boolean publicToFriends) {
        this.publicToFriends = publicToFriends;
    }
}