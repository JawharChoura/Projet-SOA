package com.billetterie.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Booking Service - Service orchestrateur principal
 * 
 * Responsabilités:
 * - Gestion des voyageurs (profils)
 * - Gestion des réservations (EN_ATTENTE, CONFIRMEE, ANNULEE, EXPIREE)
 * - Émission des billets
 * - Orchestration: appels gRPC vers place-service et SOAP vers partner-service
 * - Job planifié: expiration automatique des réservations après 10 minutes
 * 
 * Port: 8082
 * Base de données: PostgreSQL (booking)
 * Intégrations: place-service (gRPC), partner-service (SOAP)
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
