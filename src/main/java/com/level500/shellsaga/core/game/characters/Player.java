package com.level500.shellsaga.core.game.characters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.level500.shellsaga.ShellSagaApplication;
import com.level500.shellsaga.config.GameConstants;
import com.level500.shellsaga.converters.InventoryConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
    private int hp;
    private int mana;
    private int attackPower;
    private int defense;
    private int speed;
    private int xp = 0;  // ✅ XP Tracking
    // ✅ Gold & STK Management


    @Enumerated(EnumType.STRING)
    private Playstyle playStyle;

    @Column(nullable = false)
    private String currentArea = "Starting Town"; // 🔥 Default starting location

    // ✅ Returns Maximum HP based on level
    @JsonIgnore // ✅ Prevents Redis from serializing `getMaxHp`
    public int getMaxHp() {
        return GameConstants.DEFAULT_HP + (this.level * 10);
    }

    // ✅ XP Needed for Next Level
    public int calculateXPForNextLevel() {
        return (this.level+1) * 100 - this.xp;
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
        this.hp = getMaxHp(); // Restore HP on level up
        this.mana += 5;
        this.xp = 0;
        logger.info("🎉 {} leveled up to Level {}!", name, level);
    }

    @Getter
    @JdbcTypeCode(SqlTypes.JSON) // ✅ Supports JSON type for PostgreSQL
    @Column(columnDefinition = "jsonb") // ✅ Stores inventory as JSON in PostgreSQL
    @Convert(converter = InventoryConverter.class) // ✅ Converts JSON <-> Java Map
    private Map<String, Integer> inventory = new HashMap<>();

    @Getter
    @Column(columnDefinition = "jsonb") // ✅ Stores equipment as JSONB
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Integer, String> equipment = new HashMap<>();

    public void addItem(String item, int quantity) {
        inventory.put(item, inventory.getOrDefault(item, 0) + quantity);
    }

    public int getItemQuantity(String item) {
        return inventory.getOrDefault(item, 0);
    }

    public void removeItem(String item, int quantity) {
        inventory.put(item, Math.max(inventory.getOrDefault(item, 0) - quantity, 0));
    }

    public Map<Integer,String> getEquipments(){
        return equipment;
    }

    public void equipItem(int slot, String itemName) {
        equipment.put(slot, itemName);
    }

    public void unEquipItem(int slot) {
        equipment.remove(slot);
    }

    public String getEquippedItem(int slot) {
        return equipment.getOrDefault(slot, "None");
    }

    public static Player createNewPlayer(String name, Playstyle playstyle){
        return Player.builder()
                .name(name)
                .level(1)
                .hp(GameConstants.DEFAULT_HP)
                .mana(GameConstants.DEFAULT_MANA)
                .attackPower(GameConstants.DEFAULT_ATTACK_POWER)
                .defense(GameConstants.DEFAULT_DEFENSE)
                .speed(GameConstants.DEFAULT_SPEED)
                .playStyle(playstyle)
                .currentArea("Terminus")
                .xp(0)
                .inventory(new HashMap<>())
                .build();
    }
}
