package com.level500.shellsaga.cli;

import org.springframework.stereotype.Component;

/*
To make our CLI scalable, we’ll use the Command Pattern.
🛠 Why This is Needed?
✔ Defines a common interface for all game commands.
✔ Makes it easier to add new commands (e.g., attack, spell cast, etc.) in the future.
 */

public interface GameCommand {
    void execute();
}
