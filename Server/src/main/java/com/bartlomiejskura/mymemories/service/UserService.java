package com.bartlomiejskura.mymemories.service;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.exception.WrongPasswordException;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public User addUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    public List<User> getUsersWithoutFriends(String name, String email) {
        User user = getUser(email);
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

    public List<User> getFriendRequests(String email) throws EntityNotFoundException {
        User user = getUser(email);
        return user.getFriendRequests();
    }

    public List<User> getFriendRequestsByUser(String email) throws EntityNotFoundException {
        User user = getUser(email);
        List<User> friendRequests = new ArrayList<>();

        for(User u:userRepository.findAll()){
            if(u.getFriendRequests().contains(user)){
                friendRequests.add(u);
            }
        }
        return friendRequests;
    }

    public List<User> getFriends(String email) {
        User user = getUser(email);
        return user.getFriends();
    }

    public User editUser(User user){
        if(user.getPassword()==null){
            user.setPassword(userRepository.findById(user.getID()).get().getPassword());
        }
        return userRepository.save(user);
    }

    public User editUserInformation(User user){
        User u = userRepository.findByEmail(user.getEmail());
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        u.setBirthday(user.getBirthday());
        u.setAvatarUrl(user.getAvatarUrl());
        return userRepository.save(u);
    }

    public void deleteUser(String email){
        userRepository.deleteByEmail(email);
    }

    public User changePassword(String email, String oldPassword, String newPassword) throws WrongPasswordException {
        User user = getUser(email);
        if(passwordEncoder.matches(oldPassword, user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword));
        }else {
            throw new WrongPasswordException();
        }
        return userRepository.save(user);
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
