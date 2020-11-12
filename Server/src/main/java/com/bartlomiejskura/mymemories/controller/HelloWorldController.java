package com.bartlomiejskura.mymemories.controller;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.repository.MemoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    private final MemoryRepository memoryRepository;

    public HelloWorldController(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    @GetMapping("/")
    String hello() {
        return "Hello World";
    }

    @GetMapping("/memories")
    Iterable<Memory> memories(){
        return memoryRepository.findAll();
    }
}
