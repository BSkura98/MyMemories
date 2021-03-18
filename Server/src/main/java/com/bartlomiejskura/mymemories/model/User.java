package com.bartlomiejskura.mymemories.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long ID;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private LocalDateTime birthday;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="memory_user",
            joinColumns = {@JoinColumn(name="userId")},
            inverseJoinColumns = {@JoinColumn(name="memoryId")})
    private List<Memory> sharedMemories;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"sharedMemories", "friends", "friendRequests"})
    @JoinTable(name="friends",
            joinColumns = {@JoinColumn(name="userId")},
            inverseJoinColumns = {@JoinColumn(name="friendId")})
    private List<User> friends;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"sharedMemories", "friends", "friendRequests"})
    @JoinTable(name="friend_requests",
            joinColumns = {@JoinColumn(name="userId")},
            inverseJoinColumns = {@JoinColumn(name="friendId")})
    private List<User> friendRequests;

    public User(){}

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String secondName) {
        this.lastName = secondName;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public List<Memory> getSharedMemories() {
        return sharedMemories;
    }

    public void setSharedMemories(List<Memory> sharedMemories) {
        this.sharedMemories = sharedMemories;
    }

    public void addSharedMemory(Memory memory){
        sharedMemories.add(memory);
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public void addFriend(User friend){
        this.friends.add(friend);
    }

    public void removeFriend(User friend){
        this.friends.remove(friend);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<User> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<User> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public void addFriendRequest(User user){
        friendRequests.add(user);
    }

    public void removeFriendRequest(User user){
        friendRequests.remove(user);
    }
}
