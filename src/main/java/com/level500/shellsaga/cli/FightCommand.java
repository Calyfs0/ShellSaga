package com.level500.shellsaga.cli;

import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.combat.CombatSession;
import com.level500.shellsaga.core.game.combat.CombatSystem;
import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.infrastructure.database.AreaRepository;
import com.level500.shellsaga.infrastructure.database.EnemyRepository;
import com.level500.shellsaga.services.PlayerService;
import com.level500.shellsaga.services.PlayerSessionService;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FightCommand implements GameCommand{
    private final PlayerService playerService;
    private final EnemyRepository enemyRepository;
    private final PlayerSessionService playerSessionService;
    private final LineReader reader;
    private final AreasCommand areasCommand;
    private final AreaRepository areaRepository;

    private static final Logger logger = LoggerFactory.getLogger(ShellSagaApplication.class);

    @Autowired
    public FightCommand(PlayerService playerService, EnemyRepository enemyRepository, AreasCommand areasCommand, AreaRepository areaRepository, PlayerSessionService playerSessionService) throws Exception {
        this.playerService = playerService;
        this.enemyRepository = enemyRepository;
        this.areasCommand = areasCommand;
        this.areaRepository = areaRepository;
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
        String currentAreaName = player.getCurrentArea();

        if (currentAreaName == null) {
            logger.info("❌ You have not entered any area. Use 'areas' to move to a location first.");
            return;
        }

        logger.info("\n⚔️ Searching for enemies in " + currentAreaName + "...");

        Optional<Area> optionalArea = areaRepository.findByName(currentAreaName);
        if (optionalArea.isEmpty()) {
            logger.info("❌ Error: Area not found in the database.");
            return;
        }

        Area selectedArea = optionalArea.get();
        List<Enemy> enemies = enemyRepository.findByAreaAndLevelGreaterThanEqual(selectedArea, player.getLevel());

        if (enemies.isEmpty()) {
            logger.info("\n🛡️ This area seems safe... No enemies found!");
            return;
        }

        logger.info("\n⚔️ Choose an enemy to fight:");
        for (int i = 0; i < enemies.size(); i++) {
            logger.info((i + 1) + ". " + enemies.get(i).getName() + " (Level: " + enemies.get(i).getLevel() + ")");
        }

        String choiceInput = reader.readLine("\nEnter the number of the enemy you want to fight: ").trim();
        int choice;
        try {
            choice = Integer.parseInt(choiceInput);
        } catch (NumberFormatException e) {
            logger.info("❌ Invalid input. Please enter a valid number.");
            return;
        }

        if (choice < 1 || choice > enemies.size()) {
            logger.info("❌ Invalid selection. Please try again.");
            return;
        }

        Enemy selectedEnemy = enemies.get(choice - 1);
        logger.info("\n🔥 You have selected to fight: " + selectedEnemy.getName() + " (Level: " + selectedEnemy.getLevel() + ")");
        CombatSession combatSession = new CombatSession(player, selectedEnemy, playerService, playerSessionService);
        combatSession.startBattle();

        while(player.getHp() > 0 && selectedEnemy.getHp() > 0){
            String action = reader.readLine("\nType 'attack' to strike or 'escape' to flee: ").trim().toLowerCase();
            if ("attack".equals(action)) {
                combatSession.playerAttack();
            } else if ("escape".equals(action)) {
                combatSession.escape();
                break;
            } else {
                logger.info("\n❌ Invalid action. Type 'attack' or 'escape'.");
            }
        }

        playerSessionService.updateSession(player);
    }
}
