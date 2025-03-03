package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.entities.GameClasses;
import com.level500.shellsaga.core.game.items.AllEquipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllEquipmentRepository extends JpaRepository<AllEquipments, Long> {
    List<AllEquipments> getAllEquipmentsByGameClass(GameClasses gameClass);
}
