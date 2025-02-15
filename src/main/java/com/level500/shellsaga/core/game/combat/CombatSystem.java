package com.level500.shellsaga.core.game.combat;

import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.characters.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatSystem {
    private static final Logger logger = LoggerFactory.getLogger(ShellSagaApplication.class);

    public static void startBattle(Player player, Enemy enemy) {
        logger.info("\n⚔️ The battle begins!");
        logger.info(player.getName() + " VS " + enemy.getName());

        // ✅ Combat logic will be implemented in the next step
    }
}
