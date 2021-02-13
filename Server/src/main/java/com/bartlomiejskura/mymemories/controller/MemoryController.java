package com.bartlomiejskura.mymemories.controller;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.service.MemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/memory")
public class MemoryController {
    @Autowired
    private MemoryService memoryService;

    @GetMapping("/getAll")
    public List<Memory> getAll(@RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "tagId", required = false) Long tagId){
        if (userId != null) {
            if(tagId != null){
                return memoryService.getAllForUserAndTag(userId, tagId);
            }
            return memoryService.getAllForUser(userId);
        }
        if(tagId!=null){
            return memoryService.getAllForTag(tagId);
        }
        return memoryService.getAll();
    }

    @GetMapping("/getAllWithShared")
    public List<Memory> getAllWithShared(@RequestParam(value = "userId") Long userId){
        return Stream.concat(memoryService.getAllForUser(userId).stream(), memoryService.getAllSharedMemoriesForUser(userId).stream())
                .collect(Collectors.toList());
    }

    @GetMapping("/getAllForDate")
    public List<Memory> getAllForDateWithShared(@RequestParam(value = "userId") Long userId, @RequestParam(value = "time") String time){
        LocalDateTime date = LocalDateTime.parse(time);

        return Stream.concat(memoryService.getAllForUserAndDate(userId, date).stream(),
                memoryService.getAllSharedMemoriesForUserAndDate(userId, date).stream())
                .collect(Collectors.toList());
    }

    @PostMapping
    public Memory addMemory(@RequestBody Memory memory){
        return memoryService.addMemory(memory);
    }

    @GetMapping
    public Memory getMemory(@RequestParam(name="memoryId")Long memoryId){
        try{
            return memoryService.getMemory(memoryId);
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping
    public Memory editMemory(@RequestBody Memory memory){
        return memoryService.editMemory(memory);
    }

    @DeleteMapping
    public void deleteMemory(@RequestParam(name="memoryId")Long memoryId){
        memoryService.deleteMemory(memoryId);
    }

    @GetMapping("/getShared")
    public List<Memory> getSharedMemories(@RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "tagId", required = false) Long tagId){
        if (userId != null) {
            return memoryService.getAllSharedMemoriesForUser(userId);
        }
        return null;
    }

    @GetMapping("/getFriendsPublicMemories")
    public List<Memory> getFriendsPublicMemories(@RequestParam(value = "userId", required = false) Long userId){
        if (userId != null) {
            return memoryService.getFriendsPublicMemories(userId);
        }
        return null;
    }

    @GetMapping("/search")
    public List<Memory> search(@RequestParam(value = "keyword") String keyword, @RequestParam(value = "userId", required = false) Long userId){
        return memoryService.search(keyword, userId);
    }
}
