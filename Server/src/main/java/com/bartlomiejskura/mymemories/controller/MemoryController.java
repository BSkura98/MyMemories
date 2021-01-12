package com.bartlomiejskura.mymemories.controller;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.service.MemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
