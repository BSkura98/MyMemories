package com.bartlomiejskura.mymemories.service;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public User addUser(User user){
        return userRepository.save(user);
    }

    public User getUser(Long userId) throws EntityNotFoundException {
        return userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByName(String name){
        return userRepository.findAll().stream()
                .filter(user -> user.getFirstName().toLowerCase().concat("_").concat(user.getLastName().toLowerCase()).contains(name.toLowerCase())
                        ||user.getLastName().toLowerCase().concat("_").concat(user.getFirstName().toLowerCase()).contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<User> getUsersWithoutFriends(String name, Long userId) throws EntityNotFoundException {
        User user = getUser(userId);
        List<User> usersByName = getUsersByName(name);
        List<User> friends = Stream.concat(user.getFriends().stream(), user.getFriendRequests().stream())
                .collect(Collectors.toList());
        List<User> usersWithoutFriends = new ArrayList<>();
        usersByName.remove(user);

        for(User u:usersByName){
            if(!friends.contains(u)&&!u.getFriendRequests().contains(user)){
                usersWithoutFriends.add(u);
            }
        }

        return usersWithoutFriends;
    }

    public List<User> getFriendRequests(Long userId) throws EntityNotFoundException {
        User user = getUser(userId);
        return user.getFriendRequests();
    }

    public List<User> getFriendRequestsByUser(Long userId) throws EntityNotFoundException {
        User user = getUser(userId);
        List<User> friendRequests = new ArrayList<>();

        for(User u:userRepository.findAll()){
            if(u.getFriendRequests().contains(user)){
                friendRequests.add(u);
            }
        }
        return friendRequests;
    }

    public List<User> getFriends(Long userId) throws EntityNotFoundException {
        User user = getUser(userId);
        return user.getFriends();
    }

    public User editUser(User user){
        if(user.getPassword()==null){
            user.setPassword(userRepository.findByEmail(user.getEmail()).getPassword());
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

    public User addFriend(User user, User friend){
        user.addFriend(friend);
        return userRepository.save(user);
    }

    public User removeFriend(User user, User friend){
        user.removeFriend(friend);
        return userRepository.save(user);
    }
}
