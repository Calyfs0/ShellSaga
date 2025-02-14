package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.characters.Playstyle;
import com.level500.shellsaga.infrastructure.database.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {


    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player createPlayer(String name, Playstyle playstyle){
        Player player = Player.createNewPlayer(name, playstyle);
        playerRepository.save(player);
        return player;
    }

    public List<Player> getAllPlayers(){
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerById(Long id){
        return playerRepository.findById(id);
    }
}
