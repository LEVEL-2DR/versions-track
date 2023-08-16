package com.version.track;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.*")
@EnableWebMvc
public class TrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrackApplication.class, args);
	}

	@Bean
	RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

}
