package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.entities.enums.EquipmentSlot;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.core.game.items.EquipmentStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> getAllEquipmentByPlayer(Player player);
    List<Equipment> getEquipmentById(Long id);
}
