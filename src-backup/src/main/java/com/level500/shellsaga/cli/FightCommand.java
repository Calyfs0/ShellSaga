package com.level500.shellsaga.cli;

import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.combat.CombatSession;
import com.level500.shellsaga.core.game.combat.CombatSystem;
import com.level500.shellsaga.core.game.factories.EnemyFactory;
import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.infrastructure.database.AreaRepository;
import com.level500.shellsaga.infrastructure.database.EnemyRepository;
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

import java.util.List;
import java.util.Optional;

@Component
public class FightCommand implements GameCommand{
    private final PlayerService playerService;
    private final EnemyRepository enemyRepository;
    private final PlayerSessionService playerSessionService;
    private final EquipmentRepository equipmentRepository;
    private final LineReader reader;
    private final AreasCommand areasCommand;
    private final AreaRepository areaRepository;
    private final EnemyService enemyService;
    EnemyFactory enemyFactory;

    private static final Logger logger = LoggerFactory.getLogger(FightCommand.class);

    @Autowired
    public FightCommand(PlayerService playerService, EnemyRepository enemyRepository, EnemyFactory enemyFactory, AreasCommand areasCommand, AreaRepository areaRepository, PlayerSessionService playerSessionService, EquipmentRepository equipmentRepository, EnemyService enemyService) throws Exception {
        this.playerService = playerService;
        this.enemyRepository = enemyRepository;
        this.enemyFactory = enemyFactory;
        this.areasCommand = areasCommand;
        this.areaRepository = areaRepository;
        this.playerSessionService = playerSessionService;
        this.equipmentRepository = equipmentRepository;
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
        this.enemyService = enemyService;
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
        Enemy enemy = enemyService.spawnEnemy(selectedArea, player.getLevel());
        if (enemy == null) {
            logger.info("\n🛡️ This area seems safe... No enemies found!");
            return;
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("\n🔥" + enemy.getName() + " (Level: " + enemy.getLevel() + ") has appeared.");
        CombatSession combatSession = new CombatSession(player, enemy, playerService, playerSessionService, enemyFactory, equipmentRepository);
        combatSession.startBattle();

        while(player.getBaseHp() > 0 && enemy.getHp() > 0){
            String action = reader.readLine("\nType 'attack' to strike or 'escape' to flee: ").trim().toLowerCase();
            if ("attack".equals(action)) {
                combatSession.playerAttack();
            } else if ("escape".equals(action)) {
                //combatSession.escape();
                break;
            } else if(player.getBaseHp() <=0) {
                playerService.respawnPlayer(player); // ✅ Move player to Terminus & restore HP
                logger.info("🏠 You have been revived at Terminus.");
                logger.info("❤️ Your HP and Mana have been fully restored!");
                break;
            }else {
                logger.info("\n❌ Invalid action. Type 'attack' or 'escape'.");
            }
        }

        playerSessionService.updateSession(player);
    }
}
