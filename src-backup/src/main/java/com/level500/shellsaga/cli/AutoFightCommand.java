package com.level500.shellsaga.cli;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.combat.CombatSession;
import com.level500.shellsaga.core.game.factories.EnemyFactory;
import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.infrastructure.database.AreaRepository;
import com.level500.shellsaga.infrastructure.database.EquipmentRepository;
import com.level500.shellsaga.services.EnemyService;
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

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AutoFightCommand implements GameCommand {
    private static final Logger logger = LoggerFactory.getLogger(AutoFightCommand.class);
    private final PlayerSessionService playerSessionService;
    private final PlayerService playerService;
    private final EnemyService enemyService;
    private final EquipmentRepository equipmentRepository;
    private final EnemyFactory enemyFactory;
    private final AreaRepository areaRepository;
    private final AtomicBoolean stopAutoFight = new AtomicBoolean(false);
    private final LineReader reader;

    @Autowired
    public AutoFightCommand(PlayerSessionService playerSessionService, PlayerService playerService,
                            EnemyService enemyService, EquipmentRepository equipmentRepository,
                            EnemyFactory enemyFactory, AreaRepository areaRepository) throws IOException {
        this.playerSessionService = playerSessionService;
        this.playerService = playerService;
        this.enemyService = enemyService;
        this.equipmentRepository = equipmentRepository;
        this.enemyFactory = enemyFactory;
        this.areaRepository = areaRepository;
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    @Override
    public void execute() {
        Optional<Player> optionalPlayer = playerSessionService.getPlayerSession();

        if (optionalPlayer.isEmpty()) {
            logger.info("❌ No active player session found.");
            return;
        }

        Player player = optionalPlayer.get();
        logger.info("\n⚔️ Entering AutoFight Mode... Type 'stop' after a battle to exit.");

        // Get the player's current area
        String currentAreaName = player.getCurrentArea();
        if (currentAreaName == null) {
            logger.info("❌ You have not entered any area. Use 'areas' to move to a location first.");
            return;
        }

        Optional<Area> optionalArea = areaRepository.findByName(currentAreaName);
        if (optionalArea.isEmpty()) {
            logger.info("❌ Error: Area not found in the database.");
            return;
        }

        Area selectedArea = optionalArea.get();

        // ✅ Run AutoFight continuously until the player dies or stops
        while (!stopAutoFight.get() && player.getCurrentHp() > 0) {
            Enemy selectedEnemy = enemyService.spawnEnemy(selectedArea, player.getLevel());

            if (selectedEnemy == null) {
                logger.info("\n🛡️ No enemies found in this area! Exiting AutoFight.");
                break;
            }

            logger.info("\n⚡ Battling: {} (Level: {})", selectedEnemy.getName(), selectedEnemy.getLevel());

            CombatSession combatSession = new CombatSession(player, selectedEnemy, playerService, playerSessionService, enemyFactory, equipmentRepository);
            combatSession.startBattle();

            if (player.getCurrentHp() <= 0) {
                logger.info("💀 You have been defeated! AutoFight session ending...");
                break;
            }

            logger.info("🎉 Battle Won! XP Gained: {}, Gold Earned: {}", selectedEnemy.getXpReward(), selectedEnemy.getGoldReward());

            player.setReceivedDamage(0);
            playerService.savePlayer(player);

            // ✅ Wait 3 seconds for player input before continuing
            logger.info("\n⏳ Waiting 3 seconds... Type 'stop' to end AutoFight, or press Enter to continue.");
            String userInput = reader.readLine().trim().toLowerCase();

            if ("stop".equals(userInput)) {
                stopAutoFight.set(true);
                logger.info("\n🛑 AutoFight stopped by player.");
                break;
            }
        }

        logger.info("🔚 AutoFight has completely stopped.");
    }
}
