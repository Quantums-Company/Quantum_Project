# LogiRoute

**LogiRoute** is a logistics and fleet-management application developed by **Quantom Team**. It brings together warehouse and route management, package and vehicle operations, route analysis, shipment dispatch, pricing, fleet planning, classification, telemetry monitoring, and operational reporting.

The project is built primarily with **Kotlin**, using a layered architecture, coroutine-based asynchronous operations where appropriate, repositories for data access, and dependency injection to keep application components testable and maintainable.

> **Project status:** This README describes the repository structure and capabilities represented by the current codebase. Update feature details when implementation or supported launch flows change.

## Core capabilities

- **Warehouse, package, vehicle, and route management** through the presentation layer.
- **Route analysis** with shortest-route and fewest-hop workflows and graph-based routing.
- **Shipment dispatch** with dispatch modes/processors, shipment-state updates, and command-based operations.
- **Pricing and fleet planning** for logistics decisions.
- **Cargo optimization and classification**, including a Knapsack optimizer and K-nearest-neighbors components.
- **Telemetry workflows**, including simulated vehicle telemetry and telemetry search.
- **Operational analytics and reporting**, including system overview and warehouse reports.
- **Data access through CSV and remote sources**, including Supabase-backed repositories and resilience utilities.
- **Dependency injection** using Koin modules.

## Architecture

The repository separates business rules, data access, presentation orchestration, and user interfaces.

```text
src/main/kotlin/org/bytebloom/logiroute/
├── data/
│   ├── csv/             # CSV readers/writers, parsing, mapping, cache and repositories
│   ├── remote/          # Remote DTOs, mappers, Supabase repositories and resilience
│   ├── telemetry/       # Telemetry data sources and repository implementation
│   └── training/        # Training/history data support
├── di/                  # Koin dependency-injection modules
├── domain/
│   ├── command/         # Commands and command invoker
│   ├── dispatch/        # Dispatch processors, outcomes and shipment state updates
│   ├── model/           # Domain entities, inputs, results, validation and exceptions
│   ├── repository/      # Repository contracts
│   ├── usecase/         # Application/business actions
│   └── ...              # Routing, pricing, optimization, telemetry and other domain logic
├── presentation/
│   ├── presenter/       # Screen-facing orchestration
│   ├── screens/         # Presentation screen flows
│   └── ...              # Presentation models and shared presentation data
└── ui/                  # Terminal UI, widgets, tables and screen rendering

src/resources/
├── fleet.csv
├── packages.csv
├── routes.csv
├── warehouses.csv
├── Quantom.png
└── quantom_architecture.png

frontend/logiroute/       # Compose Multiplatform frontend module
```

Some directories contain additional feature-specific packages; the tree above is intentionally a high-level map rather than a complete file listing.

### Layer responsibilities

- **Domain:** owns business rules, domain models, repository contracts, use cases, validation, and domain-level exceptions. It should not depend on CSV, Supabase, terminal rendering, or frontend frameworks.
- **Data:** implements repository contracts and handles CSV/remote I/O, DTO mapping, caching, and data-source-specific concerns.
- **Presentation:** coordinates use cases for user-facing workflows and prepares data for screens. Presenter dependencies are grouped in presentation dependency data classes where that improves constructor clarity and avoids long parameter lists.
- **UI / Frontend:** renders user interactions and output. UI code should call presentation workflows rather than implementing business rules itself.
- **DI:** assembles implementations and application components through Koin modules.

## Technology

The current repository configuration includes:

- Kotlin/JVM with JDK 21 toolchain
- Kotlin Coroutines and Flow
- Supabase Kotlin client and Ktor CIO
- Kotlinx Serialization
- Koin for dependency injection
- Gradle Kotlin DSL
- JUnit 5, Kotlin Test, Truth, MockK, coroutine test utilities, Koin test, and Konsist
- Detekt for static analysis
- A Compose Multiplatform frontend module

Check the Gradle build files and version catalog for the exact versions and module-specific configuration.

## Build and verification

Run commands from the repository root.

```bash
# Run unit tests
./gradlew test

# Run static analysis
./gradlew detekt

# Run both checks
./gradlew test detekt

# Compile/package the JVM application
./gradlew build
```

A successful test run is required before a change is considered ready for review. If the build requires network access to resolve dependencies, make sure Gradle can access the configured repositories.

## Configuration and data

- CSV resources are stored under `src/resources/`.
- Remote data access is implemented in the `data/remote` area and configured through the DI modules.
- Keep credentials and private environment configuration out of source control. Use local environment configuration or the project's established secret-management approach; never commit Supabase keys or other credentials.
- Do not assume CSV and remote modes have identical behavior without checking the relevant repository/module configuration.

## Development workflow

Use the team's feature-branch and pull-request process.

1. Create a focused branch from the current integration branch.
2. Make a cohesive, reviewable change.
3. Add or update tests for behavior changes.
4. Run `./gradlew test detekt`.
5. Review `git diff` and `git status`.
6. Open a pull request with a clear summary, test results, and any known limitations.

### Commit messages

Use Conventional Commits:

```text
type(scope): short imperative description
```

Examples:

```text
feat(telemetry): add active-fleet delay notifications
fix(csv): protect cached snapshots from external mutation
refactor(presentation): group presenter use-case dependencies
test(routing): cover unreachable destinations
docs(readme): document current project architecture
```

## Team

LogiRoute is developed by **Quantom Team**. Team responsibilities, collaboration expectations, branch rules, commit conventions, and review practices are documented in [`TEAM_CHARTER.md`](TEAM_CHARTER.md).

## Project principle

Build logistics software that is understandable, testable, resilient, and maintainable. Prefer the simplest design that satisfies the requirement, keep business rules in the domain, isolate infrastructure concerns, and validate changes with tests rather than relying on assumptions.
