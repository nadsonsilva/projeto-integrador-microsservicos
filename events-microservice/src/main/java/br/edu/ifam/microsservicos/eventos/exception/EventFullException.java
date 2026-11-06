package br.edu.ifam.microsservicos.eventos.exception;

public class EventFullException extends RuntimeException {
    public EventFullException(String id) {
        super("O evento " + id + " já atingiu o limite de participantes.");
    }
}
