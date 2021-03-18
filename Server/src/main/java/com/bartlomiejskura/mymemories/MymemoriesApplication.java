package com.bartlomiejskura.mymemories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MymemoriesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MymemoriesApplication.class, args);
	}
}