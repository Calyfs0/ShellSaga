package com.level500.shellsaga.cli;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.services.AreaService;
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
import java.util.List;
import java.util.Optional;

@Component
public class AreasCommand implements GameCommand {
    private static final Logger logger = LoggerFactory.getLogger(AreasCommand.class);
    private final PlayerService playerService;
    private final PlayerSessionService playerSessionService;
    private final AreaService areaService;
    private final LineReader reader;

    @Autowired
    public AreasCommand(PlayerService playerService, PlayerSessionService playerSessionService, AreaService areaService) throws Exception {
        this.playerService = playerService;
        this.playerSessionService = playerSessionService;
        this.areaService = areaService;

        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    @Override
    public void execute() {
        Optional<Player> cachedPlayer = playerSessionService.getPlayerSession();

        if (cachedPlayer.isEmpty()) {
            logger.error("❌ No active player session found! Please restart the game.");
            return;
        }

        Player player = cachedPlayer.get();
        logger.info("✅ Using stored player session: " + player.getName());

        // ✅ Fetch areas available based on level
        List<Area> availableAreas = areaService.getAvailableAreas(player.getLevel());

        if (availableAreas.isEmpty()) {
            logger.info("❌ No areas available for your level.");
            return;
        }

        logger.info("\n🌍 Available Areas:");
        for (int i = 0; i < availableAreas.size(); i++) {
            logger.info("{}. {} (Min Level: {})", i + 1, availableAreas.get(i).getName(), availableAreas.get(i).getLevelRequirement());
        }

        String choiceInput = reader.readLine("\nSelect an area to move to (enter number): ").trim();
        int choice;
        try {
            choice = Integer.parseInt(choiceInput);
        } catch (NumberFormatException e) {
            logger.info("❌ Invalid input. Please enter a valid number.");
            return;
        }

        if (choice < 1 || choice > availableAreas.size()) {
            logger.info("❌ Invalid selection. Please try again.");
            return;
        }

        Area selectedArea = availableAreas.get(choice - 1);
        player.setCurrentArea(selectedArea.getName());

        logger.info("\n🚀 Moving to {}...", selectedArea.getName());
        if (selectedArea.getName().equalsIgnoreCase("Terminus")) {
            player.setReceivedDamage(0);
            logger.info("\n🏠 You have returned to Terminus. Your HP is fully restored!");
        }

        playerSessionService.storePlayerSession(player);
        playerService.savePlayer(player);
    }
}
