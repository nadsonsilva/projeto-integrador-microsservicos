package br.edu.ifam.microsservicos.eventos.dto;

public record SubscriptionResponseDTO(
    String subscriptionId, String eventId, String participantEmail,
    String message
) { }
