package com.level500.shellsaga.cli;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.services.PlayerSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryCommand implements GameCommand {
    private final PlayerSessionService playerSessionService;

    @Autowired
    public InventoryCommand(PlayerSessionService playerSessionService) {
        this.playerSessionService = playerSessionService;
    }

    @Override
    public void execute() {
        Optional<Player> player = playerSessionService.getPlayerSession();
        player.ifPresent(Player::showInventory);
    }
}
