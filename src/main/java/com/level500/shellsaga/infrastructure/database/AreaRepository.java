package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.locations.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByLevelRequirementLessThanEqual(int playerLevel);
}
