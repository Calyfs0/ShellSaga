package com.level500.shellsaga.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor  // ✅ Ensure a no-args constructor exists
@AllArgsConstructor // ✅ This allows creating PlayerDTO(id, name, playstyle)
public class PlayerDTO {


    @NotBlank(message="Name cannot be empty")
    private String name;

    @NotNull(message = "Playstyle is required.")
    private Playstyle playstyle;

}