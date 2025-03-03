package com.level500.shellsaga.core.game.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "upgrade_materials")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpgradeMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_name", nullable = false, unique = true)
    private String materialName;

    private String description;

    @Column(name = "drop_source")
    private String dropSource;

    @Column(name = "drop_chance")
    private double dropChance;

    @Column(name = "drop_quantity_min")
    private int dropQuantityMin;

    @Column(name = "drop_quantity_max")
    private int dropQuantityMax;

    @Column(name = "is_level_material")
    private boolean isLevelMaterial;
}

