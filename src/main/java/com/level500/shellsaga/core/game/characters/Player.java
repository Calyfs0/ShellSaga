package com.level500.shellsaga.core.game.characters;

import com.level500.shellsaga.config.GameConstants;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int level;
    private int hp;
    private int mana;
    private int attackPower;
    private int defense;
    private int speed;

    @Enumerated(EnumType.STRING)
    private Playstyle playStyle;

    public void levelUp(){
        this.level++;
        this.hp += 10;
        this.mana += 5;
        System.out.println(name + " leveled up to " + level + "!");
    }

    public static Player createNewPlayer(String name, Playstyle playstyle){
        return Player.builder()
                .name(name)
                .level(1)
                .hp(GameConstants.DEFAULT_HP)
                .mana(GameConstants.DEFAULT_MANA)
                .attackPower(GameConstants.DEFAULT_ATTACK_POWER)
                .defense(GameConstants.DEFAULT_DEFENSE)
                .speed(GameConstants.DEFAULT_SPEED)
                .playStyle(playstyle)
                .build();
    }
}
