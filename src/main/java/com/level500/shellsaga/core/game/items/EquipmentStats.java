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


    @Override
    public String toString() {
        return "ATK: " + attack + ", DEF: " + defense + ", CRIT: " +  String.format("%.2f",critRate) + "%, CRITMULTIPLIER: " + critMultiplier +
                ", SPD: " + speed + ", HP Bonus: " + hpIncrease + ", MP Bonus: " + manaIncrease;
    }

    public void applyUpgrade(EquipmentStats statsBoost) {
        this.attack += statsBoost.attack;
        this.defense += statsBoost.defense;
        this.critRate += statsBoost.critRate;
        this.critMultiplier += statsBoost.critMultiplier;
        this.speed += statsBoost.speed;
        this.hpIncrease += statsBoost.hpIncrease;
        this.manaIncrease += statsBoost.manaIncrease;
    }

    public void boostWithCoreExploit(EquipmentStats statsBoost) {
        this.attack += statsBoost.attack * 2; // ✅ Core Exploits double the upgrade effect
        this.defense += statsBoost.defense * 2;
        this.critRate += statsBoost.critRate * 2;
        this.critMultiplier += statsBoost.critMultiplier * 2;
        this.speed += statsBoost.speed * 2;
        this.hpIncrease += statsBoost.hpIncrease * 2;
        this.manaIncrease += statsBoost.manaIncrease * 2;
    }

}
