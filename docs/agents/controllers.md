# Controllers

## Common

### RedirectController

```
GET  /redirect/{shortUrl}   → resolves link, logs usage, 302 to original URL or /no-link-found
GET  /no-link-found         → renders no-link-found.html (permitAll — anonymous users land here on dead links)
```

`logUsage` is called even on a successful redirect. For anonymous links `logUsage` is a no-op (service checks `existsByShortLinkId`).

### GlobalControllerAdvice (`@ControllerAdvice`)

Adds two model attributes to every request:
- `currentPath` — `HttpServletRequest.getRequestURI()`
- `redirectBase` — injected from `app.redirect.base` property (e.g. `http://localhost:8080/redirect/`)

Used by templates to construct the full short URL and to highlight the active nav item.

## Anonymous

### AnonymousCreateLinkController

```
GET  /create-link       → auth check: if authenticated → auth-users/create-link.html
                                        else → anonymous-users/create-link.html
POST /create-link       → validates CreateLinkRequestDto
                          success → anonymous-users/display-short-link-page.html (model: link)
                          error   → anonymous-users/create-link.html (model: error)
```

URL validation: `@Valid` on `CreateLinkRequestDto`. Pattern `https?://.*` — must start with `http://` or `https://`.

### AuthController

```
GET /login → login.html
```

Spring Security handles the POST to `/login` itself.

## Authenticated

### AuthCreateLinkController

```
POST /create-link/auth  → @PreAuthorize on URL (security config: authenticated)
                          validates CreateLinkRequestDto
                          success → auth-users/display-short-link-page.html (model: link)
                          error   → auth-users/create-link.html (model: error)
```

Gets username from `Authentication.getName()` and passes it to `AuthLinkService.createUserLink()`.

### AuthLinkController

```
GET  /update-link/{linkId}   → getLinkForEdit (permission-checked), edit form or no-link-found
POST /update-link             → param: shortLinkId, validates UpdateLinkRequestDto
                               success → auth-users/display-short-link-page.html
                               error   → auth-users/edit-link.html (model: error, link)
POST /delete-link/{linkId}   → deleteUserLink, 302 /dashboard
```

On update validation failure: the controller re-fetches the link to repopulate the form (`getLinkForEdit(shortLinkId)` called again).

### DashboardController

```
GET /dashboard → model: linkCount (LinkCountDto), links (List<Link>)
               → auth-users/dashboard.html
```

### LinkStatsController

```
GET /link-stats/{shortLinkId} → getLinkForEdit (permission-checked)
                                 getUsageTimestamps
                                 model: link, timestamps (List<String>), totalUses (int)
                                 → auth-users/link-stats.html or no-link-found.html
```

`timestamps` is serialized as a JSON-safe list of ISO strings for Chart.js consumption.

## Templates

```
templates/
├── fragments.html                         — shared nav/footer fragments
├── login.html
├── no-link-found.html
├── anonymous-users/
│   ├── create-link.html
│   └── display-short-link-page.html
└── auth-users/
    ├── create-link.html                   — includes date pickers for activeFrom/activeUntil
    ├── display-short-link-page.html
    ├── dashboard.html
    ├── edit-link.html
    └── link-stats.html                    — Chart.js bar chart (hourly buckets, non-cumulative)
```

**link-stats chart:** Uses Chart.js with a `time` x-axis. Raw timestamps from the model are bucketed into 1-hour slots client-side. Renders as a bar chart (`type: 'bar'`) with `rgba(79,70,229,0.6)` fill.

## Static Assets

```
static/css/main.css          — full-width layout, no max-width on nav or container-wide
static/js/confirm.js         — delete confirmation dialogs
static/js/clipboard.js       — copy short link to clipboard
static/js/date-utils.js      — date formatting helpers
static/js/create-auth-link.js
static/js/edit-link.js
```

**Layout note:** `html, body` have `min-height: 100vh; overflow-x: hidden` to prevent Windows double-scrollbar. `.container-wide` is `width: 100%` with no `max-width`. `.container` is `max-width: 480px` (narrow forms only).
