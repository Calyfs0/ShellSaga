package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.entities.UpgradeMaterial;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.infrastructure.database.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * EquipmentEvolutionService:
 *  - All gear evolution logic (upgrade gear stats, itemLevel, rarity).
 *  - All final stat calculations (like computeAttack).
 */
@Service
public class EquipmentEvolutionService {

    private final RarityService rarityService;
    private final EquipmentRepository equipmentRepository;

    @Autowired
    public EquipmentEvolutionService(RarityService rarityService,
                                     EquipmentRepository equipmentRepository) {
        this.rarityService = rarityService;
        this.equipmentRepository = equipmentRepository;
    }

    /**
     * Upgrades base stats using an UpgradeMaterial (minor incremental upgrade).
     */
//    public Equipment upgradeEquipment(Equipment eq, UpgradeMaterial material) {
//        // check item level vs. material requirement
//        if (!eq.canApplyUpgrade(material.getLevelRequirement())) {
//            throw new IllegalStateException("Cannot apply this upgrade to " + eq.getName());
//        }
//        // apply stat upgrade
//        eq.getStats().applyUpgrade(material.getStatsBoost());
//
//        return equipmentRepository.save(eq);
//    }
//
//    /**
//     * Evolve gear's rarity using a "Core Exploit" or final boss drop (big jump).
//     */
//    public Equipment evolveRarity(Equipment eq, UpgradeMaterial coreExploit) {
//        if (!coreExploit.isCoreExploit()) {
//            throw new IllegalStateException("Only Core Exploits can evolve rarity.");
//        }
//
//        // next tier from RarityService
//        String nextTier = rarityService.getNextTier(eq.getRarity());
//
//        eq.setRarity(rarityService.getRarityByName(nextTier));
//
//        // big stat boost from exploit
//        eq.getStats().boostWithCoreExploit(coreExploit.getStatsBoost());
//
//        return equipmentRepository.save(eq);
//    }

    /**
     * Each gear evolution might do:
     *  - increment evolutionLevel
     *  - itemLevel = evolutionLevel * 10
     *  - random chance for rarity upgrade
     */
    public Equipment evolveGear(Equipment eq) {
        eq.setEvolutionLevel(eq.getEvolutionLevel() + 1);
        eq.setItemLevel(eq.getEvolutionLevel() * 10);

        double roll = Math.random();
        double chance = rarityService.getUpgradeChance(eq.getRarity().getRarityName());
        if (roll < chance) {
            String next = rarityService.getNextTier(eq.getRarity());
            if(next != null){
                eq.setRarity(rarityService.getRarityByName(next));
                System.out.println("✨ Rarity upgraded to: " + next);
            }
            else{
                System.out.println("✨ Rarity upgraded to: " + next);
            }

        }

        return equipmentRepository.save(eq);
    }

    /**
     * Example final computation: Attack = baseAttack * (itemLevel / 10) * rarityMult
     */
    public double computeAttack(Equipment eq) {
        double baseAttack = eq.getStats().getAttack() * (eq.getItemLevel() / 10.0);
        double rarityMult = rarityService.getRarityMultiplier(eq.getRarity().getRarityName());
        return baseAttack * rarityMult;
    }

    // Similarly, computeDefense(), computeSpellPower(), etc. if needed
}
