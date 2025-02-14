package com.level500.shellsaga;

import com.level500.shellsaga.cli.GameLoop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShellSagaApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ShellSagaApplication.class, args);
		System.out.println("\uD83C\uDFAE Welcome to ShellSaga!");

		GameLoop gameLoop = new GameLoop();
		gameLoop.start();
	}

}
