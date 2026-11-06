package br.edu.ifam.microsservicos.eventos.repository;

import br.edu.ifam.microsservicos.eventos.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, String> {
    // Bloqueia a linha durante a inscrição para evitar ultrapassar o limite de vagas.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") String id);
}
