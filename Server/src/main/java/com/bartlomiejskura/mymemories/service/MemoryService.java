package com.bartlomiejskura.mymemories.service;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.repository.MemoryRepository;
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

    public List<Memory> getAll(){
        return memoryRepository.findAll();
    }

    public List<Memory> getAllForUser(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow();
        return this.memoryRepository.findAllByMemoryOwner(user);
    }

    public Memory addMemory(Memory memory){
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
