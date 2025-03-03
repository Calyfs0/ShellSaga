package com.level500.shellsaga.cli;

import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.services.PlayerService;
import com.level500.shellsaga.services.PlayerSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.*;

@Component
public class GameLoop {
    private final Map<String, GameCommand> commandMap = new HashMap<>();
    private final LineReader reader;
    private final PlayerService playerService;
    private final PlayerSessionService playerSessionService;
    private Player currentPlayer; //Stores the logged-in player session

    private static final Logger logger = LoggerFactory.getLogger(ShellSagaApplication.class);

    @Autowired
    public GameLoop(List<GameCommand> commands, PlayerService playerService, PlayerSessionService playerSessionService) throws Exception {
        this.playerService = playerService;
        this.playerSessionService = playerSessionService;
        // ✅ Initialize terminal for input reading
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();

        // ✅ Register all available commands dynamically
        registerCommands(commands);
    }

    private void registerCommands(List<GameCommand> commands) {
        logger.info("\uD83D\uDCCC Registering available commands...");
        for (GameCommand command : commands) {
            String commandName = command.getClass().getSimpleName().replace("Command", "").toLowerCase();
            commandMap.put(commandName, command);
            logger.info("✅ Registered command: " + commandName);
        }
    }
    public Set<String> getRegisteredCommands() {
        return commandMap.keySet();
    }
    public void start() {
        logger.info("\n🎮 Welcome to ShellSaga!");
        logger.info("🔹 Type 'start' to begin, or 'help' for available commands.");

        //Check Redis for an active player session
        if(currentPlayer == null){
            currentPlayer = getOrCreatePlayerSession();
        }

        while (true) {
            String input = reader.readLine("\n> ").trim().toLowerCase();
            if (input.contains("\n") || input.contains("\r")) {
                logger.info("❌ Invalid command. Please try again.");
                continue;
            }
            if (commandMap.containsKey(input)) {
                commandMap.get(input).execute();
            } else {
                logger.info("❌ Invalid command. Type 'help' for a list of commands.");
            }
        }
    }

    private Player getOrCreatePlayerSession(){
        logger.info("🔄 Checking for existing player session...");

        Optional<Player> cachedPlayer = playerSessionService.getPlayerSession();

        if(cachedPlayer.isPresent()){
            logger.info("✅ Welcome back, " + cachedPlayer.get().getName() + "!");
            return cachedPlayer.get();
        }

        //No session found -> Ask for player ID
        logger.info("No active session found. PLease enter your Player ID: ");
        long playerId;

        while (true) {
            try {
                playerId = Long.parseLong(reader.readLine().trim());
                break;
            } catch (NumberFormatException e) {
                logger.error("❌ Invalid input. Please enter a valid numeric Player ID.");
            }
        }

        Optional<Player> player = playerService.getPlayerById(playerId);
        if(player.isPresent()){
            playerSessionService.storePlayerSession(player.get());
            logger.info("✅ Player found! Logging in " + player.get().getName() + "...");
            return player.get();
        } else {
            logger.error("❌ Player ID not found. Please enter a valid Player ID.");
            return getOrCreatePlayerSession();
        }


    }
}
