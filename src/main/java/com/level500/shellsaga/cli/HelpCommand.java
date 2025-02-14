package com.level500.shellsaga.cli;

public class HelpCommand implements GameCommand{
    @Override
    public void execute() {
        System.out.println("\uD83C\uDFAE \nAvailable commands:");
        System.out.println("\uD83D\uDCCC start - Begin your adventure");
        System.out.println("\uD83D\uDCCC help - Show available commands");
        System.out.println("\uD83D\uDCCC exit - Quit the game");

    }
}
