package com.level500.shellsaga.cli;

import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.services.PlayerSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
        logger.info("❤️ HP: {} / {}", player.getHp(), player.getMaxHp());
        logger.info("⚔️ Attack Power: {}", player.getAttackPower());
        logger.info("🛡️ Defense: {}", player.getDefense());
        logger.info("⚡ Speed: {}", player.getSpeed());
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

        logger.info("\n📦 Equipments:");
        if (player.getEquipments().isEmpty()) {
            logger.info("   - Inventory is empty.");
        } else {
            player.getEquipments().forEach((item, quantity) -> {
                logger.info("   - {}: {}", item, quantity);
            });
        }

        logger.info("\n🔹 Type 'help' to see available commands.");
    }
}
