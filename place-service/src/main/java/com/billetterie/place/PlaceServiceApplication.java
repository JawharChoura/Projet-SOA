package com.billetterie.place;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Place Service - Service gRPC de gestion des places disponibles
 * 
 * Responsabilités:
 * - Gérer le stock de places disponibles par trajet
 * - Bloquer des places (retenue de 10 minutes)
 * - Confirmer ou libérer les retenues
 * - Streaming serveur: diffuser en temps réel les changements de disponibilité
 * - Job planifié: libération automatique des retenues expirées
 * 
 * Port gRPC: 9090
 * Port HTTP (Actuator): 8083
 * Base de données: PostgreSQL (places)
 * 
 * Opérations gRPC:
 * - GetAvailability: Lire places disponibles
 * - HoldPlaces: Bloquer places
 * - ConfirmHold: Confirmer retenue
 * - ReleasePlaces: Libérer retenue
 * - WatchAvailability: Flux serveur (streaming)
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class PlaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaceServiceApplication.class, args);
    }
}
