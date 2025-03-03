package com.level500.shellsaga.cli;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.core.game.items.InventoryItem;
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
public class DropCommand implements GameCommand {
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
        if (optionalPlayer.isEmpty()) {
            logger.info("❌ No active player session found.");
            return;
        }

        Player player = optionalPlayer.get();
        player.displayInventoryAndEquipment(); // ✅ Displays both inventory & unequipped gear

        String itemName = reader.readLine("\nEnter item name to drop: ").trim();

        // ✅ Check if item is in inventory
        Optional<InventoryItem> itemToDrop = player.getInventory().stream()
                .filter(item -> item.getItemName().equalsIgnoreCase(itemName))
                .findFirst();

        if (itemToDrop.isPresent()) {
            InventoryItem item = itemToDrop.get();
            int maxQuantity = item.getQuantity();

            String quantityInput = reader.readLine("Enter quantity to drop (1-" + maxQuantity + "): ").trim();
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

            if (quantity == maxQuantity) {
                player.getInventory().remove(item); // ✅ Remove item completely if dropping all
            } else {
                item.setQuantity(item.getQuantity() - quantity); // ✅ Reduce quantity
            }

            logger.info("🗑️ Dropped {} x{}", itemName, quantity);
            playerSessionService.updateSession(player);
            return;
        }

        // ✅ Check if the item is an unequipped piece of equipment
        Optional<Equipment> equipmentToDrop = player.getEquipment().stream()
                .filter(e -> e.getName().equalsIgnoreCase(itemName))
                .findFirst();

        if (equipmentToDrop.isPresent()) {
            player.removeEquipment(equipmentToDrop.get());
            logger.info("🗑️ Dropped unequipped gear: {}", itemName);
            playerSessionService.updateSession(player);
            return;
        }

        logger.info("❌ You don't have {} in your inventory or unequipped equipment.", itemName);
    }
}
