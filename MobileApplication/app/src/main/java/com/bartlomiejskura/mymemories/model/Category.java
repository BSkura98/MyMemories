package com.bartlomiejskura.mymemories.model;

import java.util.List;

public class Category {
    private Long id;
    private String name;
    private User user;
    private List<Memory> memories;

    public Category(Long id, String name, User user, List<Memory> memories) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.memories = memories;
    }

    public Category(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public Category(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Memory> getMemories() {
        return memories;
    }

    public void setMemories(List<Memory> memories) {
        this.memories = memories;
    }
}