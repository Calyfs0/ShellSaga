package com.level500.shellsaga.core.game.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.level500.shellsaga.core.game.items.Equipment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "rarities")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RarityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rarityName;       // e.g. "Common"
    private double statMultiplier;   // e.g. 1.0
    private double upgradeChance;    // e.g. 0.10
    private String nextTier;         // e.g. "Uncommon"

    @OneToMany(mappedBy = "rarity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Equipment> equipment;
}

