package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.characters.Player;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PlayerSessionService {
    private final RedisTemplate<String, Player> redisTemplate;
    private static final String SESSION_PREFIX = "player_session:";

    public PlayerSessionService(RedisTemplate<String, Player> redisTemplate){
        this.redisTemplate = redisTemplate;
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
        redisTemplate.opsForValue().set(getPlayerSessionKey(), player,2,TimeUnit.HOURS);
    }

    public Optional<Player> getPlayerSession(){
        Optional<Player> optionalPlayer = Optional.ofNullable(redisTemplate.opsForValue().get(getPlayerSessionKey()));
        if(optionalPlayer.isPresent()){
            Player player = optionalPlayer.get();
            System.out.println(player);
            player.getInventory().size();
        } else {
            System.out.println("No player session found.");
        }

        return optionalPlayer;
    }

    public void updateSession(Player player) {
        redisTemplate.opsForValue().set(getPlayerSessionKey(), player, Duration.ofHours(1)); // Store updated player data in Redis
    }

    public void removePlayerSession(){
        redisTemplate.delete(getPlayerSessionKey());
    }


}
