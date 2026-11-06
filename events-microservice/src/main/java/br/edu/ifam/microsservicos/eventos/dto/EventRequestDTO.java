package br.edu.ifam.microsservicos.eventos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EventRequestDTO(
    @NotNull @Min(1) Integer maxParticipants,
    @Min(0) Integer registeredParticipants,
    @NotBlank String date,
    @NotBlank @Size(max = 255) String title,
    @NotBlank String description
) { }
