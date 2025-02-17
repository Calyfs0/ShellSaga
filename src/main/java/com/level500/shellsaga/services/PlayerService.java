package com.level500.shellsaga.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.level500.shellsaga.config.ItemConstants;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.characters.Playstyle;
import com.level500.shellsaga.core.game.entities.STKEarningRule;
import com.level500.shellsaga.infrastructure.database.PlayerRepository;
import com.level500.shellsaga.repositories.STKEarningRuleRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final STKEarningRuleRepository stkEarningRuleRepository;

    public PlayerService(PlayerRepository playerRepository, STKEarningRuleRepository stkEarningRuleRepository) {
        this.playerRepository = playerRepository;
        this.stkEarningRuleRepository = stkEarningRuleRepository;
    }

    public Player createPlayer(String name, Playstyle playstyle){
        if (playerRepository.existsByName(name)) {
            throw new IllegalArgumentException("Player name already exists!");
        }
        Player player = Player.createNewPlayer(name, playstyle);
        return playerRepository.save(player);
    }

    public Page<Player> getAllPlayers(Pageable pageable){
        return playerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true) // ✅ Ensures Hibernate session is active
    public Optional<Player> getPlayerById(Long playerId){
        return playerRepository.findByIdWithInventory(playerId);
    }

    public void savePlayer(Player player) {
        if (player.getInventory() != null) {
            // Ensure inventory is properly serialized into JSON
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonInventory = objectMapper.writeValueAsString(player.getInventory());
                player.setInventory(objectMapper.readValue(jsonInventory, Map.class)); // Map example
            } catch (JsonProcessingException e) {
                throw new RuntimeException("❌ Failed to serialize inventory: " + e.getMessage(), e);
            }
        }

        playerRepository.save(player);
    }

    public void respawnPlayer(Player player){
        player.setCurrentArea("Terminus");
        player.setReceivedDamage(0);
        player.setXp(player.getXp() - player.getXp() / 2);
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
                player.addItem(ItemConstants.STK, rule.getStkReward());
                playerRepository.save(player);
            }
        }
    }

}
