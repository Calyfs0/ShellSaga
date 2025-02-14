package com.level500.shellsaga.api.controllers;

import com.level500.shellsaga.api.dto.PlayerDTO;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/create")
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody PlayerDTO playerDTO){
        Player newPlayer = playerService.createPlayer(playerDTO.getName(), playerDTO.getPlaystyle());
        return ResponseEntity.ok(newPlayer);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

}
