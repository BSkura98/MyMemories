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

    private String shortDescription;
    private String longDescription;
    private String imageUrl;
    private LocalDateTime creationDate;
    private LocalDateTime date;
    private int memoryPriority;
    private Boolean publicToFriends;
    @ManyToOne
    @JsonIgnoreProperties(value = "sharedMemories", allowSetters = true)
    @JoinColumn(name = "user_id")
    private User memoryOwner;
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = "memories", allowSetters = true)
    @JoinTable(name="memory_tag",
            joinColumns = {@JoinColumn(name="memoryId")},
            inverseJoinColumns = {@JoinColumn(name="tagId")})
    private List<Tag> tags;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getMemoryPriority() {
        return memoryPriority;
    }

    public void setMemoryPriority(int memoryPriority) {
        this.memoryPriority = memoryPriority;
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

    public Boolean getPublicToFriends() {
        return publicToFriends;
    }

    public void setPublicToFriends(Boolean publicToFriends) {
        this.publicToFriends = publicToFriends;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
