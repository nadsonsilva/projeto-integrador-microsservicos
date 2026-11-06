package br.edu.ifam.microsservicos.eventos.service;

import br.edu.ifam.microsservicos.eventos.client.EmailServiceClient;
import br.edu.ifam.microsservicos.eventos.dto.EventRequestDTO;
import br.edu.ifam.microsservicos.eventos.dto.SubscriptionRequestDTO;
import br.edu.ifam.microsservicos.eventos.exception.EventFullException;
import br.edu.ifam.microsservicos.eventos.model.Event;
import br.edu.ifam.microsservicos.eventos.model.Subscription;
import br.edu.ifam.microsservicos.eventos.repository.EventRepository;
import br.edu.ifam.microsservicos.eventos.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock EventRepository eventRepository;
    @Mock SubscriptionRepository subscriptionRepository;
    @Mock EmailServiceClient emailClient;
    @InjectMocks EventService eventService;

    @Test
    void shouldCreateEvent() {
        EventRequestDTO input = new EventRequestDTO(30, 0, "30/12/2026", "Encontro", "Discussão sobre Java");
        when(eventRepository.save(any(Event.class))).thenAnswer(call -> call.getArgument(0));

        Event created = eventService.create(input);

        assertEquals("Encontro", created.getTitle());
        assertEquals(30, created.getMaxParticipants());
        assertEquals(0, created.getRegisteredParticipants());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldRejectFullEvent() {
        Event full = new Event(1, 1, "30/12/2026", "Encontro", "Lotado");
        when(eventRepository.findByIdForUpdate("1")).thenReturn(Optional.of(full));

        assertThrows(EventFullException.class,
            () -> eventService.subscribe("1", new SubscriptionRequestDTO("aluno@exemplo.com")));
        verifyNoInteractions(subscriptionRepository, emailClient);
    }

    @Test
    void shouldRegisterAndNotify() {
        Event event = new Event(10, 0, "30/12/2026", "Encontro", "Evento Tech");
        when(eventRepository.findByIdForUpdate("1")).thenReturn(Optional.of(event));
        when(subscriptionRepository.save(any(Subscription.class)))
            .thenAnswer(call -> call.getArgument(0));
        when(eventRepository.save(any(Event.class)))
            .thenAnswer(call -> call.getArgument(0));

        eventService.subscribe("1", new SubscriptionRequestDTO("aluno@exemplo.com"));

        assertEquals(1, event.getRegisteredParticipants());
        verify(emailClient, times(1)).sendEmail(any());
    }

    @Test
    void shouldReturnUpcomingEvents() {
        Event past = new Event(10, 0, "01/01/2020", "Passado", "Antigo");
        Event future = new Event(10, 0, "31/12/2099", "Futuro", "Próximo");
        when(eventRepository.findAll()).thenReturn(List.of(past, future));

        List<Event> upcoming = eventService.listUpcoming();

        assertEquals(1, upcoming.size());
        assertEquals("Futuro", upcoming.get(0).getTitle());
    }
}
