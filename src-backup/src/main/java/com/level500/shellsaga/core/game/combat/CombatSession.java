package com.level500.shellsaga.core.game.combat;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.factories.EnemyFactory;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.infrastructure.database.EquipmentRepository;
import com.level500.shellsaga.services.PlayerService;
import com.level500.shellsaga.services.PlayerSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CombatSession {
    private static final Logger logger = LoggerFactory.getLogger(CombatSession.class);

    private final Player player;
    private final Enemy enemy;
    private final PlayerService playerService;
    private final PlayerSessionService playerSessionService;
    private final EnemyFactory enemyFactory;
    private final EquipmentRepository equipmentRepository;

    public CombatSession(Player player, Enemy enemy, PlayerService playerService,
                         PlayerSessionService playerSessionService, EnemyFactory enemyFactory,
                         EquipmentRepository equipmentRepository) {
        this.player = player;
        this.enemy = enemy;
        this.playerService = playerService;
        this.playerSessionService = playerSessionService;
        this.enemyFactory = enemyFactory;
        this.equipmentRepository = equipmentRepository;
    }

    public void startBattle() {
        logger.info("\n⚔️ Battle Started: {} vs {}", player.getName(), enemy.getName());

        while (player.getCurrentHp() > 0 && enemy.getHp() > 0) {
            playerAttack();
            if (enemy.getHp() <= 0) {
                logger.info("\n🎉 Victory! {} defeated {}!", player.getName(), enemy.getName());
                handleVictory();
                break;
            }

            enemyAttack();
            if (player.getCurrentHp() <= 0) {
                logger.info("\n💀 You have been defeated by {}!", enemy.getName());
                playerService.respawnPlayer(player);
                break;
            }
        }

        playerService.savePlayer(player);
        playerSessionService.updateSession(player);
    }

    private void handleVictory() {
        int xpGained = enemy.getXpReward();
        int goldGained = enemy.getGoldReward();
        player.gainXp(xpGained);
        player.addItem("Gold", goldGained);
        logger.info("\n🎖️ You gained {} XP and {} Gold!", xpGained, goldGained);

        // ✅ Generate Equipment Drop
        Optional<Equipment> droppedEquipment = enemyFactory.generateEquipmentDrop(enemy);
        droppedEquipment.ifPresent(equipment -> {
            player.addEquipment(equipment);
            equipmentRepository.save(equipment);
            logger.info("\n🎁 Dropped Equipment: {}", equipment.getName());
        });

        playerService.savePlayer(player);
    }

    public void playerAttack() {
        int damage = player.getTotalAttackPower() - enemy.getDefense();
        if (damage < 1) damage = 1;
        enemy.takeDamage(damage);
        logger.info("⚡ {} attacked {} for {} damage!", player.getName(), enemy.getName(), damage);
    }

    public void enemyAttack() {
        int damage = enemy.getAttackPower() - player.getTotalDefense();
        if (damage < 1) damage = 1;
        player.setReceivedDamage(damage);
        logger.info("\n💥 {} attacked {} for {} damage!", enemy.getName(), player.getName(), damage);
    }
}
