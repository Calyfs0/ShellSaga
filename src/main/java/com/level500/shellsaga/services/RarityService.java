package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.entities.RarityEntity;
import com.level500.shellsaga.infrastructure.database.RarityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RarityService {
    private final RarityRepository rarityRepository;

    @Autowired
    public RarityService(RarityRepository rarityRepository){
        this.rarityRepository = rarityRepository;
    }

    public double getRarityMultiplier(String rarityName){
        return rarityRepository.findByRarityName(rarityName)
                .map(RarityEntity::getStatMultiplier)
                .orElse(1.0);
    }

    public double getUpgradeChance(String rarityName){
        return rarityRepository.findByRarityName(rarityName)
                .map(RarityEntity::getUpgradeChance)
                .orElse(0.0);
    }

    public String getNextTier(RarityEntity rarity){
        return rarityRepository.findByRarityName(rarity.getRarityName())
                .map(RarityEntity::getNextTier)
                .orElse(rarity.getRarityName());
    }

    public RarityEntity getRarityByName(String name){
        return rarityRepository.findByRarityName(name).orElse(null);
    }
}
