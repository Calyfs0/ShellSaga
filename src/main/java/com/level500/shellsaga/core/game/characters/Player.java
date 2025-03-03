package com.level500.shellsaga.core.game.characters;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.level500.shellsaga.core.game.entities.GameClasses;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.core.game.items.InventoryItem;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    @Setter
    private int level;
    @Setter
    private int experience;

    // 🔥 Base stats (before equipment bonuses)

    private int baseHp;
    @Setter
    private int baseMana;
    @Setter
    private int baseAttackPower;
    @Setter
    private int baseDefense;
    private int baseSpeed;
    @Setter
    private double baseCriticalRate;
    @Setter
    private double baseCriticalMultiplier;

    // 🔥 Enhanced stats (from equipment bonuses)
    @Getter
    @Setter
    private int enhancedHp;
    @Getter
    @Setter
    private int enhancedMana;
    @Getter
    @Setter
    private int enhancedAttackPower;
    @Getter
    @Setter
    private int enhancedDefense;
    private int enhancedSpeed;
    @Getter
    @Setter
    private double enhancedCriticalRate;
    @Getter
    @Setter
    private double enhancedCriticalMultiplier;

    private int receivedDamage = 0;

    @ManyToOne
    @JoinColumn(name = "game_class_id")
    @JsonManagedReference
    private GameClasses gameClass;

    // ✅ Set Current Area
    // ✅ Get Current Area
    // ✅ Track Player's Current Area
    @Setter
    @Getter
    @Column(nullable = false)
    private String currentArea = "Terminus";

    @Getter
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private Set<Equipment> equipmentList = new HashSet<>(); // ✅ Changed from List to Set

    @Getter
    @Setter
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<InventoryItem> inventoryItems = new HashSet<>(); // ✅ Changed from List to Set

    public int getXp() {
        return experience; // ✅ XP is stored in `experience` field
    }

    public void gainXp(int xp) {
        this.experience += xp;

        // ✅ Check for Level Up, but stop at level 100
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (this.experience >= calculateXPForNextLevel() && this.level < 100) {
            this.experience -= calculateXPForNextLevel();
            this.level++;
            System.out.println("\n🔼 Level Up! " + this.name + " is now Level " + this.level + "!");
        }

        // ✅ Prevent XP from increasing after level 100
        if (this.level >= 100) {
            this.experience = 0;
        }
    }

    public void setXp(int xp) {
        this.experience = xp; // ✅ Updates XP properly
    }

    // ✅ Get Total HP
    @JsonIgnore
    public int getTotalHp() {
        return Math.max(0, baseHp + enhancedHp);
    }

    @JsonIgnore
    public int getTotalRemainingHp(){
        return getTotalHp() - receivedDamage;
    }

    // ✅ Get Total Mana
    @JsonIgnore
    public int getTotalMana() {
        return baseMana + enhancedMana;
    }

    // ✅ Get Total Attack Power
    @JsonIgnore
    public int getTotalAttackPower() {
        return baseAttackPower + enhancedAttackPower;
    }

    // ✅ Get Total Defense
    @JsonIgnore
    public int getTotalDefense() {
        return baseDefense + enhancedDefense;
    }

    // ✅ Get Total Speed
    @JsonIgnore
    public int getTotalSpeed() {
        return baseSpeed + enhancedSpeed;
    }
    @JsonIgnore
    public double getTotalCritRate() {
        return baseCriticalRate + enhancedCriticalRate; // ✅ Base + bonus from equipment
    }

    @JsonIgnore
    public double getTotalCritMultiplier() {
        return baseCriticalMultiplier + enhancedCriticalMultiplier;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", experience=" + experience +
                ", totalHp=" + getTotalHp() +
                ", totalMana=" + getTotalMana() +
                ", totalAttackPower=" + getTotalAttackPower() +
                ", totalDefense=" + getTotalDefense() +
                ", totalSpeed=" + getTotalSpeed() +
                ", currentArea='" + getCurrentArea() + "'" +
                '}';
    }


    public void addItem(String itemName, int quantity) {
        Optional<InventoryItem> existingItem = inventoryItems.stream()
                .filter(item -> item.getItemName().equalsIgnoreCase(itemName))
                .findFirst();

        if (existingItem.isPresent()) {
            System.out.println(existingItem.get().getItemName() + " is already present in inventory with value " + existingItem.get().getQuantity());
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            InventoryItem newItem = new InventoryItem();
            newItem.setPlayer(this);
            newItem.setItemName(itemName);
            newItem.setQuantity(quantity);
            inventoryItems.add(newItem);
        }
    }

    public void removeItem(String itemName) {
        inventoryItems.removeIf(item -> item.getItemName().equalsIgnoreCase(itemName));
    }

    public Set<InventoryItem> getInventory() {
        return inventoryItems; // ✅ Converts Set to List when accessing inventory
    }

    public void setInventory(Set<InventoryItem> inventory) {
        this.inventoryItems = inventory; // ✅ Correctly assigns the deserialized inventory
    }


    public boolean hasItem(String itemName) {
        return inventoryItems.stream().anyMatch(item -> item.getItemName().equalsIgnoreCase(itemName));
    }

    public void displayInventory() {
        System.out.println("\n📦 Inventory:");
        if (inventoryItems.isEmpty()) {
            System.out.println(" - Empty");
        } else {
            inventoryItems.forEach(item ->
                    System.out.println(" - " + item.getItemName() + " x" + item.getQuantity()));
        }
    }

    public void displayInventoryAndEquipment() {
        System.out.println("\n📦 Inventory:");
        if (inventoryItems.isEmpty()) {
            System.out.println(" - Empty");
        } else {
            inventoryItems.forEach(item ->
                    System.out.println(" - " + item.getItemName() + " x" + item.getQuantity()));
        }

        System.out.println("\n🛡️ Equipped Gear:");
        if (equipmentList.isEmpty()) {
            System.out.println(" - None");
        } else {
            equipmentList.forEach(equip -> System.out.println(" - " + equip.getName()));
        }
    }

    public void showInventory() {
        System.out.println("\n📦 Inventory:");
        if (inventoryItems.isEmpty()) {
            System.out.println(" - Empty");
        } else {
            inventoryItems.forEach(item ->
                    System.out.println(" - " + item.getItemName() + " x" + item.getQuantity()));
        }
    }

    public void addEquipment(Equipment equipment) {
        equipment.setPlayer(this); // Associate the equipment with this player
        equipmentList.add(equipment);
    }

    public List<Equipment> getEquipment() {
        return new ArrayList<>(equipmentList); // ✅ Converts Set to List when needed
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipmentList = new HashSet<>(equipment); // ✅ Convert List to Set
    }


    public void removeEquipment(Equipment equipment) {
        if (equipmentList.remove(equipment)) {
            System.out.println("\n🛡️ Removed equipment: " + equipment.getName());
        } else {
            System.out.println("\n❌ Equipment not found in your list.");
        }
    }

    public int calculateXPForNextLevel() {
        return (int) (100 * Math.pow(1.75, level - 1)); // ✅ Exponential XP scaling
    }



    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getBaseHp() {
        return baseHp;
    }

    public int getBaseMana() {
        return baseMana;
    }

    public int getBaseAttackPower() {
        return baseAttackPower;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public double getBaseCriticalRate() {
        return baseCriticalRate;
    }

    public double getBaseCriticalMultiplier() {
        return baseCriticalMultiplier;
    }

    public int getEnhancedSpeed() {
        return enhancedSpeed;
    }

    public int getReceivedDamage() {
        return receivedDamage;
    }

    public void setReceivedDamage(int receivedDamage) {
        if(receivedDamage == 0){
            this.receivedDamage = 0;
        }
        else{
            this.receivedDamage += receivedDamage;
        }

    }
    public GameClasses getGameClass() {
        return gameClass;
    }
    public void setGameClass(GameClasses gameClass) {
        this.gameClass = gameClass;
    }
}
