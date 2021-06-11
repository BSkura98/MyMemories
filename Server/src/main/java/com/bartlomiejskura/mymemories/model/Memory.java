package com.bartlomiejskura.mymemories.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "memories")
public class Memory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    private String title;
    private String description;
    private String imageUrl;
    private LocalDateTime modificationDate;
    private LocalDateTime date;
    private int priority;
    private Boolean isPublicToFriends;
    private Double longitude;
    private Double latitude;
    @ManyToOne
    @JsonIgnoreProperties(value = {"sharedMemories", "friends"}, allowSetters = true)
    @JoinColumn(name = "user_id")
    private User memoryOwner;
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = "memories", allowSetters = true)
    @JoinTable(name="memory_category",
            joinColumns = {@JoinColumn(name="memoryId")},
            inverseJoinColumns = {@JoinColumn(name="categoryId")})
    private List<Category> categories;
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = "sharedMemories", allowSetters = true)
    @JoinTable(name="memory_user",
            joinColumns = {@JoinColumn(name="memoryId")},
            inverseJoinColumns = {@JoinColumn(name="userId")})
    private List<User> memoryFriends;

    public Memory(){}

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String shortDescription) {
        this.title = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String longDescription) {
        this.description = longDescription;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime creationDate) {
        this.modificationDate = creationDate;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int memoryPriority) {
        this.priority = memoryPriority;
    }

    public User getMemoryOwner() {
        return memoryOwner;
    }

    public void setMemoryOwner(User memoryOwner) {
        this.memoryOwner = memoryOwner;
    }

    public List<User> getMemoryFriends() {
        return memoryFriends;
    }

    public void setMemoryFriends(List<User> memoryFriends) {
        this.memoryFriends = memoryFriends;
    }

    public void addMemoryFriend(User friend){
        memoryFriends.add(friend);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsPublicToFriends() {
        return isPublicToFriends;
    }

    public void setIsPublicToFriends(Boolean publicToFriends) {
        this.isPublicToFriends = publicToFriends;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
