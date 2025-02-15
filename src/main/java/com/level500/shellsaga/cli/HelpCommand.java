package com.level500.shellsaga.cli;

import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.services.PlayerSessionService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class HelpCommand implements GameCommand{

    private static final Logger logger = LoggerFactory.getLogger(ShellSagaApplication.class);
    private final ApplicationContext applicationContext;
    private final PlayerSessionService playerSessionService;

    public HelpCommand(ApplicationContext applicationContext, PlayerSessionService playerSessionService) {
        this.applicationContext = applicationContext;
        this.playerSessionService = playerSessionService;
    }

    @Override
    public void execute() {
        Optional<Player> player = playerSessionService.getPlayerSession();
        if(player.isEmpty()){
            logger.error("❌ No active player session found! Please restart the game.");
            return;
        }
        logger.info("\n📜 **Available Commands:**");
        // ✅ Check if the game has started
        boolean gameStarted = playerSessionService.getPlayerSession().isPresent();

        if (!gameStarted) {
            // ✅ Before the game starts, show only start and exit
            logger.info("🔹 start  - Begin your adventure!");
            logger.info("🔹 exit   - Quit the game.");
        }else{
            // ✅ Dynamically fetch GameLoop and list registered commands
            GameLoop gameLoop = applicationContext.getBean(GameLoop.class);
            Set<String> commands = gameLoop.getRegisteredCommands();

            for (String command : commands) {
                if(command.equals("start") || command.equals("exit")) continue;
                logger.info("🔹 {}", command);

            }
        }


        logger.info("\n🔹 Type a command to perform an action.");


    }
}
