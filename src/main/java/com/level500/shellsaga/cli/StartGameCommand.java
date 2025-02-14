package com.level500.shellsaga.cli;

//✔ Executes when the player types start.
public class StartGameCommand implements GameCommand{

    @Override
    public void execute() {
        System.out.println("\uD83C\uDFAE The game has started! Type 'help to see available commands.");
        // Here we can initialize player creation and game mechanics
    }
}
