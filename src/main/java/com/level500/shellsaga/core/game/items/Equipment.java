package com.level500.shellsaga.core.game.items;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.entities.GearSlotEntity;
import com.level500.shellsaga.core.game.entities.GearTierList;
import com.level500.shellsaga.core.game.entities.RarityEntity;
import com.level500.shellsaga.core.game.entities.enums.EquipmentSlot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int itemLevel = 1;       // Replaces old `level`
    private int evolutionLevel = 0;  // How many times gear has evolved


    @ManyToOne
    @JoinColumn(name = "gear_slot_id", nullable = false)
    @JsonBackReference
    private GearSlotEntity gearSlot;

    private String name;         // e.g. "Starter Sword"
    private double price;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    @JsonManagedReference
    private Player player;

    @Embedded
    private EquipmentStats stats;

    @ManyToOne
    @JoinColumn(name = "rarity_id")
    @JsonManagedReference
    private RarityEntity rarity;

    @ManyToOne
    @JoinColumn(name = "gear_tier_id")
    @JsonManagedReference
    private GearTierList gearTier;

    // ---------------------------
    // Minimal Accessor Methods
    // ---------------------------

    @Override
    public String toString() {
        return name + " (ilvl " + itemLevel
                + ", slot=" + gearSlot.getDisplayName()
                + ", stats=" + stats.toString() + ")";
    }


    /**
     * Sell price => half the original price
     */
    public int getSellPrice() {
        return (int) (this.price * 0.5);
    }

    /**
     * Just a small convenience method to see if an upgrade material can be applied
     * based on the gear's itemLevel, if you want.
     * (If not needed, you can remove it.)
     */
    public boolean canApplyUpgrade(int materialLevelRequirement) {
        return materialLevelRequirement <= this.itemLevel;
    }
}
