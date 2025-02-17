package com.level500.shellsaga.cli;

import com.level500.shellsaga.core.game.characters.Player;
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

@Component
public class DropCommand implements GameCommand{
    private static final Logger logger = LoggerFactory.getLogger(DropCommand.class);
    private final PlayerSessionService playerSessionService;
    private final LineReader reader;

    @Autowired
    public DropCommand(PlayerSessionService playerSessionService) throws IOException {
        this.playerSessionService = playerSessionService;
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    @Override
    public void execute() {
        Optional<Player> optionalPlayer = playerSessionService.getPlayerSession();
        optionalPlayer.ifPresent(Player::showInventory);

        Player player = optionalPlayer.get();



        String itemName = reader.readLine("\nEnter item name to drop: ").trim();
        if (!player.getInventory().containsKey(itemName)) {
            logger.info("❌ You don't have that item.");
            return;
        }

        int maxQuantity = player.getInventory().get(itemName);
        String quantityInput = reader.readLine("Enter quantity to drop (1-{}): ", (char) (1 - maxQuantity)).trim();

        int quantity;
        try {
            quantity = Integer.parseInt(quantityInput);
        } catch (NumberFormatException e) {
            logger.info("❌ Invalid input. Please enter a number.");
            return;
        }

        if (quantity < 1 || quantity > maxQuantity) {
            logger.info("❌ Invalid quantity.");
            return;
        }

        player.dropItem(itemName, quantity);
    }
}
