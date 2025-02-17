package com.level500.shellsaga.core.game.characters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.config.GameConstants;
import com.level500.shellsaga.converters.EquipmentConverter;
import com.level500.shellsaga.core.game.entities.enums.EquipmentSlot;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.core.game.items.EquipmentStats;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Entity
@Table(name="players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    private static final Logger logger = LoggerFactory.getLogger(ShellSagaApplication.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int level;

    // 🔥 Base stats (before equipment bonuses)
    private int baseHp;
    private int baseMana;
    private int baseAttackPower;
    private int baseDefense;
    private int baseSpeed;
    private double baseCritRate;
    private double baseCritMultiplier;

    // 🔥 Enhanced stats (from equipment bonuses)

    private int enhancedHp;
    private int enhancedMana;
    private int enhancedAttackPower;
    private int enhancedDefense;
    private int enhancedSpeed;
    private double enhancedCritRate;
    private double enhancedCritMultiplier;
    private int receivedDamage = 0; // ✅ Damage received in combat


    private int xp = 0;  // ✅ XP Tracking
    // ✅ Gold & STK Management


    @Enumerated(EnumType.STRING)
    private Playstyle playStyle;

    @Column(nullable = false)
    private String currentArea = "Starting Town"; // 🔥 Default starting location

    // ✅ Returns Maximum HP based on level
    @JsonIgnore // ✅ Prevents Redis from serializing `getMaxHp`


    public int getCurrentHp(){
        return getTotalHp() - this.receivedDamage;
    }

    // ✅ XP Needed for Next Level
    public int calculateXPForNextLevel() {
        return (this.level+1) * 100;
    }

    // ✅ Player Gains XP and Levels Up
    public void gainXp(int amount) {
        this.xp += amount;
        if (this.xp >= calculateXPForNextLevel()) {
            levelUp();
        }
    }
    public void levelUp(){
        this.level++;
        this.baseHp += 10;
        this.baseMana += 5;
        this.xp = 0;
        this.receivedDamage = 0;
        logger.info("🎉 {} leveled up to Level {}!", name, level);
    }

    @Getter
    @JdbcTypeCode(SqlTypes.JSON) // ✅ Supports JSON type for PostgreSQL
    @Column(columnDefinition = "jsonb") // ✅ Stores inventory as JSON in PostgreSQL
    private Map<String, Integer> inventory = new HashMap<>();

    public void showInventory(){
        if(inventory.isEmpty()){
            logger.info("🎒 Your inventory is empty.");
            return;
        }
        logger.info("\n🎒 Inventory:");
        inventory.forEach((item, quantity) -> logger.info("🔹 {} x{}", item, quantity));
    }

    public void dropItem(String item, int quantity){
        if(!inventory.containsKey(item)){
            logger.info("🚫 You don't have any {} in your inventory.", item);
            return;
        }
        int currentQuantity = inventory.get(item);
        if(quantity >= currentQuantity){
            logger.info("🚫 You don't have enough {} in your inventory.", item);
            return;
        }
        inventory.put(item, currentQuantity - quantity);
        logger.info("🗑️ Dropped {} x{} from your inventory.", item, quantity);
    }

    public void sellItem(String item, int quantity, int sellPrice) {
        if (!inventory.containsKey(item)) {
            logger.info("❌ You don't have any {} to sell.", item);
            return;
        }

        int currentQuantity = inventory.get(item);
        int totalGoldEarned = quantity * sellPrice;

        if (quantity >= currentQuantity) {
            inventory.remove(item); // ✅ Remove item if selling all
            logger.info("💰 Sold all {} for {} gold!", item, totalGoldEarned);
        } else {
            inventory.put(item, currentQuantity - quantity);
            logger.info("💰 Sold {} ({} remaining) for {} gold.", item, quantity, inventory.get(item), totalGoldEarned);
        }

        // ✅ Increase player's gold
        addItem("Gold", totalGoldEarned);
    }

    @Getter
    @Setter
    @Column(columnDefinition = "jsonb") // ✅ Stores equipment as JSONB
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = EquipmentConverter.class)
    private List<Equipment> equipment = new ArrayList<>(); // ✅ Store all owned equipment

    public void addEquipment(Equipment item) {
        equipment.add(item);
    }
    public void removeEquipment(Equipment item) {
        equipment.remove(item);
    }

    public Optional<Equipment> getEquippedItem(EquipmentSlot slot) {
        return equipment.stream().filter(e -> e.getSlot() == slot && e.isEquipped()).findFirst();
    }

    public void equipItem(Equipment item) {
        EquipmentSlot slot = item.getSlot();

        //Check if an item is already equipped in this slot
        getEquippedItem(slot).ifPresent(existingItem -> {
            logger.info("Replacing {} with {}", existingItem.getName(), item.getName());
            unEquipItem(slot);
        });
        // ✅ Unequip any existing item in that slot
        getEquippedItem(item.getSlot()).ifPresent(existing -> existing.setEquipped(false));

        // ✅ Equip new item
        item.setEquipped(true);
        applyEquipmentStats(item);
        // ✅ Recalculate stats
        //recalculateStats();
    }

    private void applyEquipmentStats(Equipment item) {
        EquipmentStats stats = item.getStats();
        this.enhancedAttackPower += stats.getAttack();
        this.enhancedDefense += stats.getDefense();
        this.enhancedSpeed += stats.getSpeed();
        this.enhancedHp += stats.getHpIncrease();
        this.enhancedMana += stats.getManaIncrease();
        this.enhancedCritRate += stats.getCritRate();
        this.enhancedCritMultiplier += stats.getCritMultiplier();
    }

    private void removeEquipmentStats(Equipment item) {
        EquipmentStats stats = item.getStats();
        this.enhancedAttackPower -= stats.getAttack();
        this.enhancedDefense -= stats.getDefense();
        this.enhancedSpeed -= stats.getSpeed();
        this.enhancedHp -= stats.getHpIncrease();
        this.enhancedMana -= stats.getManaIncrease();
        this.enhancedCritRate -= stats.getCritRate();
        this.enhancedCritMultiplier -= stats.getCritMultiplier();
    }

    public void unEquipItem(EquipmentSlot slot) {
        getEquippedItem(slot).ifPresent(existingItem -> {
            existingItem.setEquipped(false);
            removeEquipmentStats(existingItem);
            logger.info("Unequipped: {}", existingItem.getName());
        });

        // ✅ Recalculate stats after unequipping
        //recalculateStats();
    }
    public void addItem(String item, int quantity) {
        inventory.put(item, inventory.getOrDefault(item, 0) + quantity);
    }

    public boolean hasItem(String item) {
        return inventory.containsKey(item);
    }

    public int getItemQuantity(String item) {
        return inventory.getOrDefault(item, 0);
    }

    public void removeItem(String item, int quantity) {
        inventory.put(item, Math.max(inventory.getOrDefault(item, 0) - quantity, 0));
    }

    public static Player createNewPlayer(String name, Playstyle playstyle) {
        return Player.builder()
                .name(name)
                .level(1)
                .baseHp(GameConstants.DEFAULT_HP)
                .baseMana(GameConstants.DEFAULT_MANA)
                .baseAttackPower(GameConstants.DEFAULT_ATTACK_POWER)
                .baseDefense(GameConstants.DEFAULT_DEFENSE)
                .baseSpeed(GameConstants.DEFAULT_SPEED)
                .baseCritRate(0)
                .baseCritMultiplier(0)
                .enhancedHp(0)
                .enhancedMana(0)
                .enhancedAttackPower(0)
                .enhancedDefense(0)
                .enhancedSpeed(0)
                .enhancedCritRate(0)
                .enhancedCritMultiplier(0)
                .playStyle(playstyle)
                .currentArea("Terminus")
                .inventory(new HashMap<>())
                .equipment(new ArrayList<>())
                .build();
    }

    @Transient
    @JsonIgnore
    public int getTotalHp() {
        return this.baseHp + this.enhancedHp;
    }
    @Transient
    @JsonIgnore
    public int getTotalMana() {
        return this.baseMana + this.enhancedMana;
    }
    @Transient
    @JsonIgnore
    public int getTotalAttackPower() {
        return this.baseAttackPower + this.enhancedAttackPower;
    }

    @Transient
    @JsonIgnore
    public int getTotalDefense() {
        return this.baseDefense + this.enhancedDefense;
    }

    @Transient
    @JsonIgnore
    public int getTotalSpeed() {
        return this.baseSpeed + this.enhancedSpeed;
    }

    @Transient
    @JsonIgnore
    public double getTotalCritRate() {
        return this.baseCritRate + this.enhancedCritRate;
    }

    @Transient
    @JsonIgnore
    public double getTotalCritMultiplier() {
        return this.baseCritMultiplier + this.enhancedCritMultiplier;
    }


    @Override
    public String toString() {
        return "Player{Name='" + name + "', Level=" + level + ", HP=" + (getTotalHp()-getReceivedDamage()) + ", MP=" + getTotalMana() +
                ", Attack Power=" + getTotalAttackPower() + ", Defense=" + getTotalDefense() +
                ", Speed=" + getTotalSpeed() +
                ", Critical Rate=" + getTotalCritRate() +
                ", Critical Rate Multiplier=" + getTotalCritMultiplier()+ ", Current Area='" + currentArea + "'}";
    }
}
