# Services

## AnonymousLinkService

Created as a `@Bean` in `LinkServiceConfig`, **not** annotated `@Service`. Inject via constructor or `@Autowired` — Spring resolves it from `LinkServiceConfig`.

```java
// @PreAuthorize("hasRole('ANONYMOUS')")
Link createAnonymousLink(CreateLinkRequestDto dto)

Optional<String> getOriginalUrl(String shortLinkId)

// private
Link saveWithRetry(Link link)
```

**createAnonymousLink:** Delegates to `LinkFactory.createAnonymousLink(dto.url())`, then `saveWithRetry`.

**getOriginalUrl:** Calls `LinkRepository.findActiveLinkByShortLinkId` (enforces temporal validity). Returns `Optional<String>` of the original URL.

**saveWithRetry:** Attempts `linkRepository.save(link)` up to 5 times. Each retry calls `LinkFactory.createAnonymousLink()` again to get a new short code (the factory sets a new code on each call). On the 5th failure, throws `IllegalStateException("... after 5 attempts")`.

## AuthLinkService

Also a `@Bean` in `LinkServiceConfig`.

```java
// @Transactional
Link createUserLink(String username, CreateLinkRequestDto dto)

// @PreAuthorize hasPermission check
Optional<Link> getLinkForEdit(String linkId)

// @Transactional, @PreAuthorize
Optional<Link> updateUserLink(String linkId, UpdateLinkRequestDto dto)

// @Transactional
void deleteUserLink(String linkId)

// @PreAuthorize authentication.name == username
List<Link> getUserLinks(String username)
```

**createUserLink:** Creates `Link` via `LinkFactory.createUserLink(dto)`, calls `saveWithRetry`, then saves `LinkUser(username, shortLinkId)`. Both saves happen in a single transaction.

**getLinkForEdit:** Looks up link by id. The `@PreAuthorize` on this method delegates to `LinkPermissionsEvaluator.hasPermission()` — ownership + role check.

**updateUserLink:** Finds link, overwrites fields from dto, saves. Returns `Optional.empty()` if not found.

**deleteUserLink:** Deletes `LinkUser` first, then `Link`. Both in one transaction.

**saveWithRetry:** Same logic as in `AnonymousLinkService`. Uses `LinkFactory.createUserLink()` on retry (re-derives a new short code).

## LinkUsageService

Standard `@Service`.

```java
void logUsage(String shortLinkId)
List<LocalDateTime> getUsageTimestamps(String shortLinkId)
```

**logUsage:** Checks `linkUserRepository.existsByShortLinkId(shortLinkId)`. If false (anonymous link), returns immediately. Otherwise saves a `LinkUsage` row with `LocalDateTime.now()`.

**getUsageTimestamps:** Loads all `LinkUsage` records for the link, sorted ascending by `usedAt`. Strips nanoseconds via `.truncatedTo(ChronoUnit.SECONDS)` before returning — important because H2 stores with second precision and the chart needs clean values.

## LinkStatisticsService

Standard `@Service`.

```java
// @PreAuthorize authentication.name == username
LinkCountDto getLinkCount(String username)
```

Calls three repository methods in sequence:
- `countByUserNameIs(username)` → total
- `countActiveLinksByUserName(username)` → active (activeUntil in the future)
- `countExpiredLinksByUserName(username)` → expired (activeUntil in the past)

Returns a `LinkCountDto` record.

## LinkFactory (@Component)

Not a service, but produces entities.

```java
Link createAnonymousLink(String url)
Link createUserLink(CreateLinkRequestDto dto)
```

**createAnonymousLink:** Sets `originalUrl`, calls `ShortCodeGenerator.generate()` for `shortLinkId`, sets `createdAt = now()`, `activeUntil = now().plusDays(3)`.

**createUserLink:** Sets `originalUrl`, `description`, new short code, `createdAt`. Uses `dto.activeFrom()` if present, else null. Uses `dto.activeUntil()` if present, else `now().plusDays(5)`. Sets `updatedAt = null`.
