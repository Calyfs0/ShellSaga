package com.level500.shellsaga.cli;

import com.level500.shellsaga.infrastructure.database.AreaRepository;
import com.level500.shellsaga.services.PlayerService;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class AreasCommand implements GameCommand{

    private final PlayerService playerService;
    private final AreaRepository areaRepository;
    private final LineReader reader;

    public AreasCommand(PlayerService playerService, AreaRepository areaRepository, LineReader reader) throws IOException {
        this.playerService = playerService;
        this.areaRepository = areaRepository;
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
    }
    @Override
    public void execute() {
        String playerIdInput = reader.readLine("\nEnter your player ID: ").trim();
        long playerId;

        try {
            playerId = Long.parseLong(playerIdInput);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid player ID. Please enter a valid numeric player ID.");
            return; // Exit the method
        }
    }
}
