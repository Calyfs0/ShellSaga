package com.level500.shellsaga.cli;

import com.level500.shellsaga.ShellSagaApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
public class ExitCommand implements GameCommand{

    private static final Logger logger = LoggerFactory.getLogger(ShellSagaApplication.class);
    @Override
    public void execute() {
        logger.info("\uD83D\uDC4B Exiting ShellSaga. Goodbye!");
        System.exit(0);
    }
}
