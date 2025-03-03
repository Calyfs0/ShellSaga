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
public class UnequipCommand implements GameCommand{
    private static final Logger logger = LoggerFactory.getLogger(EquipCommand.class);
    private final PlayerService playerService;
    private final PlayerSessionService playerSessionService;
    private final LineReader reader;

    @Autowired
    public UnequipCommand(PlayerService playerService, PlayerSessionService playerSessionService) throws IOException {
        this.playerService = playerService;
        this.playerSessionService = playerSessionService;
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    @Override
    public void execute(){
            logger.info("You can't equip or unequip items!");
//        Optional<Player> optionalPlayer = playerSessionService.getPlayerSession();
//        if (optionalPlayer.isEmpty()) {
//            logger.info("❌ No active player session found!");
//            return;
//        }
//
//        Player player = optionalPlayer.get();
//
//        List<Equipment> availableEquipment = player.getEquipment().stream()
//                .filter(Equipment::isEquipped)
//                .toList();
//        if (availableEquipment.isEmpty()) {
//            logger.info("❌ You don't have any items to equip.");
//            return;
//        }
//
//        logger.info("\n🎒 Available Equipment:");
//        for (int i = 0; i < availableEquipment.size(); i++) {
//            logger.info("{}. {}. {}", i + 1, availableEquipment.get(i), availableEquipment.get(i).getStats());
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
//        if (choice < 1 || choice > availableEquipment.size()) {
//            logger.info("❌ Invalid selection. Please try again.");
//            return;
//        }
//
//        Equipment selectedItem = availableEquipment.get(choice - 1);
//
//        // ✅ Check for class restrictions
//        if (!selectedItem.canEquip(player.getPlayStyle().name())) {
//            logger.info("❌ You cannot equip this item as a " + player.getPlayStyle().name());
//            return;
//        }
//
//        // ✅ Equip item and update player state
//        playerService.unEquipItem(selectedItem, player);
//        logger.info("✅ You un-equipped: " + selectedItem.getName());
//
//        playerSessionService.updateSession(player);
//        playerService.savePlayer(player);

    }
}
