package com.level500.shellsaga.core.game.factories;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.infrastructure.database.EnemyRepository;
import com.level500.shellsaga.infrastructure.database.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class EnemyFactory {
    private final EnemyRepository enemyRepository;
    private final EquipmentRepository equipmentRepository;
    private final Random random = new Random();

    @Autowired
    public EnemyFactory(EnemyRepository enemyRepository,EquipmentRepository equipmentRepository) {
        this.enemyRepository = enemyRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public Enemy createEnemy(Area area, int playerLevel) {
        List<Enemy> possibleEnemies = enemyRepository.findByArea(area);

        if (possibleEnemies.isEmpty()) {
            return getFallbackEnemy(area, playerLevel);
        }

        // Select a random enemy from database
        Enemy baseEnemy = possibleEnemies.get(random.nextInt(possibleEnemies.size()));

        // 10% chance to spawn a rare enemy
        boolean isRare = random.nextInt(10) == 0;
        int xpReward = baseEnemy.getLevel() * 10; // XP scales with level
        int goldReward = baseEnemy.getLevel() * 5; // Gold scales with level

        return new Enemy(
                null, // No ID, let the database handle it
                baseEnemy.getName(),
                baseEnemy.getLevel(),
                baseEnemy.getHp() * (isRare ? 3 : 1), // Rare enemies have 3x HP
                baseEnemy.getAttackPower(),
                baseEnemy.getDefense(),
                baseEnemy.getSpeed(),
                isRare,
                area,
                xpReward,
                goldReward
        );
    }

    private Enemy getFallbackEnemy(Area area, int playerLevel) {
        return new Enemy(
                null,
                "Mysterious Shadow",
                playerLevel,
                100,
                15,
                5,
                4,
                false,
                area,
                playerLevel * 10, // XP Reward
                playerLevel * 5  // Gold Reward
        );
    }

    // ✅ Generates a random equipment drop based on the enemy level
    // ✅ Generates a random equipment drop based on the enemy level
    public Optional<Equipment> generateEquipmentDrop(Enemy enemy) {
        if (random.nextDouble() < 0.20) { // 20% chance to drop an item
            List<Equipment> possibleDrops = equipmentRepository.findByLevelLessThanEqual(enemy.getLevel());
            if (!possibleDrops.isEmpty()) {
                return Optional.of(possibleDrops.get(random.nextInt(possibleDrops.size())));
            }
        }
        return Optional.empty(); // No drop this time
    }
}
