package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.entities.GameClasses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameClassesRepository extends JpaRepository<GameClasses, Long> {
    GameClasses findByClassNameIgnoreCase(String className);
}
