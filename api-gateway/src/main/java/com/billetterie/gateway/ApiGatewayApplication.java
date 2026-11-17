package com.billetterie.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway - Point d'entrée unique pour tous les clients
 * 
 * Responsabilités:
 * - Routage des requêtes vers les microservices appropriés
 * - Validation des JWT émis par auth-service
 * - Gestion CORS
 * - Agrégation des health checks
 * - Rate limiting (future)
 * - Load balancing (future avec service discovery)
 * 
 * Port: 8080
 * Framework: Spring Cloud Gateway (WebFlux)
 * 
 * Routes configurées:
 * - /api/v1/auth/** → auth-service:8081
 * - /api/v1/trajets/** → booking-service:8082
 * - /api/v1/reservations/** → booking-service:8082 (JWT requis)
 * - /api/v1/voyageurs/** → booking-service:8082 (JWT requis)
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
