# Car Rental — architecture microservices (Spring Boot / Spring Cloud)

[![CI](https://github.com/ridhaw/car-rental-microservices/actions/workflows/ci.yml/badge.svg)](https://github.com/ridhaw/car-rental-microservices/actions/workflows/ci.yml)

Application de location de voitures découpée en microservices : authentification JWT,
catalogue de voitures, locations et clients, derrière une API Gateway avec découverte
de services et configuration centralisée. L'ensemble démarre avec Docker Compose.

## Architecture

```
                 ┌──────────────────┐
  client ──────▶ │  gateway-service │ :8080   (Spring Cloud Gateway + filtre JWT)
                 └────────┬─────────┘
          ┌───────────────┼────────────────┐
          ▼               ▼                ▼
   auth-service     car-service      rental-service
      :8083            :8081             :8082
          │               │                │
          └───────────────┴──── MySQL 8 ───┘  (auth_db · car_db · rental_db)

   discovery-server (Eureka) :8761      config-server (Spring Cloud Config) :8888
```

| Service | Dossier | Rôle |
|---|---|---|
| Gateway | `Gateway/` | Point d'entrée unique, routage `lb://`, vérification du JWT |
| Auth | `auth-service/` | Inscription, connexion, émission et validation des JWT |
| Car | `car-rental-car-service/` | CRUD voitures, recherche par marque, couleur, année, prix |
| Rental | `rental-service/` | Locations, clients, planificateur |
| Discovery | `discovery-service/` | Registre Eureka |
| Config | `config-server/` | Configuration centralisée des services |

## Routes exposées par la Gateway

| Préfixe | Service |
|---|---|
| `/api/auth/**` | auth-service (`/login`, `/register`, `/validate`) |
| `/api/cars/**` | car-service |
| `/api/rentals/**` | rental-service |
| `/api/customers/**` | rental-service |
| `/api/scheduler/**` | rental-service |

## Stack

Java 17 · Spring Boot 3 · Spring Cloud Gateway · Eureka · Spring Cloud Config ·
Spring Security + JWT (jjwt) · Spring Data JPA · MySQL 8 · Docker Compose

## Intégration continue

À chaque push, GitHub Actions ([`ci.yml`](.github/workflows/ci.yml)) :

1. compile et teste les 6 services en parallèle, avec un conteneur MySQL 8 pour ceux qui ont une base de données ;
2. construit ensuite toutes les images Docker (`docker compose build`).

## Lancer le projet

```bash
cp .env.example .env        # puis mettre une vraie clé dans APP_JWT_SECRET
docker compose up --build
```

- Gateway : http://localhost:8080
- Tableau de bord Eureka : http://localhost:8761

La clé de signature JWT n'est pas dans le code : chaque service la lit dans la variable
d'environnement `APP_JWT_SECRET`. Pour lancer un service hors Docker, définir cette
variable avant `./mvnw spring-boot:run`.
