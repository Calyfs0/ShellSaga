package com.level500.shellsaga.core.game.factories;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.entities.UpgradeMaterial;
import com.level500.shellsaga.core.game.entities.enums.EquipmentSlot;
import com.level500.shellsaga.core.game.entities.enums.UpgradeType;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.core.game.items.EquipmentStats;
import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.infrastructure.database.EnemyRepository;
import com.level500.shellsaga.infrastructure.database.EquipmentRepository;
import com.level500.shellsaga.infrastructure.database.UpgradeMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class EnemyFactory {
    private final EnemyRepository enemyRepository;
    private final EquipmentRepository equipmentRepository;
    private final UpgradeMaterialRepository upgradeMaterialRepository;
    private final Random random = new Random();

    @Autowired
    public EnemyFactory(EnemyRepository enemyRepository,EquipmentRepository equipmentRepository, UpgradeMaterialRepository upgradeMaterialRepository) {
        this.enemyRepository = enemyRepository;
        this.equipmentRepository = equipmentRepository;
        this.upgradeMaterialRepository = upgradeMaterialRepository;
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

//    public Optional<Equipment> generateEquipmentDrop(Enemy enemy) {
//        double dropChance = Math.random();
//        EquipmentSlot[] equipmentSlots = EquipmentSlot.values();
//        Random random = new Random();
//        int randomSlot = random.nextInt(equipmentSlots.length);
//        EquipmentSlot generateSlot = equipmentSlots[randomSlot];
//        if (dropChance > 0.8) { // 20% drop chance
//            Equipment droppedEquipment = Equipment.builder()
//                    .name("Hacked Blade")
//                    .itemLevel(enemy.getLevel())
//                    .slot(generateSlot)
//                    .restrictedClass(null) // Any class can equip
//                    .isEquipped(false)
//                    .price(0.2) // Drop rate? (We might not need this)
//                    .player(null)
//                    .rarity("Rare")// No player assigned initially
//                    .stats(generateEquipmentStats(enemy.getLevel(), generateSlot)) // ✅ Fetch stats dynamically
//                    .build();
//
//            return Optional.of(droppedEquipment);
//        }
//        return Optional.empty();
//    }

//    private EquipmentStats generateEquipmentStats(int level, EquipmentSlot slot) {
//        return new EquipmentStats(
//                10 * (int)(1 + level * 0.05),
//                5 + (level * 2),
//                Math.min(0.05 + (level * 0.002), 1.0),
//                1.5 + (level * 0.05),
//                1 + (level / (level + 20)),
//                100 + (level * 15),
//                50 + (level * 10),
//                0,
//                0);
//    }

//    private EquipmentStats fetchEquipmentStats(int level, EquipmentSlot slot) {
//        return equipmentRepository.findStatsByItemLevelAndSlot(level, slot)
//                .orElse(new EquipmentStats(
//                        10 * (int)(1 + level * 0.05),
//                        5 + (level * 2), Math.min(0.05 + (level * 0.002), 1.0),
//                        1.5 + (level * 0.05),
//                        1 + (level / (level + 20)),
//                        100 + (level * 15),
//                        50 + (level * 10),
//                        0,
//                        0)); // ✅ Fallback default stats
//    }

    public Optional<UpgradeMaterial> generateUpgradeDrop(Enemy enemy) {
        double dropChance = Math.random();

//        // ✅ Normal enemies drop COMMON materials (50% chance)
//        if (dropChance > 0.5) {
//            return upgradeMaterialRepository.findRandomByEnemyLevel(enemy.getLevel());
//        }
//
//        // ✅ Rare enemies drop RARE materials (30% chance)
//        if (enemy.isRare() && dropChance > 0.3) {
//            return upgradeMaterialRepository.findRareUpgradeMaterial(UpgradeType.RARE, enemy.getLevel());
//
//        }

        return Optional.empty();
    }


//    public Optional<UpgradeMaterial> generateFinalBossDrop(Enemy boss) {
//        return upgradeMaterialRepository.findCoreExploitForLevel(UpgradeType.CORE_EXPLOIT, boss.getLevel());
//
//    }



}
