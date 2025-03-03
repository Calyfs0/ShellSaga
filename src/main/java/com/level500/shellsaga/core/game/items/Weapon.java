package com.level500.shellsaga.core.game.items;

import com.level500.shellsaga.core.game.characters.Player;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "weapons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weapon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int baseDamage;
    private int levelRequired;
    private int upgradeLevel;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player owner;

    private String specialAbility;

    public void upgradeWeapon(String newName, int newDamage) {
        this.name = newName;
        this.baseDamage = newDamage;
        this.upgradeLevel += 10;
    }
}
