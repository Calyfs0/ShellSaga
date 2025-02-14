package com.level500.shellsaga.cli;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.HashMap;
import java.util.Map;

public class GameLoop {

    private final Map<String, GameCommand> commandMap;
    private final LineReader reader;

    public GameLoop() throws Exception{
        commandMap = new HashMap<>();
        commandMap.put("start", new StartGameCommand());
        commandMap.put("help", new HelpCommand());
        commandMap.put("exit", new ExitCommand());

        Terminal terminal = TerminalBuilder.builder().system(true).build();
        reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    public void start(){
        System.out.println("\n \uD83C\uDFAE Welcome to ShellSaga!");
        System.out.println("\uD83D\uDD39 Type 'start' to begin, or 'help' for available commands.");


            while (true){
                String input = reader.readLine("\n> ").trim().toLowerCase();

                if(commandMap.containsKey(input)){
                    commandMap.get(input).execute();
                }
                else{
                    System.out.println("❌ Invalid command. Type 'help' for a list of commands.");
                }
            }
    }
}
