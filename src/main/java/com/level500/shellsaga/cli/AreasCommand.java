package com.level500.shellsaga.cli;

import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.infrastructure.database.AreaRepository;
import com.level500.shellsaga.infrastructure.database.EnemyRepository;
import com.level500.shellsaga.services.PlayerService;
import com.level500.shellsaga.services.PlayerSessionService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AreasCommand implements GameCommand {
    private final PlayerService playerService;
    private final AreaRepository areaRepository;
    private final EnemyRepository enemyRepository;
    private final PlayerSessionService playerSessionService;
    private final LineReader reader;

    private final Map<Long, Long> playerSessions = new HashMap<>(); // ✅ Multi-player safe storage

    private static final Logger logger = LoggerFactory.getLogger(AreasCommand.class);

    @Autowired
    public AreasCommand(PlayerService playerService, AreaRepository areaRepository, EnemyRepository enemyRepository, PlayerSessionService playerSessionService) throws Exception {
        this.playerService = playerService;
        this.areaRepository = areaRepository;
        this.enemyRepository = enemyRepository;
        this.playerSessionService = playerSessionService;

        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    @Override
    public void execute() {
        // ✅ Retrieve the logged-in player from Redis (session created in GameLoop.java)
        Optional<Player> cachedPlayer = playerSessionService.getPlayerSession();

        if (cachedPlayer.isEmpty()) {
            logger.error("❌ No active player session found! Please restart the game.");
            return;
        }

        Player player = cachedPlayer.get();
        logger.info("✅ Using stored player session: " + player.getName());

        // Fetch areas available based on level
        List<Area> availableAreas = areaRepository.findByLevelRequirementLessThanEqual(player.getLevel());
        if (availableAreas.isEmpty()) {
            logger.info("❌ No areas available for your level.");
            return;
        }

        logger.info("\n🌍 Available Areas:");
        // ✅ Ensure "Town" is always available
        Area town = new Area(0L, "Terminus", 1, new ArrayList<>());
        availableAreas.add(0, town); // Add "Town" as the first option
        for (int i = 0; i < availableAreas.size(); i++) {
            logger.info("{}. {} (Min Level: {})", i + 1, availableAreas.get(i).getName(), availableAreas.get(i).getLevelRequirement());
        }

        String choiceInput = reader.readLine("\nSelect an area to move to (enter number): ").trim();
        int choice;
        try {
            choice = Integer.parseInt(choiceInput);
        } catch (NumberFormatException e) {
            logger.info("❌ Invalid input. Please enter a valid number.");
            return;
        }

        if (choice < 1 || choice > availableAreas.size()) {
            logger.info("❌ Invalid selection. Please try again.");
            return;
        }

        Area selectedArea = availableAreas.get(choice - 1);

        // Update player's current location
        player.setCurrentArea(selectedArea.getName());

        logger.info("\n\uD83D\uDEB6 Moving to {}...", selectedArea.getName());
        if(selectedArea.getName().equalsIgnoreCase("Terminus")){
            player.setReceivedDamage(0); // ✅ Restore full HP
            logger.info("\n🏠 You have returned to Terminus. Your HP is fully restored!");
        }else{
            // Fetch and display enemies with a level >= player's level
            //List<Enemy> enemies = enemyRepository.findByAreaAndLevelGreaterThanEqual(selectedArea, player.getLevel());
            List<Enemy> enemies = enemyRepository.findByArea(selectedArea);
            if (enemies.isEmpty()) {
                logger.info("\n🛡️ This area seems safe... No enemies found!");
            } else {
                logger.info("\n⚔\uFE0F Enemies in {} (Minimum Level: {}):", selectedArea.getName(), player.getLevel());
                for (Enemy enemy : enemies) {
                    logger.info("  - {} (Level: {})", enemy.getName(), enemy.getLevel());
                }
            }
        }
        playerSessionService.storePlayerSession(player);
        playerService.savePlayer(player); // ✅ Update HP in database
    }

    public long getStoredPlayerId(Long playerId){
        return playerSessions.get(playerId);
    }

}
