package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.items.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByLevelLessThanEqual(int level); // ✅ Fetch equipment matching player's level or below
}
