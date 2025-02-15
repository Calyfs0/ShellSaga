package com.level500.shellsaga.api.controllers;

import com.level500.shellsaga.api.dto.PlayerDTO;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/create")
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        Player newPlayer = playerService.createPlayer(playerDTO.getName(), playerDTO.getPlaystyle());
        return ResponseEntity.ok(new PlayerDTO(newPlayer.getName(), newPlayer.getPlayStyle()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<PlayerDTO> players = playerService.getAllPlayers(Pageable.ofSize(10)).stream()
                .map(p -> new PlayerDTO(p.getName(), p.getPlayStyle()))
                .toList();
        return ResponseEntity.ok(players);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
    }
}
