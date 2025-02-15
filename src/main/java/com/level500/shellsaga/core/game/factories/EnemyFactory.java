package com.level500.shellsaga.core.game.factories;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.infrastructure.database.EnemyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class EnemyFactory {
    private final EnemyRepository enemyRepository;
    private final Random random = new Random();

    public EnemyFactory(EnemyRepository enemyRepository) {
        this.enemyRepository = enemyRepository;
    }

    public Enemy createEnemy(Area area, int playerLevel) {
        List<Enemy> possibleEnemies = enemyRepository.findByAreaAndLevelGreaterThanEqual(area, playerLevel);

        if (possibleEnemies.isEmpty()) {
            return getFallbackEnemy(area, playerLevel);
        }

        Enemy enemy = possibleEnemies.get(random.nextInt(possibleEnemies.size())); // ✅ Random selection from database

        // ✅ 10% chance to make enemy rare
        boolean isRare = random.nextDouble() < 0.10;
        enemy.setRare(isRare);

        // ✅ If rare, multiply HP by 3
        if (isRare) {
            enemy.setHp(enemy.getHp() * 3);
        }

        return enemy;

    }

    private Enemy getFallbackEnemy(Area area, int playerLevel) {
        Optional<Enemy> fallback = enemyRepository.findByLevelGreaterThanEqual(playerLevel);
        return fallback.orElseGet(() -> new Enemy(null, "Mysterious Shadow", playerLevel, 100, 15, 5, 4,false, area));
    }
}
