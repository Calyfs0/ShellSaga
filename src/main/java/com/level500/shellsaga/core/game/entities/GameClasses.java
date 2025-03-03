package com.level500.shellsaga.core.game.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.items.AllEquipments;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "game_classes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameClasses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    @OneToMany(mappedBy = "gameClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Player> player;

    @OneToMany(mappedBy = "gameClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AllEquipments> allEquipments;
}
