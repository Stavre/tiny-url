# Architecture

## Overview

Standard Spring Boot MVC with a service layer. No REST API — all responses are Thymeleaf HTML views.

## Package Layout

```
com.stavre.tinyurl
├── authorization/     LinkPermissionsEvaluator
├── configuration/     SecurityFilterChainConfig, UserDetailsManagerConfig, LinkServiceConfig
├── controller/
│   ├── anonymous/     AnonymousCreateLinkController, AuthController (login page)
│   ├── auth/          AuthCreateLinkController, AuthLinkController, DashboardController, LinkStatsController
│   └── common/        RedirectController, GlobalControllerAdvice
├── dto/               CreateLinkRequestDto, UpdateLinkRequestDto, LinkCountDto
├── entity/            Link, LinkUser, LinkUsage
├── factory/           LinkFactory
├── repository/        LinkRepository, LinkUserRepository, LinkUsageRepository
├── service/           AnonymousLinkService, AuthLinkService, LinkUsageService, LinkStatisticsService
└── util/              ShortCodeGenerator
```

## Data Flow — Link Creation (Anonymous)

1. `GET /create-link` → `AnonymousCreateLinkController` → returns anonymous or auth form depending on auth status
2. `POST /create-link` → validates `CreateLinkRequestDto`
3. `AnonymousLinkService.createAnonymousLink()` → `LinkFactory.createAnonymousLink()` → sets 3-day expiry
4. `saveWithRetry()` — saves `Link`, retries up to 5× on `DataIntegrityViolationException` (short code collision)
5. Result view shown with generated short link

## Data Flow — Link Creation (Authenticated)

1. `POST /create-link/auth` → `AuthCreateLinkController`
2. `AuthLinkService.createUserLink(username, dto)` — `@Transactional`
3. `LinkFactory.createUserLink(dto)` — uses provided dates or 5-day default
4. Saves `Link`, then saves `LinkUser` (username ↔ shortLinkId mapping)
5. Result view shown

## Data Flow — Link Redirect

1. `GET /redirect/{code}` → `RedirectController`
2. `AnonymousLinkService.getOriginalUrl(code)` — `findActiveLinkByShortLinkId` checks temporal validity in SQL
3. `LinkUsageService.logUsage(code)` — saves `LinkUsage` row (only if `LinkUser` exists for this code)
4. 302 to original URL, or 302 to `/no-link-found`

## Key Design Decisions

**`AnonymousLinkService` and `AuthLinkService` are `@Bean`s, not `@Service`s.** They are created by `LinkServiceConfig` (@Configuration class). This matters for testing: `@MockitoBean` correctly overrides them because they are Spring-managed beans.

**Short code collision handling.** `ShortCodeGenerator` produces 6-char alphanumeric codes (62^6 ≈ 56 billion combinations). Collisions are practically impossible but handled: both services retry up to 5 times, throwing `IllegalStateException` if all attempts fail.

**Temporal validity lives in SQL.** `findActiveLinkByShortLinkId` uses a native query checking `activeFrom <= now AND activeUntil >= now` (nulls treated as unbounded). The `Link.isActive()` Java method mirrors this logic for test purposes.

**Usage tracking is opt-in by design.** `LinkUsageService.logUsage()` only records a row if `LinkUserRepository.existsByShortLinkId()` returns true. Anonymous links are never tracked.
