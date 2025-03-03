package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.entities.RarityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RarityRepository extends JpaRepository<RarityEntity, Long> {
    Optional<RarityEntity> findByRarityName(String rarityName);
}
