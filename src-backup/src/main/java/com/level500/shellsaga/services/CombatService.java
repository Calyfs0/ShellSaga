package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.characters.Player;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CombatService {
    private final LineReader reader;
    private volatile boolean playerDefeated = false;
    private volatile boolean enemyDefeated = false;
    private volatile boolean playerEscaped = false;

    public CombatService() throws IOException{
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    public void fight(Player player, Enemy enemy){

    }
}
