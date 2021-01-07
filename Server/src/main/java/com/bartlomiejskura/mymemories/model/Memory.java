package com.bartlomiejskura.mymemories.model;

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
    private String imagePath;
    private LocalDateTime creationDate;
    private LocalDateTime date;
    private int memoryPriority;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User memoryOwner;
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
    @ManyToMany(mappedBy = "sharedMemories")
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
