package com.level500.shellsaga.core.game.items;

import com.level500.shellsaga.core.game.characters.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="inventory_items")
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="player_id", nullable=false)
    private Player player;

    @Getter
    @Setter
    private String itemName;

    @Getter
    @Setter
    private int quantity;

    public InventoryItem(Player player, String itemName, int quantity) {
        this.player = player;
        this.itemName = itemName;
        this.quantity = quantity;
    }
}
