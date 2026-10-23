# Milestone 14 — Gate A: Authenticated learner dashboard foundation

## Scope
Prepare a typed frontend API client for the existing, read-only synthetic learner self-service endpoint `GET /api/v1/learner/me/attempts`.

The client accepts an **access token provided by a future trusted authentication integration** and passes it only in the request Authorization header. No token is written to localStorage, sessionStorage, URLs, or source control. It does not accept a learner ID or organization ID: ownership is determined by the backend's validated JWT identity binding.

## Tests
Vitest checks bearer-header construction, rejection of empty credentials without network access, and safe 403 messaging. Existing catalog and demo flows are unchanged.

## Not yet implemented
This gate **does not provide sign-in, token acquisition, session renewal, logout, or a personalized dashboard screen**. Those require an approved identity provider and deliberate browser session design. Do not insert hardcoded, user-entered, or synthetic tokens into a production UI. The existing shared synthetic demo remains separate from private learner history.

## Acceptance
Review GitHub Actions results and merge only after checks pass. Subsequent gates will add secure authentication integration and the learner-specific interface with end-to-end authorization checks.
