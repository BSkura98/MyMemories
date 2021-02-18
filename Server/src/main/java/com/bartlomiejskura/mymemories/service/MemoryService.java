package com.bartlomiejskura.mymemories.service;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.repository.MemoryRepository;
import com.bartlomiejskura.mymemories.repository.CategoryRepository;
import com.bartlomiejskura.mymemories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoryService {
    @Autowired
    private MemoryRepository memoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Memory> getAll(){
        return memoryRepository.findAll();
    }

    public List<Memory> getAllForUser(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow();
        return this.memoryRepository.findAllByMemoryOwner(user);
    }

    public List<Memory> getNewestForUser(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow();
        LocalDateTime newestDate = this.memoryRepository.findAllByMemoryOwner(user).stream()
                .map(Memory::getDate)
                .filter(date -> date.getYear() <= LocalDateTime.now().getYear() &&
                        date.getDayOfYear() <= LocalDateTime.now().getDayOfYear())
                .max(LocalDateTime::compareTo)
                .get();
        return this.memoryRepository.findAllByMemoryOwner(user).stream()
                .filter(memory -> memory.getDate().getYear()==newestDate.getYear() &&
                        memory.getDate().getDayOfYear()==newestDate.getDayOfYear())
                .collect(Collectors.toList());
    }

    public List<Memory> getAllForCategory(Long categoryId){
        Category category = this.categoryRepository.findById(categoryId).orElseThrow();
        return this.memoryRepository.findAllByCategory(category);
    }

    public List<Memory> getAllForUserAndCategory(Long userId, Long categoryId){
        User user = this.userRepository.findById(userId).orElseThrow();
        Category category = this.categoryRepository.findById(categoryId).orElseThrow();
        return this.memoryRepository.findAllByMemoryOwnerAndCategory(user, category);
    }

    public List<Memory> getAllSharedMemoriesForUser(Long userId){
        User user = this.userRepository.findById(userId).orElseThrow();
        return user.getSharedMemories();
    }

    public List<Memory> getAllForUserAndDate(Long userId, LocalDateTime time){
        User user = this.userRepository.findById(userId).orElseThrow();

        return this.memoryRepository.findAllByMemoryOwner(user).stream()
                .filter(memory -> memory.getDate().getYear()==time.getYear() &&
                        memory.getDate().getDayOfYear()==time.getDayOfYear())
                .collect(Collectors.toList());
    }

    public List<Memory> getAllSharedMemoriesForUserAndDate(Long userId, LocalDateTime time){
        User user = this.userRepository.findById(userId).orElseThrow();

        return user.getSharedMemories().stream()
                .filter(memory -> memory.getDate().getYear()==time.getYear() &&
                        memory.getDate().getDayOfYear()==time.getDayOfYear())
                .collect(Collectors.toList());
    }

    public Memory addMemory(Memory memory){
        List<User> memoryFriends = memory.getMemoryFriends();
        if(memoryFriends!=null){
            for(User user:memoryFriends){
                if(user.getID()==null||user.getEmail()!=null){
                    user.setID(userRepository.findByEmail(user.getEmail()).getID());
                }
            }
            memory.setMemoryFriends(memoryFriends);
        }
        return memoryRepository.save(memory);
    }

    public Memory getMemory(Long memoryId) throws EntityNotFoundException {
        return memoryRepository.findById(memoryId).orElseThrow(EntityNotFoundException::new);
    }

    public Memory editMemory(Memory memory){
        return memoryRepository.save(memory);
    }

    public void deleteMemory(Long memoryId){
        memoryRepository.deleteById(memoryId);
    }

    public List<Memory> search(String keyword, Long userId){
        return memoryRepository.search(keyword).stream()
                .filter(memory -> memory.getMemoryOwner().getID().equals(userId)||
                        memory.getMemoryFriends().stream()
                                .anyMatch(user -> user.getID().equals(userId)))
                .collect(Collectors.toList());
    }

    public List<Memory> getFriendsPublicMemories(Long userId){
        User user = this.userRepository.findById(userId).orElseThrow();
        List<User> friends = user.getFriends();
        List<Memory> publicMemories = new ArrayList<>();


        for(User u:friends){
            for(Memory m:memoryRepository.findAllByMemoryOwner(u)){
                if(m.getPublicToFriends()){
                    publicMemories.add(m);
                }
            }
        }

        return publicMemories;
    }
}
