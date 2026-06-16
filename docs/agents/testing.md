# Testing

## Test Infrastructure

**Spring Boot version:** 4.0.3. This version reorganized several test annotations — the details below are critical for writing new tests.

### Correct Imports (Spring Boot 4.x)

```java
// AutoConfigureMockMvc moved from spring-boot-test to spring-boot-webmvc-test
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

// @MockBean replaced by @MockitoBean
import org.springframework.test.context.bean.override.mockito.MockitoBean;
```

### @WithMockUser Does Not Work with @SpringBootTest

In Spring Boot 4.x, `@WithMockUser` does NOT propagate authentication through the real security filter chain when using `@SpringBootTest + @AutoConfigureMockMvc`. Tests annotated this way will receive 302 redirects instead of 200 responses.

**Fix:** Use `.with(user("john").roles("USER"))` inline on each MockMvc request:

```java
mockMvc.perform(get("/dashboard").with(user("john").roles("USER")))
```

Requires `import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;`

### @SpringBootTest Context Caching

All controller test classes declare the **same four `@MockitoBean` fields** so Spring can reuse one application context across all controller tests:

```java
@MockitoBean AnonymousLinkService anonymousLinkService;
@MockitoBean AuthLinkService authLinkService;
@MockitoBean LinkUsageService linkUsageService;
@MockitoBean LinkStatisticsService linkStatisticsService;
```

Do not add extra `@MockitoBean` fields unless all controller test classes declare them — context mismatch forces a new context per class.

### CSRF

POST requests require CSRF token in tests:

```java
mockMvc.perform(post("/create-link").with(csrf()).param("url", "https://example.com"))
```

## Unit Tests (Mockito, no Spring context)

These use `@ExtendWith(MockitoExtension.class)` and `@InjectMocks` / `@Mock`. No Spring context is loaded.

Files: `LinkTest`, `LinkFactoryTest`, `ShortCodeGeneratorTest`, `LinkPermissionsEvaluatorTest`, `AnonymousLinkServiceTest`, `AuthLinkServiceTest`, `LinkUsageServiceTest`, `LinkStatisticsServiceTest`

**GrantedAuthority lambda** in `LinkPermissionsEvaluatorTest`:
```java
GrantedAuthority authority = () -> "ROLE_USER";
when(authentication.getAuthorities()).thenAnswer(_ -> (Collection<GrantedAuthority>) List.of(authority));
```
The unnamed parameter `_` requires Java 21+. Project is on Java 25.

## Checkstyle Import Order (Google Java Style)

Rule: `CustomImportOrder`. Groups must be in this exact order with exactly one blank line between each group, and no blank lines within a group:

```java
// Group 1: STATIC — all import static lines together
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
                                          ← exactly one blank line
// Group 2: THIRD_PARTY_PACKAGE — ALL non-static imports together, NO blank lines within
import com.stavre.tinyurl.entity.Link;
import org.junit.jupiter.api.Test;
import java.util.List;                    ← java.* is in the same group, no blank line before it
```

**Common mistake:** Putting a blank line before `java.*` imports. This causes "Extra separation in import group" errors. `java.*`, `org.*`, and `com.*` are all in the same THIRD_PARTY_PACKAGE group.

## PMD Configuration

Custom ruleset at `pmd/pmd-rules.xml`. Uses `category/java/bestpractices.xml` with two rule adjustments:

- `UnitTestShouldIncludeAssert` — **excluded entirely.** PMD does not recognize MockMvc `.andExpect()` chains as assertions.
- `UnitTestContainsTooManyAsserts` — **re-included with `maximumAsserts=5`** (default of 1 is too strict).

If adding new rules, edit `pmd/pmd-rules.xml`, not `build.gradle`.

## PMD VariableDeclarationUsageDistance

Checkstyle rule `VariableDeclarationUsageDistance` allows at most 3 lines between a variable declaration and its first use. Variables declared in test setup that are only used in a lambda or assertion at the bottom of the method will trigger this.

**Fix:** Move the variable declaration immediately before the line that uses it.

Example fix in `AnonymousLinkServiceTest`:
```java
// BAD — dto declared 4 lines before use
CreateLinkRequestDto dto = new CreateLinkRequestDto(...);
Link link = new Link();
when(...).thenReturn(link);
when(...).thenThrow(...);
anonymousLinkService.createAnonymousLink(dto);  // ← distance = 4

// GOOD — dto declared 1 line before use
Link link = new Link();
when(...).thenReturn(link);
when(...).thenThrow(...);
CreateLinkRequestDto dto = new CreateLinkRequestDto(...);
anonymousLinkService.createAnonymousLink(dto);  // ← distance = 1
```

## Coverage

JaCoCo threshold: **70% line coverage** on `com.stavre.tinyurl.*`. Verified by `:jacocoTestCoverageVerification` (enabled in `build.gradle`). The threshold check runs as part of `./gradlew check`.

If coverage drops below 70%, the build fails. Add tests before removing any.

## Test File Locations

```
src/test/java/com/stavre/tinyurl/
├── authorization/     LinkPermissionsEvaluatorTest
├── controller/
│   ├── anonymous/     AnonymousCreateLinkControllerTest
│   ├── auth/          AuthCreateLinkControllerTest, AuthLinkControllerTest,
│   │                  DashboardControllerTest, LinkStatsControllerTest
│   └── common/        RedirectControllerTest
├── entity/            LinkTest
├── factory/           LinkFactoryTest
├── service/           AnonymousLinkServiceTest, AuthLinkServiceTest,
│                      LinkStatisticsServiceTest, LinkUsageServiceTest
└── util/              ShortCodeGeneratorTest
```
