package com.bartlomiejskura.mymemories.service;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
