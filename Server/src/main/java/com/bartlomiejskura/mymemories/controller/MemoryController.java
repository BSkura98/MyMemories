package com.bartlomiejskura.mymemories.controller;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.service.MemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("#email.equals(authentication.name)")
    public List<Memory> getAll(@RequestParam(value = "email") String email, @RequestParam(value = "categoryId", required = false) Long categoryId){
        if(categoryId != null){
            return memoryService.getAllForUserAndCategory(email, categoryId);
        }
        return memoryService.getAllForUser(email);
    }

    @GetMapping("/getAllWithShared")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<Memory> getAllWithShared(@RequestParam(value = "email") String email){
        return Stream.concat(memoryService.getAllForUser(email).stream(), memoryService.getAllSharedMemoriesForUser(email).stream())
                .collect(Collectors.toList());
    }

    @GetMapping("/getAllForDate")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<Memory> getAllForDateWithShared(@RequestParam(value = "email") String email, @RequestParam(value = "time") String time){
        LocalDateTime date = LocalDateTime.parse(time);

        return Stream.concat(memoryService.getAllForUserAndDate(email, date).stream(),
                memoryService.getAllSharedMemoriesForUserAndDate(email, date).stream())
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("#memory.memoryOwner.email.equals(authentication.name)")
    public Memory addMemory(@RequestBody Memory memory){
        return memoryService.addMemory(memory);
    }

    @GetMapping
    public Memory getMemory(@RequestParam(name="memoryId")Long memoryId){
        try{
            Memory memory = memoryService.getMemory(memoryId);
            if(!memory.getMemoryOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
                throw new SecurityException();
            }
            return memory;
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping
    @PreAuthorize("#memory.memoryOwner.email.equals(authentication.name)")
    public Memory editMemory(@RequestBody Memory memory){
        return memoryService.editMemory(memory);
    }

    @DeleteMapping
    public void deleteMemory(@RequestParam(name="memoryId")Long memoryId){
        try{
            if(!memoryService.getMemory(memoryId).getMemoryOwner().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
                throw new SecurityException();
            }
            memoryService.deleteMemory(memoryId);
        }catch (EntityNotFoundException ignored){

        }
    }

    @GetMapping("/getShared")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<Memory> getSharedMemories(@RequestParam(value = "email") String email){
        return memoryService.getAllSharedMemoriesForUser(email);
    }

    @GetMapping("/getFriendsPublicMemories")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<Memory> getFriendsPublicMemories(@RequestParam(value = "email") String email){
        return memoryService.getFriendsPublicMemories(email);
    }

    @GetMapping("/search")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<Memory> search(@RequestParam(value = "email") String email,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "hasImage", required = false) Boolean hasImage,
                               @RequestParam(value = "creationDateStart", required = false) String creationDateStart,
                               @RequestParam(value = "creationDateEnd", required = false) String creationDateEnd,
                               @RequestParam(value = "dateStart", required = false) String dateStart,
                               @RequestParam(value = "dateEnd", required = false) String dateEnd,
                               @RequestParam(value = "memoryPriorities", required = false) String memoryPriorities,
                               @RequestParam(value = "publicToFriends", required = false) Boolean publicToFriends,
                               @RequestParam(value = "isSharedMemory", required = false) Boolean isSharedMemory,
                               @RequestParam(value = "categories", required = false) String categories) {
        return memoryService.getMemories(email, keyword, hasImage, creationDateStart==null?null:LocalDateTime.parse(creationDateStart),
                creationDateEnd==null?null:LocalDateTime.parse(creationDateEnd),
                dateStart==null?null:LocalDateTime.parse(dateStart), dateEnd==null?null:LocalDateTime.parse(dateEnd),
                memoryPriorities, publicToFriends, isSharedMemory, categories);
    }
}
