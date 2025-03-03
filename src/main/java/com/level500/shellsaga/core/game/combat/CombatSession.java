package com.level500.shellsaga.core.game.combat;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.factories.EnemyFactory;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.infrastructure.database.EquipmentRepository;
import com.level500.shellsaga.services.PlayerService;
import com.level500.shellsaga.services.PlayerSessionService;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class CombatSession {
    private static final Logger logger = LoggerFactory.getLogger(CombatSession.class);

    private final Player player;
    private final Enemy enemy;
    private final PlayerService playerService;
    private final PlayerSessionService playerSessionService;
    private final EnemyFactory enemyFactory;
    private final EquipmentRepository equipmentRepository;
    private final LineReader reader;
    private volatile boolean battleOngoing;
    private final ScheduledExecutorService enemyAttackExecutor;

    public CombatSession(Player player, Enemy enemy, PlayerService playerService,
                         PlayerSessionService playerSessionService, EnemyFactory enemyFactory,
                         EquipmentRepository equipmentRepository) throws IOException {
        this.player = player;
        this.enemy = enemy;
        this.playerService = playerService;
        this.playerSessionService = playerSessionService;
        this.enemyFactory = enemyFactory;
        this.equipmentRepository = equipmentRepository;
        this.enemyAttackExecutor = Executors.newSingleThreadScheduledExecutor();
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
        this.battleOngoing = true;
    }

    public void startAutoBattle() {
        logger.info("\n⚔️ Battle Started: {} vs {}", player.getName(), enemy.getName());

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

        Runnable playerAttackTask = () -> {
            if (player.getTotalRemainingHp() > 0 && enemy.getHp() > 0) {
                playerAttack();
                if (enemy.getHp() <= 0) {
                    executorService.shutdown();
                }
            }
        };

        Runnable enemyAttackTask = () -> {
            if (player.getTotalRemainingHp() > 0 && enemy.getHp() > 0) {
                enemyAttack();
                if (player.getTotalRemainingHp() <= 0) {
                    playerService.respawnPlayer(player);
                    executorService.shutdown();
                }
            }
        };

        long playerAttackInterval = (long) (5000 / player.getTotalSpeed());
        long enemyAttackInterval = (long) (5000 / enemy.getSpeed());

        executorService.scheduleAtFixedRate(playerAttackTask, 0, playerAttackInterval, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(enemyAttackTask, 0, enemyAttackInterval, TimeUnit.MILLISECONDS);

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        playerService.savePlayer(player);
        playerSessionService.updateSession(player);
    }

    public void startBattle() {
        logger.info("\n⚔️ Battle Started: {} vs {}", player.getName(), enemy.getName());

        // Start enemy attack thread
        startEnemyAttacks();

        // ✅ Start a separate thread for player input

        while (battleOngoing && player.getTotalRemainingHp() > 0 && enemy.getHp() > 0) {
            try {
                String action = reader.readLine("\nType 'attack' to strike or 'escape' to flee: ").trim().toLowerCase();

                if ("attack".equals(action)) {
                    synchronized (this) {  // Prevent simultaneous actions
                        playerAttack();
                    }
                } else if ("escape".equals(action)) {
                    logger.info("\n🏃 You escaped the battle!");
                    battleOngoing = false;
                    enemyAttackExecutor.shutdown();
                    return;
                } else {
                    logger.info("\n❌ Invalid action. Type 'attack' or 'escape'.");
                }
            } catch (Exception e) {
                logger.error("Error reading input", e);
            }
        }


    }

    private void startEnemyAttacks() {
        enemyAttackExecutor.scheduleAtFixedRate(() -> {
            if (battleOngoing && player.getTotalRemainingHp() > 0 && enemy.getHp() > 0) {
                synchronized (this) {  // Prevent simultaneous actions
                    enemyAttack();
                }
            }
        }, (long) (enemy.getSpeed() * 1000), (long) (enemy.getSpeed() * 1000), TimeUnit.MILLISECONDS);
    }

    private void listenForPlayerCommands() {
        Thread playerInputThread = new Thread(() -> {
            while (battleOngoing && player.getTotalRemainingHp() > 0 && enemy.getHp() > 0) {
                String action = "";
                try {
                    action = reader.readLine("\nType 'attack' to strike or 'escape' to flee: ").trim().toLowerCase();
                } catch (Exception e) {
                    logger.error("Error reading input", e);
                }

                if ("attack".equals(action)) {
                    playerAttack();
                } else if ("escape".equals(action)) {
                    logger.info("\n🏃 You escaped the battle!");
                    battleOngoing = false;
                    enemyAttackExecutor.shutdown();
                    return;
                } else {
                    logger.info("\n❌ Invalid action. Type 'attack' or 'escape'.");
                }
            }
        });
        playerInputThread.start();
    }

    private void playerAttack() {
        double criticalRoll = Math.random() * 100;
        boolean isCriticalHit = criticalRoll <= player.getTotalCritRate();
        int damage = player.getTotalAttackPower() - enemy.getDefense();

        if (isCriticalHit) {
            damage += (int)((player.getTotalCritMultiplier()/100) * damage);
            logger.info("💥 Critical Hit!");
        }

        if (damage < 1) damage = 1;
        enemy.takeDamage(damage);
        logger.info("⚡ {} attacked {} for {} damage!", player.getName(), enemy.getName(), damage);

        if (enemy.getHp() <= 0) {
            logger.info("\n🎉 Victory! {} defeated {}!", player.getName(), enemy.getName());
            handleVictory();
            battleOngoing = false;
            enemyAttackExecutor.shutdown();
        }
    }

    private void enemyAttack() {
        int damage = enemy.getAttackPower() - player.getTotalDefense();
        if (damage < 1) damage = 1;
        player.setReceivedDamage(damage);
        logger.info("\n💥 {} attacked {} for {} damage! Player HP left: {}", enemy.getName(), player.getName(), damage, player.getTotalRemainingHp());

        if (player.getTotalRemainingHp() <= 0) {
            logger.info("\n💀 You have been defeated by {}!", enemy.getName());
            battleOngoing = false;
            enemyAttackExecutor.shutdown();
            playerService.respawnPlayer(player);
            logger.info("🏠 You have been revived at Terminus.");
            logger.info("❤️ Your HP and Mana have been fully restored!");
            playerSessionService.updateSession(player);
        }
    }

    private void handleVictory() {
        int xpGained = enemy.getXpReward();
        int goldGained = enemy.getGoldReward();
        player.gainXp(xpGained);
        player.addItem("Gold", goldGained);
        logger.info("\n🎖️ You gained {} XP and {} Gold!", xpGained, goldGained);
        playerService.savePlayer(player);
    }

    // ✅ Update Player Session (Fixing Redis Issues)
    public void updateSession() {
        playerSessionService.updateSession(player);
        logger.info("\n🔄 Session Updated: {}'s stats saved!", player.getName());
    }

//    public void playerAttack() {
//        int damage = player.getTotalAttackPower() - enemy.getDefense();
//        if (damage < 1) damage = 1;
//        enemy.takeDamage(damage);
//        logger.info("⚡ {} attacked {} for {} damage!", player.getName(), enemy.getName(), damage);
//    }
//
//    public void enemyAttack() {
//        //int damage = enemy.getAttackPower() - player.getTotalDefense();
//        int damage = 50;
//        if (damage < 1) damage = 1;
//        player.setReceivedDamage(damage);
//        logger.info("\n💥 {} attacked {} for {} damage! Player hp left {}", enemy.getName(), player.getName(), damage, player.getTotalHp());
//    }
}
