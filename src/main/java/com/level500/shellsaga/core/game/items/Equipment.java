package com.level500.shellsaga.core.game.items;

import com.level500.shellsaga.core.game.entities.enums.EquipmentSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {
    private String name;
    private int level;
    private EquipmentSlot slot;
    private String restrictedClass;
    private EquipmentStats stats;
    private boolean isEquipped; // ✅ Flag to indicate if the item is equipped
    private double price;

    public boolean canEquip(String playerClass) {
        return restrictedClass == null || restrictedClass.equals(playerClass);
    }
    @Override
    public String toString() {
        return name + " (Level " + level + ")";
    }

    public boolean isEquipped() {
        return this.isEquipped;
    }
}
