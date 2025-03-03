package com.level500.shellsaga.core.game.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "stk_earning_rules")
public class STKEarningRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String condition; // Example: "Defeat Rare Enemy", "Uncover Achievement"
    private int stkReward; // Example: 5 STK, 10 STK, etc.
    private boolean oneTimeOnly; // If true, player can earn it only once
}
