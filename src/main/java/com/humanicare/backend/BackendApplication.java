package com.humanicare.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling //@Scheduled - 주기적인 스케쥴링을 위해서 필요
public class BackendApplication {
	public static void main(String[] args) {SpringApplication.run(BackendApplication.class, args);}
}

