package com.level500.shellsaga.services;

import com.level500.shellsaga.config.ItemConstants;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.entities.STKEarningRule;
import com.level500.shellsaga.core.game.items.AllEquipments;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.core.game.items.InventoryItem;
import com.level500.shellsaga.infrastructure.database.PlayerRepository;
import com.level500.shellsaga.repositories.STKEarningRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final STKEarningRuleRepository stkEarningRuleRepository;
    private final GameClassService gameClassService;
    private final EquipmentService equipmentService;
    private final AllEquipmentService allEquipmentService;


    @Autowired
    public PlayerService(PlayerRepository playerRepository, STKEarningRuleRepository stkEarningRuleRepository, EquipmentService equipmentService, GameClassService gameClassService, EquipmentService equipmentService1, AllEquipmentService allEquipmentService) {
        this.playerRepository = playerRepository;
        this.stkEarningRuleRepository = stkEarningRuleRepository;
        this.gameClassService = gameClassService;
        this.equipmentService = equipmentService1;
        this.allEquipmentService = allEquipmentService;
    }

    public Player createPlayer(String name, String playstyle){
        if (playerRepository.existsByName(name)) {
            throw new IllegalArgumentException("Player name already exists!");
        }
        Player player = createNewPlayer(name, playstyle);
        addStarterEquipments(player);
        setStatsBasedOnEquipments(player);
        return playerRepository.save(player);
    }

    public Player createNewPlayer(String name, String playstyle) {
        if (playerRepository.existsByName(name)) {
            throw new IllegalArgumentException("Player name already exists!");
        }
        Player player = Player.builder()
                .name(name)
                .gameClass(gameClassService.findClassByClassName(playstyle))
                .level(1)
                .experience(0)
                .baseHp(100)
                .baseMana(50)
                .baseAttackPower(10)
                .baseDefense(5)
                .baseSpeed(5)
                .baseCriticalMultiplier(0)
                .baseCriticalRate(0)
                .enhancedHp(0)
                .enhancedMana(0)
                .enhancedAttackPower(0)
                .enhancedDefense(0)
                .enhancedSpeed(0)
                .enhancedCriticalMultiplier(0)
                .enhancedCriticalRate(0)
                .receivedDamage(0)
                .currentArea("Terminus")
                .equipmentList(new HashSet<>())
                .inventoryItems(new HashSet<>())
                .build();
        return playerRepository.save(player);
    }

    public Page<Player> getAllPlayers(Pageable pageable){
        return playerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Player> getPlayerById(Long playerId) {
        return playerRepository.findByIdWithInventoryAndEquipment(playerId);
    }

    public void savePlayer(Player player) {
        playerRepository.save(player);
    }

    public void respawnPlayer(Player player) {
        player.setCurrentArea("Terminus");
        player.setReceivedDamage(0);
        int xpLoss = player.getXp() / 2;
        player.setXp(Math.max(0, player.getXp() - xpLoss)); // ✅ Ensures XP does not become negative
        playerRepository.save(player);
    }


    // ✅ Fetch STK earning rules from the database
    public List<STKEarningRule> getSTKEarningRules() {
        return stkEarningRuleRepository.findAll();
    }

    // ✅ Award STK based on a specific condition
    public void awardSTK(Player player, String condition) {
        List<STKEarningRule> rules = stkEarningRuleRepository.findAll();

        for (STKEarningRule rule : rules) {
            if (rule.getCondition().equalsIgnoreCase(condition)) {
                addItemToPlayer(player.getId(), ItemConstants.STK, rule.getStkReward());
            }
        }
    }

    @Transactional
    public void addItemToPlayer(Long playerId, String itemName, int quantity) {
        Player player = playerRepository.findByIdWithInventoryAndEquipment(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        if (player.getInventoryItems() == null) { // ✅ Ensure list is initialized
            player.setInventoryItems(new HashSet<>());
        }

        Optional<InventoryItem> existingItem = player.getInventoryItems().stream()
                .filter(item -> item.getItemName().equalsIgnoreCase(itemName))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            InventoryItem newItem = new InventoryItem();
            newItem.setPlayer(player);
            newItem.setItemName(itemName);
            newItem.setQuantity(quantity);
            player.getInventoryItems().add(newItem);
        }

        playerRepository.save(player);
    }
    public void setStatsBasedOnEquipments(Player player){
        List<Equipment> equipmentList = equipmentService.getAllPlayerEquipments(player);

        for(Equipment equipment : equipmentList){
            addStats(equipment, player);
        }
    }

    public void addStarterEquipments(Player player){
        List<AllEquipments> allEquipments = allEquipmentService.getAllEquipmentsByClass(player.getGameClass());
        if(allEquipments == null){
            throw new IllegalStateException("Cannot find equipments!");
        }

        for(AllEquipments equipments : allEquipments){
            Equipment equipment = new Equipment();
            equipment.setRarity(equipments.getRarity());
            equipment.setPlayer(player);
            equipment.setName(equipments.getItemName());
            equipment.setItemLevel(equipments.getItemLevel());
            equipment.setStats(equipments.getStats());
            equipment.setGearSlot(equipments.getGearSlot());
            equipmentService.addEquipment(equipment);
        }

    }
//    public void equipItem(Equipment equipment, Player player) {
//        List<Equipment> equipmentList = equipmentService.getAllEquipments();
//        // ✅ First, unequip any existing gear in the same slot
//        equipmentList.stream()
//                .filter(e -> e.getSlot() == equipment.getSlot() && e.isEquipped()).findFirst().ifPresent(equippedEquipment -> {
//                    removeStats(equippedEquipment, player);
//                    equippedEquipment.setEquipped(false);
//                } );
//
//        // ✅ Equip the new item
//        equipment.setEquipped(true);
//        addStats(equipment, player);
//        equipmentService.updateEquipments(equipment);
//    }
//
//    public void unEquipItem(Equipment equipment, Player player) {
//        equipment.setEquipped(false); // Mark as unequipped
//        removeStats(equipment, player);
//        equipmentService.updateEquipments(equipment);
//        System.out.println("\n🛡️ Unequipped: " + equipment.getName());
//    }


    public void removeStats(Equipment equipment, Player player){
        player.setEnhancedAttackPower(player.getEnhancedAttackPower() - equipment.getStats().getAttack());
        player.setEnhancedDefense(player.getEnhancedDefense() - equipment.getStats().getDefense());
        player.setEnhancedHp(player.getEnhancedHp() - equipment.getStats().getHpIncrease());
        player.setEnhancedMana(player.getEnhancedMana() - equipment.getStats().getManaIncrease());
        player.setEnhancedCriticalRate(player.getEnhancedCriticalRate() - equipment.getStats().getCritRate());
        player.setEnhancedCriticalMultiplier(player.getEnhancedCriticalMultiplier() - equipment.getStats().getCritMultiplier());
        playerRepository.save(player);
    }

    public void addStats(Equipment equipment, Player player){
        player.setEnhancedAttackPower(player.getEnhancedAttackPower() + equipment.getStats().getAttack());
        player.setEnhancedDefense(player.getEnhancedDefense() + equipment.getStats().getDefense());
        player.setEnhancedHp(player.getEnhancedHp() + equipment.getStats().getHpIncrease());
        player.setEnhancedMana(player.getEnhancedMana() + equipment.getStats().getManaIncrease());
        player.setEnhancedCriticalRate(player.getEnhancedCriticalRate() + equipment.getStats().getCritRate());
        player.setEnhancedCriticalMultiplier(player.getEnhancedCriticalMultiplier() + equipment.getStats().getCritMultiplier());
        playerRepository.save(player);
    }

}
