package com.level500.shellsaga.core.game.locations;

import jakarta.persistence.*;
import lombok.*;

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

    @Override
    public String toString(){
        return name + " (Min Level: " + levelRequirement + ")";
    }

}
