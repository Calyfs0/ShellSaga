package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.factories.EnemyFactory;
import com.level500.shellsaga.core.game.locations.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnemyService {
    private final EnemyFactory enemyFactory;

    @Autowired
    public EnemyService(EnemyFactory enemyFactory) {
        this.enemyFactory = enemyFactory;
    }

    public Enemy spawnEnemy(Area area, int playerLevel) {
        return enemyFactory.createEnemy(area, playerLevel);
    }
}
