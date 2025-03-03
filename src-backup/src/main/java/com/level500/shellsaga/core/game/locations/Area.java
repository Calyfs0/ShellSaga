package com.level500.shellsaga.core.game.locations;

import com.level500.shellsaga.core.game.characters.Enemy;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private int levelRequirement; // Level restriction for entry

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enemy> enemies; // 🔥 List of enemies in this area

    @Override
    public String toString(){
        return name + " (Min Level: " + levelRequirement + ")";
    }

}
