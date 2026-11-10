<div>

<img src="src/resources/Quantum.png" alt="Quantum Team Logo" width="480">

# QUANTUM TEAM

### LogiRoute — Team Charter & Engineering Standards

*Build · Ship · Innovate*

</div>

---

## Purpose

This charter defines how Quantum Team works on **LogiRoute**: who owns what, how a change travels from an idea to `master`, and the engineering standards every contribution is reviewed against. It is a shared working agreement. Where the repository configuration (Detekt, CI, Konsist tests) states a more concrete rule, that configuration wins.

## Team and responsibilities

| Member              | Role                            | Owns                                                                               |
|---------------------|---------------------------------|------------------------------------------------------------------------------------|
| **Abdallah Jarwan** | Workflow Lead                   | Repository governance, branching, pull-request hygiene, commit-message consistency |
| **Zain ALajjouri**  | Clean Standards Lead            | Kotlin conventions, clean-code review, static-analysis configuration               |
| **Marah Issa**      | Communication & SLA Lead        | Team communication, meetings, follow-up, review-response tracking                  |
| **Hadeel Hejazy**   | Architecture & Environment Lead | Package and module structure, dependencies, build configuration, ignore rules      |
| **Noor**            | Contributor                     | Feature work, tests and reviews                                                    |

A role defines ownership, not exclusive permission. Everyone reviews, tests, documents and improves the project.

---

## 1. Workflow

### 1.1 Branches

`master` is always stable and only changes through a reviewed pull request. Work happens on short-lived branches named after the **type of change**, using the same three types as commits:

| Branch prefix | Use for                                             | Example                           |
|---------------|-----------------------------------------------------|-----------------------------------|
| `feature/`    | New behaviour, or a fix to behaviour that was wrong | `feature/fewest-hops-routing`     |
| `refactor/`   | Restructuring, renaming, clean-up, rule compliance  | `refactor/layer-boundaries`       |
| `test/`       | Adding or improving tests only                      | `test/dijkstra-unreachable-nodes` |

Rules:

- Branch from an up-to-date `master`; use lowercase words separated by hyphens.
- One branch, one purpose. Do not mix a feature and an unrelated cleanup.
- Never commit directly to `master`.

### 1.2 Commit messages

Every commit message has **one of exactly three prefixes**, followed by a short, lowercase, imperative description:

```text
refactor: <what was changed>
feature: <what was added or corrected in behaviour>
test: <what is now covered>
```

| Prefix      | Meaning                                                                                                                                                            |
|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `feature:`  | A user-visible capability is added, or incorrect behaviour is corrected                                                                                            |
| `refactor:` | The structure improves without a new capability: moving, renaming, extracting, removing dead code, applying the clean-code rules, documentation and build tidy-ups |
| `test:`     | Only tests are added or changed                                                                                                                                    |

Examples:

```text
feature: add fewest-hop route analysis
feature: stop classification from voting with the package being classified
refactor: group presenter use-case dependencies
refactor: replace object constants with companion objects
test: cover geohash neighbours and bounds
```

Guidelines:

- No scopes, no ticket numbers, no trailing period, at most about 72 characters.
- Describe **what** changed in the code, not the activity ("add", "move", "remove" — not "fixed stuff").
- One logical change per commit. A change and its tests may share a commit or be split into a `feature:`/`refactor:` commit followed by a `test:` commit.
- Never commit generated output, local IDE files or credentials.

### 1.3 Pull requests

Every pull request contains:

1. **Problem and outcome** — what was wrong or missing, and what is true now.
2. **Summary of the change** and any important design decision.
3. **Verification** — the commands that were run and their real results.
4. **Screenshots or sample output** when terminal output changes.
5. **Limitations or follow-ups**, if any.

Merge requires **two approvals** and a green CI run (`./gradlew detekt check`). Approvals never replace passing checks.

---

## 2. Architecture standards

LogiRoute uses **Clean Architecture** with four code layers and a composition root. The architecture diagram lives in [`README.md`](README.md).

```text
ui  ──►  presentation  ──►  domain  ◄──  data
                   di  wires all layers together
```

### 2.1 The dependency rule

Dependencies point inwards. Inner layers never import outer ones.

| Layer            | May depend on                             | Must never depend on                                                     |
|------------------|-------------------------------------------|--------------------------------------------------------------------------|
| **domain**       | nothing outside itself                    | data, presentation, ui, di, Koin, Supabase, Ktor, CSV, terminal, Compose |
| **data**         | domain                                    | presentation, ui                                                         |
| **presentation** | domain                                    | ui, data, di, Koin, `java.io`, Android / desktop APIs                    |
| **ui**           | presentation, domain (models for display) | use cases and repositories, data                                         |
| **di**           | every layer, to assemble them             | —                                                                        |

These rules are enforced by Konsist tests in `src/test/.../architecture`. A change that breaks a rule must fix the code, not weaken the test.

### 2.2 Responsibilities

- **Domain** — entities, validation, use cases, algorithms (routing, knapsack, KNN, pricing, geohash), the shipment state machine and repository **interfaces**. Business decisions live here and nowhere else.
- **Data** — implements the repository interfaces: CSV, Supabase, offline-first reading, caching, DTOs and mappers, resilience utilities.
- **Presentation** — presenters coordinate use cases and drive a `Screen` **interface**. No rendering, no formatting of terminal output, no business decisions, no state that belongs to the domain.
- **UI** — implements the `Screen` interfaces and only displays and collects input. It does not call use cases or repositories.
- **DI** — creates objects and connects them. No logic.

### 2.3 Patterns in use

