package com.level500.shellsaga.infrastructure.database;


import com.level500.shellsaga.config.EnemySpawnRule;
import com.level500.shellsaga.core.game.locations.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnemySpawnRuleRepository extends JpaRepository<EnemySpawnRule, Long> {
    List<EnemySpawnRule> findByArea(Area area);
}
