package com.level500.shellsaga.cli;

public class ExitCommand implements GameCommand{
    @Override
    public void execute() {
        System.out.println("\uD83D\uDC4B Exiting ShellSaga. Goodbye!");
        System.exit(0);
    }
}
