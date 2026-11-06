package br.edu.ifam.microsservicos.eventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SubscriptionRequestDTO(
    @NotBlank @Email String participantEmail
) { }