MVP (presenter + screen interface), Repository, Strategy and Decorator (pricing), State (shipments), Template Method (dispatch), Observer through `Flow` (telemetry), Dependency Injection (KOIN). Add a new abstraction only when it clarifies a real boundary or removes real duplication.

### 2.4 Keeping documentation current

When a package moves, a layer rule changes or a module is added, update the README structure section and the architecture diagram in the same pull request.

---

## 3. Kotlin and clean-code standards

### 3.1 Naming and structure

- `PascalCase` for types, `camelCase` for functions and properties, `UPPER_SNAKE_CASE` for constants.
- Names describe intent. Booleans read as questions: `isEmpty`, `hasVehicle`, `shouldRetry`.
- Use-case classes end with `UseCase` and expose a single `operator fun invoke`. Validators end with `Validator`. DTOs end with `Dto` and are `@Serializable`.
- A function does one thing and stays short. Prefer few parameters; group dependencies that belong together into a small data class instead of passing a long list.
- Comments explain *why*. Delete commented-out code and unused code instead of keeping it.

### 3.2 Rules the team enforces

1. **Constants live in a `companion object`**, never as top-level `const` values and never as magic numbers or strings in logic.
2. **No `object` declarations.** Use a class (with a `companion object` for constants) so dependencies stay explicit and testable.
3. **No decisions by comparing strings.** Model alternatives with enums, sealed types or typed identifiers; normalize values once at the boundary.
4. **Typed errors.** Failures are specific exception types, not generic `RuntimeException` or `UnsupportedOperationException`. Presenters translate them to user messages by type.
5. **No `!!`.** Use `checkNotNull`, `requireNotNull` or safe calls with a meaningful message.
6. **No printing from domain or data code**, no `printStackTrace`, no `TODO` left in `master`.
7. **Immutability by default** — `val`, read-only collections, defensive copies at public boundaries.
8. **Functional collection operations** (`map`, `filter`, `groupBy`, `fold`, …) where they make the code clearer; a plain loop is fine for stateful algorithms.
9. **Remove duplication** when the same knowledge appears twice (for example the same constant or parsing rule in two places).

### 3.3 Exceptions and validation

- `require` / `check` for programmer-contract violations; project exceptions when callers must tell failures apart.
- Validate at the boundary that owns the rule. Never swallow an exception to keep running.
- Keep the original cause when translating an infrastructure failure into a domain error.

### 3.4 Coroutines and concurrency

- A function is `suspend` only when it calls suspending code.
- Use structured concurrency: `coroutineScope` with `async` for independent work whose results are all needed. Do not parallelize dependent steps.
- Run blocking file and network work on `Dispatchers.IO` once, at the edge that does the blocking.
- Never swallow `CancellationException`; always re-throw it.
- Protect shared mutable state at its owner. A thread-safe collection does not make a multistep operation atomic.

### 3.5 Secrets and configuration

Credentials come from environment variables (`URL`, `Key`). Never commit keys, tokens or personal configuration, and never log them.

---

## 4. Testing and quality gates

### 4.1 Conventions

- JUnit 5, Truth, MockK, `kotlinx-coroutines-test`, KOIN test and Konsist.
- Test names: ``should <expected behaviour> when <condition>``.
- Test bodies are divided into `// Given`, `// When` and `// Then`.
- Prefer small fakes over deep mock chains; test behavior, not implementation.
- Every bug fix comes with a test that fails without the fix.
- Cover boundaries, invalid input, empty collections, failure paths, cancellation and concurrent access where relevant.

### 4.2 Before requesting review

```bash
./gradlew test detekt     # must pass
./gradlew build           # when compilation, wiring or modules changed
```

Detekt runs with zero allowed issues and an empty baseline. Never report a check as passing unless it actually ran and passed; if the environment prevents a command, say so in the pull request.

### 4.3 Reviewer checklist

1. The behavior is correct, including edge cases.
2. The dependency rule holds and the change is in the right layer.
3. Names, size and readability follow section 3; the clean-code rules are respected.
4. Errors are typed and cancellation is preserved.
5. Shared state is safe; nothing mutable leaks across a boundary.
6. Tests are meaningful and follow the conventions.
7. Documentation and the diagram are updated when structure changed.
8. No credentials, generated files or unrelated edits.

---

## 5. Communication and service levels

### 5.1 Channels and rhythm

| Purpose                                    | Channel     |
|--------------------------------------------|-------------|
| Code, pull requests, issues, documentation | GitHub      |
| Coordination and urgent matters            | WhatsApp    |
| Planned discussions                        | Google Meet |

- **Daily sync:** 11:00 AM.
- **Collaboration window:** 1:00 PM – 5:00 PM.
- Decisions about architecture, requirements or workflow are written in the relevant pull request, issue or document so they stay discoverable.

### 5.2 Response targets

| Situation                      | Target                                    |
|--------------------------------|-------------------------------------------|
| Urgent message                 | within 2 hours                            |
| General question               | within 24 hours                           |
| First review of a pull request | within 24 hours                           |
| Requested changes              | done, or a status update, within 48 hours |

These are coordination targets, not guarantees. Tell the team early when you will be unavailable.

---

## 6. Definition of done

A task is done when:

- The acceptance criteria are met and nothing unrelated was changed.
- The change sits in the correct layer and respects the dependency rule.
- Tests were added or updated, and `./gradlew test detekt` passes.
- The pull request has two approvals and a green CI run.
- The README and diagram are updated if structure or behavior changed.
- No secrets, local files or generated output are included.

---

## Team commitment

We build LogiRoute with discipline and honesty: simple designs over clever ones, business rules in the domain, infrastructure at the edges, and verified behavior over assumptions. We review each other's work respectfully and say early when something is blocked.

<div >

**Quantum Team — Together, We Build Better.**

</div>
