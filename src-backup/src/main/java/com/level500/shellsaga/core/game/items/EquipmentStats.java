package com.level500.shellsaga.core.game.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentStats {
    private int attack = 0;
    private int defense = 0;
    private double critRate = 0; // ✅ Critical Hit Chance %
    private double critMultiplier = 0; // ✅ Critical Damage Multiplier
    private int speed = 0;
    private int hpIncrease = 0;
    private int manaIncrease = 0;
    private double manaReduction = 0; // ✅ Reduces mana cost %
    private double hpAbsorption = 0; // ✅ % of attack converted into HP

    public EquipmentStats(int attack, int defense, double critChance, double critMultiplier, int speed) {
        this.attack = attack;
        this.defense = defense;
        this.critRate = critChance;
        this.critMultiplier = critMultiplier;
        this.speed = speed;

    }

    @Override
    public String toString() {
        return "ATK: " + attack + ", DEF: " + defense + ", CRIT: " +  String.format("%.2f",critRate) + "%, CRITMULTIPLIER: " + critMultiplier +
                ", SPD: " + speed + ", HP Bonus: " + hpIncrease + ", MP Bonus: " + manaIncrease;
    }
}
