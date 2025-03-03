package com.level500.shellsaga.cli;

import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.services.PlayerSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StatusCommand implements GameCommand{
    private final PlayerSessionService playerSessionService;
    private static final Logger logger = LoggerFactory.getLogger(ShellSagaApplication.class);

    public StatusCommand(PlayerSessionService playerSessionService) {
        this.playerSessionService = playerSessionService;
    }

    @Override
    public void execute(){
        logger.info("📜 Checking player status...");

        //Retrieve the current player session from Redis
        Optional<Player> cachedPlayer = playerSessionService.getPlayerSession();

        if(cachedPlayer.isEmpty()){
            logger.error("❌ No active player session found! Please restart the game.");
            return;
        }

        Player player = cachedPlayer.get();

        // ✅ Display Player Stats
        logger.info("\n🎭 **Player Status** 🎭");
        logger.info("👤 Name: {}", player.getName());
        logger.info("⭐ Level: {}", player.getLevel());
        logger.info("❤️ HP: {} / {}", player.getTotalHp() - player.getReceivedDamage(), player.getTotalHp());
        logger.info("⚔️ Attack Power: {}", player.getTotalAttackPower());
        logger.info("🛡️ Defense: {}", player.getTotalDefense());
        logger.info("⚡ Speed: {}", player.getTotalSpeed());
        logger.info("⚔️ Critical Chance: {}", player.getTotalCritRate());
        logger.info("⚔️⚔️ Critical Multiplier: {}", player.getTotalCritMultiplier());
        logger.info("📍 Current Area: {}", player.getCurrentArea());

        // ✅ Display XP Progress
        int nextLevelXP = player.calculateXPForNextLevel();
        logger.info("🔷 XP: {} / {} (Next Level)", player.getXp(), nextLevelXP);

        logger.info("\n📦 Inventory:");
        if (player.getInventory().isEmpty()) {
            logger.info("   - Inventory is empty.");
        } else {
            player.getInventory().forEach((item, quantity) -> {
                logger.info("   - {}: {}", item, quantity);
            });
        }

        logger.info("\n📦 Equipped Items:");
        List<Equipment> equippedEquipment = player.getEquipment().stream()
                .filter(Equipment::isEquipped)
                .toList();
        if (equippedEquipment.isEmpty()) {
            logger.info("❌ You don't have any equipped items.");
        }
        else {
            equippedEquipment.forEach((item) -> {
                logger.info("   - {}:(Level: {}) {}.", item.getName(), item.getLevel(), item.getStats());
            });
        }

        logger.info("\n📦 Equipments Available:");
        List<Equipment> equipment = player.getEquipment().stream()
                .filter(e ->  !e.isEquipped() && e.canEquip(player.getPlayStyle().name()))
                .toList();
        if (equipment.isEmpty()) {
            logger.info("❌ You don't have any spare equipments.");
        }
        else {
            equipment.forEach((item) -> {
                logger.info("   - {}:(Level: {}) {}. Equipped Status: {}. Class: {}", item.getName(), item.getLevel(), item.getStats(), item.isEquipped(), item.getRestrictedClass());
            });
        }

        logger.info("\n🔹 Type 'help' to see available commands.");
    }
}

/*
* private String name;
    private int level;
    private EquipmentSlot slot;
    private String restrictedClass;
    private EquipmentStats stats;
    private boolean isEquipped; */
