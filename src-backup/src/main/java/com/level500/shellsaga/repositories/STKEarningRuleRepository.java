package com.level500.shellsaga.repositories;

import com.level500.shellsaga.core.game.entities.STKEarningRule;
import com.level500.shellsaga.core.game.locations.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface STKEarningRuleRepository extends JpaRepository<STKEarningRule, Long> {

}
