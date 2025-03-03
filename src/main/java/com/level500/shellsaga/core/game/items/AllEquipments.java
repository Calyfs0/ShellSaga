package com.level500.shellsaga.core.game.items;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.level500.shellsaga.core.game.entities.GameClasses;
import com.level500.shellsaga.core.game.entities.GearSlotEntity;
import com.level500.shellsaga.core.game.entities.RarityEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "all_equipments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllEquipments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private int itemLevel = 1;       // Replaces old `level`
    private int evolutionLevel = 0;  // How many times gear has evolved

    @ManyToOne
    @JoinColumn(name = "gear_slot_id", nullable = false)
    @JsonBackReference
    private GearSlotEntity gearSlot;

    @Embedded
    private EquipmentStats stats;

    @ManyToOne
    @JoinColumn(name = "rarity_id")
    @JsonManagedReference
    private RarityEntity rarity;

    @ManyToOne
    @JoinColumn(name = "game_class_id")
    @JsonBackReference
    private GameClasses gameClass;

    // ---------------------------
    // Minimal Accessor Methods
    // ---------------------------

    @Override
    public String toString() {
        return itemName + " (ilvl " + itemLevel
                + ", slot=" + gearSlot.getDisplayName()
                + ", stats=" + stats.toString() + ")";
    }

}
