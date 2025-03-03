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
@Table(name = "gear_tier_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GearTierList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tierName;
    private Integer startLevel;
    private Integer maxLevel;
    private Double tierStatMultiplier;
    private Double upgradeChance;
    private String NextTier;

    @OneToMany(mappedBy = "gearTier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Equipment> equipment;
}
