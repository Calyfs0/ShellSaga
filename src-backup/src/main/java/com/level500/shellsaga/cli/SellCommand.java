package com.level500.shellsaga.cli;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.items.Equipment;
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

    @Autowired
    public SellCommand(PlayerSessionService playerSessionService) throws IOException {
        this.playerSessionService = playerSessionService;
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
        player.displayInventoryAndEquipment(); // ✅ New method to display both inventory & unequipped gear

        String itemName = reader.readLine("\nEnter item name to sell: ").trim();

        // ✅ Check if item is in inventory
        if (player.getInventory().containsKey(itemName)) {
            int maxQuantity = player.getInventory().get(itemName);
            int sellPrice = getSellPrice(itemName);

            String quantityInput = reader.readLine("Enter quantity to sell (1-" + maxQuantity + "): ").trim();
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
            logger.info("💰 Sold {} x{} for {} Gold!", itemName, quantity, quantity * sellPrice);
            playerSessionService.updateSession(player);
            return;
        }

        // ✅ Check if the item is an unequipped piece of equipment
        Optional<Equipment> equipmentToSell = player.getEquipment().stream()
                .filter(e -> e.getName().equalsIgnoreCase(itemName) && !e.isEquipped())
                .findFirst();

        if (equipmentToSell.isPresent()) {
            Equipment equipment = equipmentToSell.get();
            double sellPrice = equipment.getPrice() * 0.5; // ✅ Selling price = 50% of original price
            player.addItem("Gold", (int) sellPrice);
            player.removeEquipment(equipment);
            logger.info("💰 Sold unequipped gear: {} for {} Gold!", itemName, (int) sellPrice);
            playerSessionService.updateSession(player);
            return;
        }

        logger.info("❌ You don't have {} in your inventory or unequipped equipment.", itemName);
    }

    private int getSellPrice(String itemName) {
        Map<String, Integer> sellPrices = Map.of(
                "Health Potion", 10,
                "Mana Potion", 15,
                "Steel Sword", 100
        );
        return sellPrices.getOrDefault(itemName, 5);
    }
}
