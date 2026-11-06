package br.edu.ifam.microsservicos.eventos.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String id) {
        super("Evento não encontrado: " + id);
    }
}
