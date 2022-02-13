package io.mano;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mano.service.RedisOperationsService;

@SpringBootApplication
public class RedisDbApplication implements CommandLineRunner {
	
	@Autowired
	private RedisOperationsService redisDbService;

	public static void main(String[] args) {
		SpringApplication.run(RedisDbApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		redisDbService.performRedisOperations();
	}

}
