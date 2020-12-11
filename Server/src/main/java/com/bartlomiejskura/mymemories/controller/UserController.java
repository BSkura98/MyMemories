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

    @PutMapping
    public User editUser(@RequestBody User user){
        return userService.editUser(user);
    }

    @DeleteMapping
    public void deleteUser(@RequestParam(name="userId")Long userId){
        userService.deleteUser(userId);
    }

    @PutMapping("/addFriend")
    public User addFriend(@RequestBody User user, @RequestParam(name="friendId")Long friendId){
        try{
            User friend = userService.getUser(friendId);
            user.addFriend(friend);
            return userService.editUser(user);
        }catch (EntityNotFoundException e){
            return null;
        }
    }
}
