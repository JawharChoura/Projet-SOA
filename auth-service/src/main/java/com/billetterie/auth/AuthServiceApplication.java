package com.billetterie.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Auth Service - Service d'authentification et autorisation
 * 
 * Responsabilités:
 * - Inscription des utilisateurs (TRAVELER, ADMIN)
 * - Connexion et émission de JWT
 * - Gestion des rôles et permissions
 * - Publication JWKS pour validation par autres services
 * 
 * Port: 8081
 * Base de données: PostgreSQL (auth)
 */
@SpringBootApplication
// Active le remplissage automatique des champs d'audit des entités persistées.
@EnableJpaAuditing
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
