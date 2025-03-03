package com.level500.shellsaga.core.game.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "gear_slots")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GearSlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String slotKey;      // e.g. "EXECUTION_MODULE"
    private String displayName;  // e.g. "Execution Module"

}

