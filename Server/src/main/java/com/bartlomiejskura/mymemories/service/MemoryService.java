package com.bartlomiejskura.mymemories.service;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Tag;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.repository.MemoryRepository;
import com.bartlomiejskura.mymemories.repository.TagRepository;
import com.bartlomiejskura.mymemories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemoryService {
    @Autowired
    private MemoryRepository memoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    public List<Memory> getAll(){
        return memoryRepository.findAll();
    }

    public List<Memory> getAllForUser(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow();
        return this.memoryRepository.findAllByMemoryOwner(user);
    }

    public List<Memory> getAllForTag(Long tagId){
        Tag tag = this.tagRepository.findById(tagId).orElseThrow();
        return this.memoryRepository.findAllByTag(tag);
    }

    public List<Memory> getAllForUserAndTag(Long userId, Long tagId){
        User user = this.userRepository.findById(userId).orElseThrow();
        Tag tag = this.tagRepository.findById(tagId).orElseThrow();
        return this.memoryRepository.findAllByMemoryOwnerAndTag(user, tag);
    }

    public List<Memory> getAllSharedMemoriesForUser(Long userId){
        User user = this.userRepository.findById(userId).orElseThrow();
        return user.getSharedMemories();
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
}
