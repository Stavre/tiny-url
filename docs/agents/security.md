# Security

## Authentication

- Form login at `/login`, success redirects to `/dashboard`
- Credentials stored in H2 `users` and `AUTHORITIES` tables
- `UserDetailsManagerConfig` wires `JdbcUserDetailsManager` with custom queries
- `BCryptPasswordEncoder` with strength 12

**Anonymous principal:** configured as `"guest"` with `ROLE_ANONYMOUS`. This is required so that `AnonymousLinkService.createAnonymousLink()` can be annotated `@PreAuthorize("hasRole('ANONYMOUS')")`.

## URL Authorization (SecurityFilterChainConfig)

```
/redirect/**          → permitAll
/no-link-found        → permitAll    ← added to fix bug: anonymous users on dead links were redirected to login
/create-link/**       → permitAll
/dashboard/**         → authenticated
/link-stats/**        → authenticated
POST /create-link/auth → authenticated
/update-link/**       → authenticated
/delete-link/**       → authenticated
/login                → permitAll
/css/**, /js/**       → permitAll
everything else       → denyAll
```

**H2 console:** CSRF disabled for `/h2-console/**`, frameOptions disabled so the iframe loads.

## Method-Level Security

`@EnableMethodSecurity` is on `SecurityFilterChainConfig`. Guards appear on repository and service methods via `@PreAuthorize`.

**Repository guards:**
- `LinkRepository.findUserLinks(username)` — `authentication.name == username`
- `LinkUserRepository.save(entity)` — `entity.userName == authentication.name`
- `LinkUserRepository.countByUserNameIs(username)` — `authentication.name == username`
- `LinkUserRepository.countActiveLinksByUserName(username)` — same
- `LinkUserRepository.countExpiredLinksByUserName(username)` — same

**Service guards:**
- `AnonymousLinkService.createAnonymousLink()` — `hasRole('ANONYMOUS')`
- `AuthLinkService.getLinkForEdit(linkId)` — `hasPermission(#linkId, 'link', 'ROLE_USER')`
- `AuthLinkService.updateUserLink(linkId, dto)` — `hasPermission(#linkId, 'link', 'ROLE_USER')`
- `AuthLinkService.getUserLinks(username)` — `authentication.name == username`

## LinkPermissionsEvaluator

Implements `PermissionEvaluator`. Used by `hasPermission(targetId, targetType, permission)` expressions.

**4-argument variant (the one Spring actually calls):**
```java
hasPermission(Authentication, Serializable targetId, String targetType, Object permission)
```
- Looks up `LinkUser` by `(username, shortLinkId)` — if not found, returns false
- Then checks `authentication.authorities` contains the required role string
- Returns true only if both checks pass

**3-argument variant** (`hasPermission(auth, domainObject, permission)`): always returns false. This is a Spring Security overload that must exist on the interface. Do not remove it even though it looks dead.

**`save()` override in `LinkUserRepository`** with `@PreAuthorize`: required to guard the save operation at the repository level. Do not remove even though it looks like it overrides JPA save unnecessarily.

> **Constraint from user:** "remove unused code but do not touch spring security overloads" — the 3-arg `hasPermission` and the `save()` override are intentionally kept.

## Expression Handler Bean

```java
@Bean
DefaultMethodSecurityExpressionHandler createExpressionHandler() {
    var handler = new DefaultMethodSecurityExpressionHandler();
    handler.setPermissionEvaluator(linkPermissionsEvaluator);
    return handler;
}
```

This wires `LinkPermissionsEvaluator` into the SpEL evaluation context so `hasPermission(...)` expressions work.
