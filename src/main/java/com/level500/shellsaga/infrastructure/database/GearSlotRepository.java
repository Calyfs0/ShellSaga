package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.entities.GearSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GearSlotRepository extends JpaRepository<GearSlotEntity, Long> {
    Optional<GearSlotEntity> findBySlotKey(String slotKey);
}
