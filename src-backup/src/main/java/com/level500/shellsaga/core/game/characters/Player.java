package com.level500.shellsaga.core.game.characters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.level500.shellsaga.core.game.items.Weapon;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.core.game.entities.enums.EquipmentSlot;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.*;

@Entity
@Table(name="players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int level;
    private int experience;

    // 🔥 Base stats (before equipment bonuses)
    private int baseHp;
    private int baseMana;
    private int baseAttackPower;
    private int baseDefense;
    private int baseSpeed;

    // 🔥 Enhanced stats (from equipment bonuses)
    private int enhancedHp;
    private int enhancedMana;
    private int enhancedAttackPower;
    private int enhancedDefense;
    private int enhancedSpeed;

    private int receivedDamage = 0;

    @ManyToOne
    @JoinColumn(name = "hacker_type_id", nullable = false)
    private HackerType hackerType; // 🔥 Dynamic hacker specialization

    @Column(nullable = false)
    private String currentArea = "Terminus"; // 🔥 Dynamic starting location

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "equipped_weapon_id", referencedColumnName = "id")
    private Weapon equippedWeapon; // 🔥 Stores current weapon dynamically

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Weapon> weapons = new ArrayList<>(); // 🔥 Stores all weapons owned by the player

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Integer> inventory = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Equipment> equipment = new ArrayList<>();

    // ✅ Level Up Mechanic
    public void levelUp(){
        this.level++;
        this.baseHp += 10;
        this.baseMana += 5;
        this.experience = 0;
        this.receivedDamage = 0;
    }

    // ✅ Auto-Calculate XP for Next Level
    public int calculateXPForNextLevel() {
        return (this.level + 1) * 100;
    }

    // ✅ Add XP and Check for Level Up
    public void gainXp(int amount) {
        this.experience += amount;
        if (this.experience >= calculateXPForNextLevel()) {
            levelUp();
        }
    }

    // ✅ Equip Weapon
    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    // ✅ Drop Items from Inventory
    public void dropItem(String item, int quantity){
        if (!inventory.containsKey(item)) return;
        int currentQuantity = inventory.get(item);
        inventory.put(item, Math.max(0, currentQuantity - quantity));
        if (inventory.get(item) == 0) inventory.remove(item);
    }

    // ✅ Display Inventory & Equipment
    public void displayInventoryAndEquipment() {
        System.out.println("\n📦 Inventory:");
        inventory.forEach((item, quantity) -> System.out.println(" - " + item + " x" + quantity));

        System.out.println("\n🛡️ Unequipped Gear:");
        equipment.stream()
                .filter(e -> !e.isEquipped())
                .forEach(e -> System.out.println(" - " + e.getName() + " (Level " + e.getLevel() + ")"));
    }
}
