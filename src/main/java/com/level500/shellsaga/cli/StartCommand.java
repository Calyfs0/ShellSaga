package com.level500.shellsaga.cli;

import com.level500.shellsaga.ShellSagaApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//✔ Executes when the player types start.
@Component
public class StartCommand implements GameCommand{

    private static final Logger logger = LoggerFactory.getLogger(ShellSagaApplication.class);

    @Override
    public void execute() {
        logger.info("\uD83C\uDFAE The game has started! Type 'help to see available commands.");
        // Here we can initialize player creation and game mechanics
    }
}
