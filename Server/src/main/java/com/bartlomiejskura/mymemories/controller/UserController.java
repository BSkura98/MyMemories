package com.bartlomiejskura.mymemories.controller;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public List<User> getAll(){
        return userService.getAll();
    }

    @PostMapping
    public User addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @GetMapping
    public User getUser(@RequestParam(name="userId")Long userId){
        try{
            return userService.getUser(userId);
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @GetMapping("/getByEmail")
    public User getUser(@RequestParam(name="email")String email){
        return userService.getUser(email);
    }

    @GetMapping("/getByName")
    public List<User> getUsersByName(@RequestParam(name="name")String name){
        return userService.getUsersByName(name);
    }

    @GetMapping("/getWithoutFriends")
    public List<User> getUsersWithoutFriends(@RequestParam(name="name")String name, @RequestParam(name="userId")Long userId){
        try {
            return userService.getUsersWithoutFriends(name, userId);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @GetMapping("/getFriendRequests")
    public List<User> getFriendRequests(@RequestParam(name="userId")Long userId){
        try {
            return userService.getFriendRequests(userId);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @GetMapping("/getFriendRequestsByUser")
    public List<User> getFriendRequestsByUser(@RequestParam(name="userId")Long userId){
        try {
            return userService.getFriendRequestsByUser(userId);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @GetMapping("/getFriends")
    public List<User> getFriends(@RequestParam(name="userId")Long userId){
        try {
            return userService.getFriends(userId);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @PutMapping
    public User editUser(@RequestBody User user){
        return userService.editUser(user);
    }

    @DeleteMapping
    public void deleteUser(@RequestParam(name="userId")Long userId){
        userService.deleteUser(userId);
    }

    @PutMapping("/sendFriendRequest")
    public User sendFriendRequest(@RequestParam(name="user1Id")Long user1Id, @RequestParam(name="user2Id")Long user2Id){
        try{
            User user1 = userService.getUser(user1Id);
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
    public User removeFriendRequest(@RequestParam(name="user1Id")Long user1Id, @RequestParam(name="user2Id")Long user2Id){
        try{
            User user1 = userService.getUser(user1Id);
            User user2 = userService.getUser(user2Id);
            user1.removeFriendRequest(user2);
            return userService.editUser(user1);
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping("/acceptFriendRequest")
    public User acceptFriendRequest(@RequestParam(name="user1Id")Long user1Id, @RequestParam(name="user2Id")Long user2Id){
        try{
            User user1 = userService.getUser(user1Id);
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
    public User removeFriend(@RequestParam(name="user1Id")Long user1Id, @RequestParam(name="user2Id")Long user2Id){
        try{
            User user1 = userService.getUser(user1Id);
            User user2 = userService.getUser(user2Id);
            user1.removeFriend(user2);
            user2.removeFriend(user1);
            return userService.editUser(user1);
        }catch (EntityNotFoundException e){
            return null;
        }
    }
}
