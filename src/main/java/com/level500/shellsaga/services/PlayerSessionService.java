package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.infrastructure.database.PlayerRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PlayerSessionService {
    private final RedisTemplate<String, Player> redisTemplate;
    private final RedisTemplate<String, Long> redisLongTemplate;
    private static final String SESSION_PREFIX = "player_session:";
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerSessionService(RedisTemplate<String, Player> redisTemplate, PlayerRepository playerRepository, RedisTemplate<String, Long> redisLongTemplate){
        this.redisTemplate = redisTemplate;
        this.playerRepository = playerRepository;
        this.redisLongTemplate = redisLongTemplate;
    }

    private String getPlayerSessionKey() {
        try {
            // ✅ Use system IP address as session key
            return SESSION_PREFIX + InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("❌ Failed to retrieve system IP address", e);
        }
    }

    public void storePlayerSession(Player player){
        Long playerId = player.getId();
        redisLongTemplate.opsForValue().set(getPlayerSessionKey(), playerId, 2, TimeUnit.HOURS);
        System.out.println("✅ Stored player ID: " + playerId + " in Redis.");
    }



    public Optional<Player> getPlayerSession() {
        Long playerId = redisLongTemplate.opsForValue().get(getPlayerSessionKey());
        if (playerId == null) {
            return Optional.empty();
        }
        // Now load from DB with collections fully fetched
        return playerRepository.findByIdWithInventoryAndEquipment(playerId);
    }

    public void updateSession(Player player) {
        redisLongTemplate.opsForValue().set(getPlayerSessionKey(), player.getId(), Duration.ofHours(1));
    }

    public void removePlayerSession(){
        redisTemplate.delete(getPlayerSessionKey());
    }


}
