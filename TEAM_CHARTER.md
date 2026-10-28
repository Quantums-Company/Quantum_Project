

<img src="src/resources/Quantum.png" alt="Quantum Team Logo" width="598">

# QUANTUM TEAM

### LogiRoute — Team Charter, Engineering Standards & Architecture

*Build · Ship · Innovate*


---

## Purpose

This charter defines how Quantum Team collaborates on **LogiRoute**, how code changes are proposed and reviewed, and which engineering standards guide implementation. It is a shared working agreement; repository configuration and approved project decisions take precedence when they specify a more concrete rule.

## Team roles and responsibilities

- **Abdallah Jarwan — Workflow Lead:** repository governance, branching workflow, pull-request hygiene, and commit-message consistency.
- **Zain ALajjouri — Clean Standards Lead:** Kotlin conventions, clean-code practices, code-quality review, and static-analysis configuration.
- **Marah Issa — Communication & SLA Lead:** team communication, meeting coordination, follow-up, and review-response tracking.
- **Hadeel Hejazy — Architecture & Environment Lead:** package/module structure, dependency management, build configuration, and environment/ignore rules.

Roles establish ownership, not exclusive permission: all members are expected to review, test, document, and improve the project collaboratively.

---

## 1. Workflow and repository governance

### 1.1 Branching

- Keep `main` stable and merge changes through reviewed pull requests.
- Create task branches using the agreed naming convention, for example:
  - `feature/telemetry-delay-alerts`
  - `fix/csv-cache-safety`
  - `refactor/presenter-dependencies`
  - `docs/project-charter-refresh`
- Do not commit unfinished work directly to `main`.
- Before starting a task, sync with the current base branch and confirm that the branch contains the latest agreed changes.

### 1.2 Pull requests

Every pull request should include:

1. A concise problem statement and intended outcome.
2. A summary of the implementation and important design choices.
3. Tests and verification commands run, with their actual results.
4. Screenshots or sample output when UI behavior changes.
5. Known limitations, migration notes, or follow-up work.

The team agreement is to obtain **two peer approvals** before merging when repository settings and team availability support that rule. Required CI checks must pass; approvals do not replace tests.

### 1.3 Conventional Commits

Use:

```text
type(scope): short imperative description
```

Recommended types: `feat`, `fix`, `refactor`, `perf`, `test`, `docs`, `style`, `build`, and `chore`.

Examples:

```text
feat(routing): add fewest-hop route analysis
fix(csv): prevent callers from mutating cached snapshots
refactor(presentation): group presenter dependencies
test(telemetry): cover delay recovery transitions
docs(readme): document current application architecture
```

Keep each commit focused on one cohesive change. Avoid combining unrelated refactoring, UI changes, and bug fixes in a single commit.

---

## 2. Kotlin and clean-code standards

### 2.1 Naming and formatting

- Use `PascalCase` for classes, interfaces, objects, and enums.
- Use `camelCase` for functions, properties, and local variables.
- Use `UPPER_SNAKE_CASE` for constants.
- Prefer descriptive names and boolean prefixes such as `is`, `has`, or `should`.
- Follow the repository's Detekt configuration. Treat its configured limits as the source of truth for formatting and complexity.
- Keep comments focused on intent, constraints, and non-obvious decisions rather than restating the code.

### 2.2 Design principles

- **Single Responsibility:** each class and function should have a coherent purpose.
- **Dependency Inversion:** domain logic depends on domain contracts, not concrete infrastructure implementations.
- **Small interfaces and explicit boundaries:** introduce abstractions when they clarify a real boundary or enable meaningful substitution/testing, not automatically for every class.
- **Immutability by default:** prefer `val`, immutable views, and defensive copies at public boundaries when internal mutable state must be retained.
- **Functional collection operations:** use operations such as `map`, `filter`, `mapNotNull`, `groupBy`, `associateBy`, `any`, `all`, and `fold` when they make transformations clearer. Retain ordinary loops when an algorithm is inherently stateful, index-driven, or easier to understand imperatively.
- **Parameter clarity:** avoid long constructor parameter lists. Use a cohesive data class when a group of dependencies belongs together, such as presentation dependency bundles. Do not create a catch-all container that hides unrelated responsibilities.
- **No speculative layers:** do not add abstractions or architecture layers without a concrete requirement.

### 2.3 Exceptions and validation

- Use `require` or `check` for programmer-contract/precondition checks when their standard `IllegalArgumentException` or `IllegalStateException` semantics are intended.
- Use project-specific exceptions when a failure is part of a domain or application contract and callers need to distinguish it.
- Keep validation near the boundary that owns the rule; do not catch and suppress unexpected exceptions just to keep execution moving.
- Preserve useful context when translating infrastructure failures into application/domain-facing errors.
- Tests should assert both the outcome and the relevant failure behavior.

### 2.4 Coroutines and concurrency

- Mark functions `suspend` when they need to call suspending operations or expose a genuinely suspending contract. Do not add `suspend` merely for consistency.
- Use structured concurrency. Prefer `coroutineScope` and `async`/`await` when independent operations can safely run concurrently and their results are both required.
- Do not parallelize dependent operations or add concurrency where it does not improve the behavior.
- Keep blocking file/network operations on an appropriate dispatcher. Dispatcher choice should reflect the underlying operation and remain testable where practical.
- Preserve cancellation; avoid catching `CancellationException` as an ordinary failure.
- Protect shared mutable state at the owner boundary. A thread-safe collection alone does not make a multistep business operation atomic.
- Test concurrency-sensitive behavior deterministically where possible.

---

## 3. LogiRoute architecture standards

