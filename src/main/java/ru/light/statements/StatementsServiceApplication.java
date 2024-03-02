package ru.light.statements;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StatementsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatementsServiceApplication.class, args);
	}

}
