package com.level500.shellsaga.core.game.factories;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.entities.enums.EquipmentSlot;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.core.game.items.EquipmentStats;
import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.infrastructure.database.EnemyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class EnemyFactory {
    private final EnemyRepository enemyRepository;
    private final Random random = new Random();
    private static final Logger logger = LoggerFactory.getLogger(EnemyFactory.class);

    public EnemyFactory(EnemyRepository enemyRepository) {
        this.enemyRepository = enemyRepository;
    }

    public Enemy createEnemy(Area area, int playerLevel) {
        List<Enemy> possibleEnemies = enemyRepository.findByArea(area);

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
            logger.info("🔥 A Rare " + getEnemyDisplayName(enemy) + " has appeared! (3x HP)");
        }

        return enemy;

    }

    private Enemy getFallbackEnemy(Area area, int playerLevel) {
        Optional<Enemy> fallback = enemyRepository.findByLevelGreaterThanEqual(playerLevel);
        return fallback.orElseGet(() -> new Enemy(null, "Mysterious Shadow", playerLevel, 100, 15, 5, 4,false, area));
    }

    private String getEnemyDisplayName(Enemy enemy) {
        String RESET_COLOR = "\033[0m";  // Reset text color to default
        String RARE_COLOR = "\033[31m";  // Red color for rare enemies

        return RARE_COLOR + enemy.getName() + RESET_COLOR; // 🔥 Red text for rare enemies
    }

    public Equipment generateEquipmentDrop(Enemy enemy) {
//        if(enemy.getLevel() > 10 && enemy.isRare()){
//            return generateRareEquipment(enemy);
//        }
//        else {
//            return generateCommonEquipment(enemy);
//        }
//

        return getEquipment(enemy);
    }

    private Equipment getEquipment(Enemy enemy) {
        EquipmentSlot slot = getRandomEquipmentSlot();
        String itemName = generateItemName(slot, enemy.isRare()); // ✅ Use enum-based name generation
        String restrictedClass = assignClassRestriction(slot); // ✅ Assign class restrictions

        EquipmentStats stats = generateEquipmentStats(enemy, enemy.isRare()); // ✅ Adjust stats for rare items
        int priceMultiplier = enemy.isRare() ? 3 : 1; // ✅ Rare items have double price
        double price = enemy.getLevel() *priceMultiplier * 10; // ✅ Random price
        return new Equipment(itemName, enemy.getLevel(), slot, restrictedClass, stats, false, price);
    }

    private EquipmentSlot getRandomEquipmentSlot() {
        EquipmentSlot[] slots = EquipmentSlot.values();
        return slots[random.nextInt(slots.length)]; // ✅ Randomly select an equipment slot
    }

    private String generateItemName(EquipmentSlot slot, boolean isRare) {
        String prefix = isRare ? "Ancient " : "Iron "; // ✅ Rare items have a special prefix

        return switch (slot) {
            case WEAPON -> prefix + "Weapon";
            case HELMET -> prefix + "Helmet";
            case ARMOR -> prefix + "Armor";
            case SHOULDERS -> prefix + "Shoulder Guards";
            case PANTS -> prefix + "Leggings";
            case BOOTS -> prefix + "Boots";
        };
    }

    private EquipmentStats generateEquipmentStats(Enemy enemy, boolean isRare) {
        double multiplier = isRare ? 2.0 : 1.0; // ✅ Rare items have double stats

        return new EquipmentStats(
                (int) (enemy.getAttackPower() / 2 * multiplier),
                (int) (enemy.getDefense() / 2 * multiplier),
                Math.random() * 10,
                isRare ? 2.0 : 1.5, // ✅ Rare items have higher crit multipliers
                (int) (enemy.getSpeed() / 2 * multiplier),
                (int) (enemy.getHp() / 5 * multiplier),
                (int) (enemy.getHp() / 5 * multiplier),
                isRare ? 0.2 : 0.1, // ✅ Rare items have better mana reduction
                isRare ? 0.1 : 0.05  // ✅ Rare items have better HP absorption
        );
    }

    private String assignClassRestriction(EquipmentSlot slot) {
        return (slot == EquipmentSlot.WEAPON) ? getRandomClassRestriction() : null; // ✅ Only restrict weapons
    }

    private String getRandomClassRestriction() {
        String[] classes = {"ARCHER"}; // ✅ Define playable classes
        return classes[random.nextInt(classes.length)]; // ✅ Randomly assign a restriction
    }


}
