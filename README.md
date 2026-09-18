# Quantum Project — Error Handling Strategy

## Chosen strategy: Strategy B (Legacy Exception Style)

We adopt Strategy B across the codebase:

1. Catch low-level Supabase / PostgREST / HTTP errors at the **repository boundary** (`data` layer).
2. Translate them into typed **domain exceptions**.
3. Let use cases propagate domain exceptions (and throw `EntityValidationException` on validation failure).
4. Catch domain exceptions at the **CLI boundary** (`Main.kt`) and print clear messages.

## Why not Strategy A (Kotlin Result)?

- Our repository interfaces already return domain values directly (`Warehouse`, `List<Route>`, …).
- CRUD use cases already use exception-style failure for invalid input.
- `Main` already uses `try/catch`.
- Switching everything to `Result` would force a large breaking rewrite of every interface and caller.

## Domain exception hierarchy

- `DomainException` (sealed base)
- `NetworkUnavailableException`
- `EntityValidationException`
- `ResourceNotFoundException`
- `DatabaseConflictException`
- `UnknownDataException` (fallback for unexpected data/SDK failures)

## Architectural rationale (Clean Architecture)

- Third-party SDK types must not leak into `domain` or `presentation` (Clean Code: Boundaries).
- Repositories own infrastructure failures; domain owns business/validation failures.
- Presentation only formats known domain errors for the user.