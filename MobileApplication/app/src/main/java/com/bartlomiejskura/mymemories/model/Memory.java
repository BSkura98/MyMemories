package com.bartlomiejskura.mymemories.model;


import java.util.List;

public class Memory {
    private Long id;
    private String title;
    private String description;
    private String modificationDate;
    private String date;
    private int priority;
    private User memoryOwner;
    private List<Category> categories;
    private String imageUrl;
    private List<User> memoryFriends;
    private Boolean isPublicToFriends;
    private Double longitude;
    private Double latitude;

    public Memory(){}

    public Memory(Long id, String title, String description, String modificationDate, String date, User memoryOwner, int priority, List<Category> categories, List<User> memoryFriends, Boolean isPublicToFriends) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.modificationDate = modificationDate;
        this.date = date;
        this.memoryOwner = memoryOwner;
        this.priority = priority;
        this.categories = categories;
        this.memoryFriends = memoryFriends;
        this.isPublicToFriends = isPublicToFriends;
    }

    public Memory(String title, String description, String modificationDate, String date, User memoryOwner, int priority, List<Category> categories, List<User> memoryFriends, Boolean isPublicToFriends) {
        this.title = title;
        this.description = description;
        this.modificationDate = modificationDate;
        this.date = date;
        this.memoryOwner = memoryOwner;
        this.priority = priority;
        this.categories = categories;
        this.memoryFriends = memoryFriends;
        this.isPublicToFriends = isPublicToFriends;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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

    public Boolean getIsPublicToFriends() {
        return isPublicToFriends;
    }

    public void setIsPublicToFriends(Boolean isPublicToFriends) {
        this.isPublicToFriends = isPublicToFriends;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}