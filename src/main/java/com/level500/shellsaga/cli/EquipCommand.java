package com.level500.shellsaga.cli;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.services.PlayerService;
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
import java.util.List;
import java.util.Optional;

@Component
public class EquipCommand implements GameCommand {

    private static final Logger logger = LoggerFactory.getLogger(EquipCommand.class);
    private final PlayerService playerService;
    private final PlayerSessionService playerSessionService;
    private final LineReader reader;

    @Autowired
    public EquipCommand(PlayerService playerService, PlayerSessionService playerSessionService) throws IOException {
        this.playerService = playerService;
        this.playerSessionService = playerSessionService;
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    @Override
    public void execute() {
            logger.info("You can't equip or unequip items!");
//        Optional<Player> optionalPlayer = playerSessionService.getPlayerSession();
//        if (optionalPlayer.isEmpty()) {
//            logger.info("❌ No active player session found!");
//            return;
//        }
//
//        Player player = optionalPlayer.get();
//
//        logger.info("\n📦 Equipped Items:");
//        List<Equipment> equippedEquipment = player.getEquipment().stream()
//                .filter(Equipment::isEquipped)
//                .toList();
//        if (equippedEquipment.isEmpty()) {
//            logger.info("❌ You don't have any equipped items.");
//        } else {
//            for (int i = 0; i < equippedEquipment.size(); i++) {
//                logger.info("{}. {}. {}", i + 1, equippedEquipment.get(i), equippedEquipment.get(i).getStats());
//            }
//        }
//
//        logger.info("\n📦 Equipments Available:");
//        List<Equipment> equipment = player.getEquipment().stream()
//                .filter(e -> !e.isEquipped() && e.canEquip(player.getPlayStyle().name()))
//                .toList();
//        if (equipment.isEmpty()) {
//            logger.info("❌ You don't have any spare equipment.");
//            return;
//        }
//
//        for (int i = 0; i < equipment.size(); i++) {
//            logger.info("{}. {}. {}", i + 1, equipment.get(i), equipment.get(i).getStats());
//        }
//
//        String choiceInput = reader.readLine("\nSelect an item to equip (enter number): ").trim();
//        int choice;
//        try {
//            choice = Integer.parseInt(choiceInput);
//        } catch (NumberFormatException e) {
//            logger.info("❌ Invalid input. Please enter a valid number.");
//            return;
//        }
//
//        if (choice < 1 || choice > equipment.size()) {
//            logger.info("❌ Invalid selection. Please try again.");
//            return;
//        }
//
//        Equipment selectedItem = equipment.get(choice - 1); // ✅ Get the correct item from the list
//
//        playerService.equipItem(selectedItem, player); // ✅ Fixed: Pass the correct item
//        logger.info("\n✅ Successfully equipped {}", selectedItem.getName());// ✅ Fixed log message
//        playerSessionService.updateSession(player);
    }
}
