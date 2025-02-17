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
import java.util.Map;
import java.util.Optional;

@Component
public class SellCommand implements GameCommand {
    private final PlayerSessionService playerSessionService;
    private static final Logger logger = LoggerFactory.getLogger(SellCommand.class);
    private final LineReader reader;
    private static final Map<String, Integer> sellPrices = Map.of(
            "Health Potion", 10,
            "Mana Potion", 15,
            "Steel Sword", 100
    );

    @Autowired
    public SellCommand(PlayerSessionService playerSessionService) throws IOException {
        this.playerSessionService = playerSessionService;
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    @Override
    public void execute() {
        Optional<Player> optionalPlayer = playerSessionService.getPlayerSession();
        optionalPlayer.ifPresent(Player::showInventory);

        Player player = optionalPlayer.get();

        String itemName = reader.readLine("\nEnter item name to sell: ").trim();
        if (!player.getInventory().containsKey(itemName)) {
            logger.info("❌ You don't have that item.");
            return;
        }

        int maxQuantity = player.getInventory().get(itemName);
        int sellPrice = sellPrices.getOrDefault(itemName, 5); // Default sell price = 5 gold
        String quantityInput = reader.readLine("Enter quantity to sell (1-{}): ", (char) (1 - maxQuantity)).trim();

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

        player.sellItem(itemName, quantity, sellPrice);
    }
}
