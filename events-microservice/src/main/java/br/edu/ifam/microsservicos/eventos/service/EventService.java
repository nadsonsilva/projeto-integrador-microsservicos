package br.edu.ifam.microsservicos.eventos.service;

import br.edu.ifam.microsservicos.eventos.client.EmailServiceClient;
import br.edu.ifam.microsservicos.eventos.dto.EmailRequestDTO;
import br.edu.ifam.microsservicos.eventos.dto.EventRequestDTO;
import br.edu.ifam.microsservicos.eventos.dto.SubscriptionRequestDTO;
import br.edu.ifam.microsservicos.eventos.dto.SubscriptionResponseDTO;
import br.edu.ifam.microsservicos.eventos.exception.EventFullException;
import br.edu.ifam.microsservicos.eventos.exception.EventNotFoundException;
import br.edu.ifam.microsservicos.eventos.model.Event;
import br.edu.ifam.microsservicos.eventos.model.Subscription;
import br.edu.ifam.microsservicos.eventos.repository.EventRepository;
import br.edu.ifam.microsservicos.eventos.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

@Service
public class EventService {
    private static final DateTimeFormatter FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final EventRepository eventRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailServiceClient emailClient;

    public EventService(EventRepository eventRepository,
                        SubscriptionRepository subscriptionRepository,
                        EmailServiceClient emailClient) {
        this.eventRepository = eventRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.emailClient = emailClient;
    }

    @Transactional(readOnly = true)
    public List<Event> listAll() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> listUpcoming() {
        LocalDate today = LocalDate.now();
        return eventRepository.findAll().stream()
            .filter(e -> !parseDate(e.getDate()).isBefore(today))
            .toList();
    }

    @Transactional
    public Event create(EventRequestDTO data) {
        parseDate(data.date()); // Garante data válida antes de gravar no H2.
        int registrations = data.registeredParticipants() == null ? 0 : data.registeredParticipants();
        if (registrations > data.maxParticipants()) {
            throw new IllegalArgumentException("Quantidade de inscritos acima da capacidade.");
        }
        Event event = new Event(
            data.maxParticipants(), registrations,
            data.date(), data.title(), data.description());
        return eventRepository.save(event);
    }

    @Transactional
    public SubscriptionResponseDTO subscribe(String eventId, SubscriptionRequestDTO request) {
        Event event = eventRepository.findByIdForUpdate(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getRegisteredParticipants() >= event.getMaxParticipants()) {
            throw new EventFullException(eventId);
        }

        Subscription subscription = subscriptionRepository.save(
            new Subscription(event, request.participantEmail()));
        event.setRegisteredParticipants(event.getRegisteredParticipants() + 1);
        eventRepository.save(event);

        // Comunicação síncrona HTTP entre microsserviços via Spring Cloud OpenFeign.
        emailClient.sendEmail(new EmailRequestDTO(
            request.participantEmail(),
            "Confirmação de inscrição: " + event.getTitle(),
            "Sua inscrição no evento '" + event.getTitle() + "' foi registrada com sucesso."
        ));

        return new SubscriptionResponseDTO(
            subscription.getId(), eventId, request.participantEmail(),
            "Inscrição realizada e notificação enviada ao serviço de e-mail.");
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, FORMAT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Data inválida: " + date, ex);
        }
    }
}
