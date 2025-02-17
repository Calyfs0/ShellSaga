package com.level500.shellsaga.core.game.characters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.level500.shellsaga.config.GameConstants;
import com.level500.shellsaga.core.game.locations.Area;
import jakarta.persistence.*;
import lombok.*;

@Entity // ✅ Marks Enemy as a JPA Entity
@Table(name = "enemies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Enemy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int level;
    private int hp;
    private int attackPower;
    private int defense;
    private int speed;

    @Transient
    private boolean isRare;

    @ManyToOne // ✅ Defines many enemies belonging to one area
    @JoinColumn(name = "area_id", nullable = false)
    private Area area; // 🔥 Establishes relationship with Area entity

    public boolean isDefeated() {
        return this.hp <= 0;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
    }

    // ✅ Returns Maximum HP based on level
    @JsonIgnore // ✅ Prevents Redis from serializing `getMaxHp`
    public int getMaxHp() {
        return GameConstants.DEFAULT_HP + (level * 10);
    }

    @Override
    public String toString() {
        return name + " (Level: " + level + ", HP: " + hp + ", Attack: " + attackPower + ", Defense: " + defense + ", Speed: " + speed + ")";
    }

}
