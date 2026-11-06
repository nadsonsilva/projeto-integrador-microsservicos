package br.edu.ifam.microsservicos.eventos.repository;

import br.edu.ifam.microsservicos.eventos.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> { }
