package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.locations.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnemyRepository extends JpaRepository<Enemy, Long> {
    List<Enemy> findByAreaAndLevelGreaterThanEqual(Area area, int playerLevel);
    List<Enemy> findByArea(Area area);
    Optional<Enemy> findByLevelGreaterThanEqual(int level); // ✅ Fetch fallback enemies
}
