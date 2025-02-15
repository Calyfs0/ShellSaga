package com.level500.shellsaga.config;

import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.core.game.characters.Enemy;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enemy_spawn_rules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnemySpawnRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private Area area; // 🔥 Links the rule to an area

    @ManyToOne
    @JoinColumn(name = "enemy_id", nullable = false)
    private Enemy enemy; // 🔥 Links the rule to an enemy

    private int spawnChance; // 🔥 Probability of spawning this enemy in the area (1-100)
}
