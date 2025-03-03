package com.level500.shellsaga.core.game.items;

import com.level500.shellsaga.core.game.entities.enums.EquipmentSlot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipments") // ✅ Stores all available equipment in the database
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int level;

    @Enumerated(EnumType.STRING)
    private EquipmentSlot slot;

    private String restrictedClass; // ✅ If null, any class can equip
    private boolean isEquipped = false; // ✅ Indicates if it's currently equipped
    private double price;

    @Embedded // ✅ Includes EquipmentStats as a sub-object in the same table
    private EquipmentStats stats;

    public boolean canEquip(String playerClass) {
        return restrictedClass == null || restrictedClass.equals(playerClass);
    }

    @Override
    public String toString() {
        return name + " (Level " + level + ", Slot: " + slot + ", " + stats.toString() + ")";
    }
}
