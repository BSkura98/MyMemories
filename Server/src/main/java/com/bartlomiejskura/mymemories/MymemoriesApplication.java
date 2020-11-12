package com.bartlomiejskura.mymemories;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.repository.MemoryRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MymemoriesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MymemoriesApplication.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(MemoryRepository memoryRepository){
		return args -> {
			memoryRepository.save(new Memory("First memory"));
			memoryRepository.save(new Memory("Second memory"));
		};
	}
}