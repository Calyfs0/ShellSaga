package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.characters.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByName(String name);  // ✅ Add this method

    Optional<Player> findByName(String name); // (Optional) If you need to fetch player by name

    @Query("SELECT p FROM Player p WHERE p.id = :playerId")
    Optional<Player> findByIdWithInventory(@Param("playerId") Long playerId);

    @Query("SELECT p FROM Player p LEFT JOIN FETCH p.equipmentList LEFT JOIN FETCH p.inventoryItems WHERE p.id = :id")
    Optional<Player> findByIdWithInventoryAndEquipment(@Param("id") Long id);

}
