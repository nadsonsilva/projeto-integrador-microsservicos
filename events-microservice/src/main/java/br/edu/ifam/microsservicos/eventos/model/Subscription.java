package br.edu.ifam.microsservicos.eventos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String participantEmail;

    public Subscription() { }

    public Subscription(Event event, String participantEmail) {
        this.event = event;
        this.participantEmail = participantEmail;
    }

    public String getId() { return id; }
    public Event getEvent() { return event; }
    public String getParticipantEmail() { return participantEmail; }
}
