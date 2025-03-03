package com.level500.shellsaga.infrastructure.database;

import com.level500.shellsaga.core.game.entities.UpgradeMaterial;

import com.level500.shellsaga.core.game.entities.enums.UpgradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UpgradeMaterialRepository extends JpaRepository<UpgradeMaterial, Long> {




}
