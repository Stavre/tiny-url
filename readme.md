# TinyURL

A Spring Boot URL shortener with anonymous and authenticated user flows.

## Features

- **Anonymous users** — create short links that expire in 3 days
- **Authenticated users** — create short links with custom descriptions, activation windows, and a 5-day default expiration
- Usage tracking and per-link statistics with an hourly bar chart
- Dashboard with link counts (total / active / expired)

## Running the App

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`. The H2 in-memory database is seeded on startup with two test users and sample links.

**Test credentials:**

| Username | Password |
|----------|----------|
| john     | password |
| sam      | password |

H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, user: `admin`, password: `admin`)

## Configuration

Set `APP_BASE_URL` to change the redirect base in generated short links:

```bash
APP_BASE_URL=https://yourdomain.com ./gradlew bootRun
```

Defaults to `http://localhost:8080`.

## Quality Checks

```bash
./gradlew check          # tests + checkstyle + PMD + JaCoCo (>=70% coverage)
./gradlew test           # tests only
./gradlew checkstyleMain # main source style only
```

## Tech Stack

- Java 25, Spring Boot 4.0.3, Spring Security 7.x
- Thymeleaf, H2 (in-memory), Hibernate
- JUnit 5, Mockito, Spring Security Test
- Checkstyle (Google Java Style), PMD 7.16, JaCoCo