The current codebase is organized around the following responsibilities.

### 3.1 Domain

The `domain` area contains business models, use cases, repository contracts, routing and optimization logic, dispatch behavior, domain validation, and domain exceptions.

- Domain code must not depend on terminal UI, Compose, CSV implementation details, Supabase DTOs, or KOIN wiring.
- Use cases should represent meaningful application actions, not act as unnecessary wrappers around a single property access.
- Repository contracts belong on the domain side; concrete CSV and remote implementations belong in the data layer.
- Keep business decisions in domain/application logic rather than screen rendering code.

### 3.2 Data

The `data` area contains CSV and remote data access, data sources, DTOs, mappers, caching, telemetry implementations, and infrastructure resilience utilities.

- Keep CSV parsing and remote DTO mapping at the data boundary.
- Do not expose mutable cache collections directly to callers.
- Coordinate concurrent access to caches and file snapshots.
- Route remote operations through the project's shared resilience/execution policy where that policy applies.
- Never commit credentials or private configuration.

### 3.3 Presentation and UI

- `presentation/presenter` coordinates use cases and prepares state/data for user-facing workflows.
- `presentation/screens` contains screen-level presentation flows.
- `ui` contains terminal widgets, table formatting, and rendering behavior.
- Presenter dependencies may be grouped into focused data classes under the presentation `dependencies` package to keep constructors readable. Each bundle should contain dependencies for one presenter or one cohesive responsibility.
- Screens and widgets should not duplicate domain rules or perform direct infrastructure work.
- Keep formatting and display concerns out of domain models and use cases.

### 3.4 Dependency injection and frontends

- The `di` package assembles repositories, use cases, presenters, and infrastructure using KOIN modules.
- Keep object construction and environment-specific wiring in DI/composition-root code.
- The repository also contains a `frontend/logiroute` Compose Multiplatform module. Keep platform/UI concerns in the frontend module and shared business behavior behind appropriate application/domain boundaries.
- Update architecture documentation whenever package ownership or module boundaries materially change.

### 3.5 High-level repository map

```text
src/main/kotlin/org/bytebloom/logiroute/
├── data/
│   ├── csv/              # CSV I/O, parsing, mapping, cache, repositories
│   ├── remote/           # Supabase/remote DTOs, mappers, repositories, resilience
│   ├── telemetry/        # Telemetry data sources and repository implementation
│   └── training/         # Training/history data support
├── di/                   # Koin modules and composition wiring
├── domain/
│   ├── command/          # Command pattern and invoker
│   ├── dispatch/         # Dispatch workflows and state updates
│   ├── model/            # Domain entities, inputs, results, validation, exceptions
│   ├── repository/       # Repository contracts
│   └── use case/          # Application/business actions and feature packages
├── presentation/
│   ├── presenter/        # Presenter orchestration
│   ├── screens/          # Presentation screen flows
│   └── dependencies/     # Presenter dependency bundles (where present)
└── ui/                   # Terminal UI, shared tables, widgets and rendering

src/resources/             # CSV fixtures/resources and project images
frontend/logiroute/        # Compose Multiplatform frontend module
```

This is a conceptual map, not an exhaustive listing of every package.

---

## 4. Testing and quality gates

Before requesting review:

- Run `./gradlew test`.
- Run `./gradlew detekt`.
- Prefer `./gradlew test detekt` as the combined check when supported by the current build.
- Run `./gradlew build` for changes that affect compilation, dependency wiring, or module integration.
- Add regression tests for bug fixes and behavior changes.
- Test boundary cases, invalid input, empty collections, failure paths, cancellation, and concurrent access when relevant.
- Never report a check as passing unless it actually completed successfully. If a command cannot run because of a network or environment issue, state that explicitly in the pull request.

Reviewers should verify:
1. Functional correctness and edge cases.
2. Layer boundaries and dependency direction.
3. Naming, readability, and appropriate use of collection/functional APIs.
4. Error handling and exception semantics.
5. Mutability and concurrency safety.
6. Test quality and actual verification results.
7. Documentation and UI output where affected.

---

## 5. Communication and team SLAs

### 5.1 Communication

- **Channels:** GitHub for issues, code, pull requests, and documentation; WhatsApp for coordination and urgent matters; Google Meet for scheduled discussions.
- **Daily sync:** 11:00 AM, subject to team availability and urgent circumstances.
- **Collaboration window:** 1:00 PM–5:00 PM, subject to members' academic and personal commitments.
- Keep decisions that affect architecture, requirements, or workflow documented in the relevant GitHub issue, pull request, or project document so they remain discoverable.

### 5.2 Response targets

- Urgent messages: target response within 2 hours during availability.
- General questions: target response within 24 hours.
- Pull-request review: target first review within 24 hours.
- Requested follow-up changes: target completion or status update within 48 hours.

These are team coordination targets, not guarantees when a member is unavailable. Communicate delays early.

---

## 6. Definition of done

A task is ready to merge when:

- Acceptance criteria are met.
- The implementation follows the established architecture and avoids unrelated changes.
- Relevant tests have been added or updated.
- Required checks pass, or any environment limitation is explicitly documented.
- The pull request has the required reviews.
- Documentation, configuration, and sample output are updated when needed.
- No secrets, local-only files, generated build output, or unrelated changes are included.

---

## Team commitment

This charter is our shared commitment to building LogiRoute with discipline, transparency, and engineering judgment. We aim for software that is understandable, resilient, testable, and maintainable—not complexity for its own sake. We will communicate clearly, review one another's work respectfully, and prefer verified behavior over assumptions.


**Quantum Team — Together, We Build Better.**

