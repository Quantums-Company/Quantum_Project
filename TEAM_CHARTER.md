# Quantom Team Charter
![Quantom.png](src%2Fresources%2FQuantom.png)

## 1. Team Roles & Responsibilities
*   **Workflow Lead (Abdallah Jarwan):** Repository governance, branching model maintenance, and commit message enforcement.
*   **Clean Standards Lead (Zain ALajjouri):** Sets coding style guidelines, ensures adherence to Kotlin clean code principles, and manages automated linting configurations.
*   **Communication & SLA Lead (Marah Issa):** Oversees team communication protocols, manages meeting cadences, and ensures adherence to Service Level Agreements (SLAs).
*   **Architecture & Environment Lead (Hadeel Hejazy):** Manages project directory structure, dependency management, and configuration of environment ignores.

---

## 2. Abdallah Jarwan – Workflow

### 2.1 Branching Model: Feature-Branch Workflow
We use a centralized Feature-Branch model to ensure stable releases:
* **`main` Branch:** Protected. Merging is allowed only via validated Pull Requests.
* **`feature/` Branches:** All development must occur in branches named `feature/[task-name]`.
* **PR Requirement:** Every change must be submitted via a Pull Request (PR) and requires **two (2) peer approvals** before merging into `main`.

### 2.2 Commit Message Standards
All commits must follow the **Conventional Commits** specification: `type(scope): description`.
* **Types:** `feat` (new feature), `fix` (bug correction), `docs`, `style`, `refactor`, `test`, `chore`.
* **Example:** `feat(workflow): establish team roles and branching model`.

---

## 3. Zain ALajjouri – Clean Standards

### 3.1 Naming Conventions (Kotlin Standards)
* **Variables:** `camelCase` (descriptive names, e.g., `userRegistrationDate`).
* **Functions:** `camelCase` (verb-based, e.g., `calculateTotalScore()`).
* **Classes:** `PascalCase` (e.g., `SmartSafeLock`).
* **Constants:** `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_ATTEMPTS`).
* **Booleans:** Prefix with `is`, `has`, or `should`.

### 3.2 Function & Code Quality
* **Single Responsibility (SRP):** Each function performs one action; otherwise, split it.
* **Limit Parameters:** Max 3 parameters; wrap extra data in a `data class` within `org.bytebloom.org.bytebloom.domain.model.app.app.models/`.
* **Clean Layout:** 4-space indentation, max 120 chars per line, and logical whitespace.
* **Comments:** Explain the "Why", not the "What".

---

## 4. Marah Issa – Communication & SLAs

### 4.1 Communication Protocols
* **Meeting Cadence:** Daily syncs are set for **11:00 AM**, unless a critical circumstance prevents it.
* **Core Hours:** Team members are expected to be available for collaboration between **1:00 PM – 5:00 PM**.
* **Channels:** GitHub (Docs/PRs), WhatsApp (Syncs/Urgent), Google Meet (Meetings).

### 4.2 Service Level Agreements (SLAs)
* **Urgent Messages:** Response within 2 hours.
* **General Inquiries:** Response within 24 hours.
* **Pull Request Review:** Review required within **24 hours**.
* **Feedback Integration:** Requested changes addressed within **48 hours**.

### 4.3 Peer Review Checklist 
Every review must verify:
1. Code Structure/Naming.
2. IEEE-compliant documentation for complex functions.
3. Compliance with functional requirements.
---
## 5. Hadeel Hejazy – Architecture & .gitignore

### 5.1 Project Architecture
We follow a professional modular architecture separating business org.bytebloom.org.bytebloom.app.app.logic, data org.bytebloom.org.bytebloom.app.app.models, and entry points.

**Standard Directory Structure:**
```text
src/main/
├── kotlin/
│   └── org/
│       └── bytebloom/
│           ├── app/
│           │   └── Main.kt
│           ├── data/
│           │   ├── dataHolder/
│           │   │   └── DataHolder.kt
│           │   └── readers/
│           │       └── CsvDataLoader.kt
│           └── domain/
│               ├── logic/
│               │   ├── ConsistentHashingRing.kt
│               │   ├── DomainGraphBuilder.kt
│               │   ├── PricingStrategies.kt
│               │   ├── Printing.kt
│               │   ├── QuickSort.kt
│               │   ├── RingValidation.kt
│               │   └── Sorting.kt
│               └── model/
│                   └── Models.kt
└── resources/
    ├── fleet.csv
    ├── packages.csv
    ├── Quantum.png
    ├── quantum_architecture.png
    ├── routes.csv
    └── warehouses.csv
```
![quantom_architecture.png](src%2Fresources%2Fquantom_architecture.png)

**This charter is more than just a regulatory document; it is a professional commitment and a moral agreement among the members of Team Quantom. We believe that engineering excellence begins with discipline and precision. Therefore, we pledge to uphold these standards to ensure the delivery of our graduation project with the highest levels of quality and professionalism.**