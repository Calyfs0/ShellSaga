package com.level500.shellsaga.api.dto;

import com.level500.shellsaga.core.game.characters.Playstyle;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class PlayerDTO {

    @NotBlank(message="Name cannot be empty")
    private String name;

    @NotNull(message = "Playstyle is required.")
    private Playstyle playstyle;
}