package com.bartlomiejskura.mymemories.controller;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.exception.WrongPasswordException;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public User addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @GetMapping
    @PreAuthorize("#email.equals(authentication.name)")
    public User getUser(@RequestParam(name="email")String email){
        return userService.getUser(email);
    }

    @GetMapping("/getByName")
    public List<User> getUsersByName(@RequestParam(name="name")String name){
        return userService.getUsersByName(name);
    }

    @GetMapping("/getWithoutFriends")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<User> getUsersWithoutFriends(@RequestParam(name="email")String email, @RequestParam(name="name")String name){
        return userService.getUsersWithoutFriends(name, email);
    }

    @GetMapping("/getFriendRequests")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<User> getFriendRequests(@RequestParam(name="email")String email){
        try {
            return userService.getFriendRequests(email);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @GetMapping("/getFriendRequestsByUser")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<User> getFriendRequestsByUser(@RequestParam(name="email")String email){
        try {
            return userService.getFriendRequestsByUser(email);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @GetMapping("/getFriends")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<User> getFriends(@RequestParam(name="email")String email){
        try {
            return userService.getFriends(email);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @PutMapping
    @PreAuthorize("#user.email.equals(authentication.name)")
    public User editUser(@RequestBody User user){
        return userService.editUser(user);
    }

    @DeleteMapping
    @PreAuthorize("#email.equals(authentication.name)")
    public void deleteUser(@RequestParam(name="email")String email){
        userService.deleteUser(email);
    }

    @PutMapping("/sendFriendRequest")
    @PreAuthorize("#user1Email.equals(authentication.name)")
    public User sendFriendRequest(@RequestParam(name="user1Email")String user1Email, @RequestParam(name="user2Id")Long user2Id){
        try{
            User user1 = userService.getUser(user1Email);
            User user2 = userService.getUser(user2Id);
            if(user2.getFriendRequests().contains(user1)){
                return user1;
            }
            user2.addFriendRequest(user1);
            return userService.editUser(user1);
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping("/removeFriendRequest")
    @PreAuthorize("#user1Email.equals(authentication.name)")
    public User removeFriendRequest(@RequestParam(name="user1Email")String user1Email, @RequestParam(name="user2Id")Long user2Id){
        try{
            User user1 = userService.getUser(user1Email);
            User user2 = userService.getUser(user2Id);
            user1.removeFriendRequest(user2);
            return userService.editUser(user1);
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping("/acceptFriendRequest")
    @PreAuthorize("#user1Email.equals(authentication.name)")
    public User acceptFriendRequest(@RequestParam(name="user1Email")String user1Email, @RequestParam(name="user2Id")Long user2Id){
        try{
            User user1 = userService.getUser(user1Email);
            User user2 = userService.getUser(user2Id);
            if(user1.getFriendRequests().contains(user2)){
                user1.removeFriendRequest(user2);
                user1.addFriend(user2);
                user2.addFriend(user1);
                return userService.editUser(user1);
            }else{
                return null;
            }
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping("/removeFriend")
    @PreAuthorize("#user1Email.equals(authentication.name)")
    public User removeFriend(@RequestParam(name="user1Email")String user1Email, @RequestParam(name="user2Id")Long user2Id){
        try{
            User user1 = userService.getUser(user1Email);
            User user2 = userService.getUser(user2Id);
            user1.removeFriend(user2);
            user2.removeFriend(user1);
            return userService.editUser(user1);
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping("/changePassword")
    @PreAuthorize("#email.equals(authentication.name)")
    public User changePassword(@RequestParam(name="email") String email, @RequestParam(name="oldPassword") String oldPassword, @RequestParam(name="newPassword") String newPassword){
        try {
            return userService.changePassword(email, oldPassword, newPassword);
        } catch (EntityNotFoundException | WrongPasswordException e) {
            return null;
        }
    }
}
