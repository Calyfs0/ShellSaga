package com.level500.shellsaga;

import com.level500.shellsaga.cli.GameLoop;
import com.level500.shellsaga.services.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ShellSagaApplication {

	@Autowired
	private AreaService areaService;
	private static final Logger logger = LoggerFactory.getLogger(ShellSagaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ShellSagaApplication.class, args);
		logger.info("🎮 Welcome to ShellSaga!");
	}

	@Bean
	CommandLineRunner runGameLoop(ApplicationContext context) {
		return args -> {
			areaService.initializeAreas(); // Ensures areas are checked on startup
			GameLoop gameLoop = context.getBean(GameLoop.class);
			gameLoop.start();
		};
	}
}
