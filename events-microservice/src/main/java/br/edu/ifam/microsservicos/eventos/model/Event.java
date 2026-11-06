package br.edu.ifam.microsservicos.eventos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private Integer registeredParticipants;

    @Column(nullable = false)
    private String date;  // formato dd/MM/yyyy, como na aula

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    public Event() { }

    public Event(Integer maxParticipants, Integer registeredParticipants,
                 String date, String title, String description) {
        this.maxParticipants = maxParticipants;
        this.registeredParticipants = registeredParticipants;
        this.date = date;
        this.title = title;
        this.description = description;
    }

    public String getId() { return id; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public Integer getRegisteredParticipants() { return registeredParticipants; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public void setRegisteredParticipants(Integer registeredParticipants) {
        this.registeredParticipants = registeredParticipants;
    }
}
