package com.billetterie.partner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Partner Service - Service SOAP d'interface avec compagnie partenaire
 * 
 * Responsabilités:
 * - Rechercher les trajets proposés par la compagnie (SearchTrips)
 * - Confirmer une réservation chez le partenaire (ConfirmBooking)
 * - Annuler une réservation chez le partenaire (CancelBooking)
 * - Auditer tous les échanges SOAP (request/response XML)
 * - Mode SIMULATED: réponses depuis base locale avec latence simulée
 * - Mode REAL: appels vers système partenaire externe
 * 
 * Port: 8084
 * Endpoint SOAP: /ws/partner
 * WSDL: http://localhost:8084/ws/partner?wsdl
 * Base de données: PostgreSQL (partner)
 * 
 * Approche: Contract-First (XSD → WSDL → Code généré)
 */
@SpringBootApplication
@EnableJpaAuditing
public class PartnerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartnerServiceApplication.class, args);
    }
}
