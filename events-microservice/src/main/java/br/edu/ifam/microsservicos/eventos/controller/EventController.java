package br.edu.ifam.microsservicos.eventos.controller;

import br.edu.ifam.microsservicos.eventos.dto.EventRequestDTO;
import br.edu.ifam.microsservicos.eventos.dto.SubscriptionRequestDTO;
import br.edu.ifam.microsservicos.eventos.dto.SubscriptionResponseDTO;
import br.edu.ifam.microsservicos.eventos.model.Event;
import br.edu.ifam.microsservicos.eventos.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getEvents() {
        return eventService.listAll();
    }

    @GetMapping("/upcoming")
    public List<Event> getUpcomingEvents() {
        return eventService.listUpcoming();
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @PostMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDTO> registerParticipant(
            @PathVariable String id, @Valid @RequestBody SubscriptionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.subscribe(id, request));
    }
}
