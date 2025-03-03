package com.level500.shellsaga.cli;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.services.PlayerSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryCommand implements GameCommand {
    private final PlayerSessionService playerSessionService;
    private static final Logger logger = LoggerFactory.getLogger(InventoryCommand.class);

    @Autowired
    public InventoryCommand(PlayerSessionService playerSessionService) {
        this.playerSessionService = playerSessionService;
    }

    @Override
    public void execute() {
        Optional<Player> optionalPlayer = playerSessionService.getPlayerSession();
        if (optionalPlayer.isEmpty()) {
            logger.info("❌ No active player session found.");
            return;
        }

        Player player = optionalPlayer.get();
        player.showInventory(); // ✅ Calls the new method
    }

}
