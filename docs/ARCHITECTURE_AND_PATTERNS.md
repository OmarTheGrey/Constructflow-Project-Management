# ConstructFlow — Architecture, SOLID Compliance & Design-Pattern Implementation

> A complete map of the ConstructFlow codebase documenting the ten design patterns that were introduced into the Java backend across two phases — the creational/structural pass (**Adapter**, **Factory Method**, **Abstract Factory**, **Iterator**, **Composite**) and the behavioural pass (**Template Method** ×2, **Mediator** ×2, **Strategy** ×2, **Observer + Singleton**) — and how each one closed a specific SOLID violation or opened a specific extension point.

---

## Table of Contents

1. [Repository Layout](#1-repository-layout)
2. [Tech Stack](#2-tech-stack)
3. [Java Backend — Structure](#3-java-backend--structure)
4. [Domain Model (Entities)](#4-domain-model-entities)
5. [Repositories](#5-repositories)
6. [Services](#6-services)
7. [Controllers & REST API](#7-controllers--rest-api)
8. [DTOs](#8-dtos)
9. [Configuration & Cross-Cutting](#9-configuration--cross-cutting)
10. [Frontend Overview](#10-frontend-overview)
11. [SOLID Audit — Violations Addressed](#11-solid-audit--violations-addressed)
12. [Design-Pattern Implementation](#12-design-pattern-implementation)
    - [Phase 1 — Creational & Structural](#phase-1--creational--structural)
      - [12.1 Adapter](#121-adapter--document-storage-decoupled)
      - [12.2 Factory Method](#122-factory-method--entity-construction)
      - [12.3 Abstract Factory](#123-abstract-factory--report-families)
      - [12.4 Iterator](#124-iterator--lazy-repository-traversal)
      - [12.5 Composite](#125-composite--task-hierarchy--progress-aggregation)
    - [Phase 2 — Behavioural](#phase-2--behavioural)
      - [12.6 Template Method — Document Export](#126-template-method--document-export)
      - [12.7 Template Method — Allocation Validation](#127-template-method--allocation-validation)
      - [12.8 Mediator — Resource-Allocation Workflow](#128-mediator--resource-allocation-workflow)
      - [12.9 Mediator — Announcement Discussion Room](#129-mediator--announcement-discussion-room)
      - [12.10 Strategy — Project-Progress Calculation](#1210-strategy--project-progress-calculation)
      - [12.11 Strategy — Critical-Task Prioritisation](#1211-strategy--critical-task-prioritisation)
      - [12.12 Observer + Singleton — Global Activity Hub](#1212-observer--singleton--global-activity-hub)
13. [Final Package Structure](#13-final-package-structure)
14. [Testing](#14-testing)

---

## 1. Repository Layout

```
Constructflow-Project-Management/
├── backend/                         # Spring Boot 3.2.1 Java backend
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/constructflow/
│       │   ├── BackendApplication.java
│       │   ├── config/              # WebConfig, AppProperties, StorageProperties
│       │   ├── controller/          # 9 REST controllers
│       │   ├── dto/                 # 17 request/response DTOs
│       │   ├── exception/           # GlobalExceptionHandler + 3 domain exceptions
│       │   ├── model/               # 11 JPA entities + BaseEntity
│       │   │   └── work/            # Composite pattern nodes
│       │   ├── repository/          # 11 Spring Data repositories
│       │   └── service/             # Business logic + sub-packages
│       │       ├── events/          # TaskMutatedEvent, ProgressRecalculator
│       │       ├── factory/         # Factory Method + Abstract Factory (report/)
│       │       ├── iteration/       # Iterator implementations
│       │       ├── mapping/         # XxxMapper components
│       │       ├── mediator/        # Mediator: allocation/ + discussion/
│       │       ├── observer/        # Observer + Singleton: ActivityHub
│       │       ├── storage/         # Adapter: DocumentStorage port + adapters
│       │       ├── strategy/        # Strategy: progress/ + prioritisation/
│       │       └── template/        # Template Method: export/ + allocation/
│       └── test/java/.../observer/  # ActivityHubTest (singleton + observer invariants)
├── construct-flow-nextjs-frontend/  # Next.js 16 / React 19 / TypeScript
├── docs/                            # ERDs, setup docs, UML, this file
│   └── uml/                         # 8 PlantUML sources + rendered PNGs
├── README.md
└── LICENSE
```

---

## 2. Tech Stack

| Layer | Technology |
|---|---|
| Backend framework | Spring Boot 3.2.1 |
| Language | Java 17 (pom.xml) / 21 (README) |
| ORM | Hibernate via Spring Data JPA |
| Database | Microsoft SQL Server 2019+ |
| Build | Maven |
| Validation | Jakarta Bean Validation |
| Boilerplate | Lombok (`@RequiredArgsConstructor`, `@Data`, …) |
| Testing | JUnit 5 (via `spring-boot-starter-test`) |
| UML | PlantUML (sources + rendered PNGs in `docs/uml/`) |
| Frontend | Next.js 16, React 19, TypeScript 5, TailwindCSS 4, Radix UI, Axios, React Hook Form, Zod, Recharts |

---

## 3. Java Backend — Structure

Entry point: [backend/src/main/java/com/constructflow/BackendApplication.java](../backend/src/main/java/com/constructflow/BackendApplication.java)

```
com.constructflow
├── BackendApplication          # @SpringBootApplication + @EnableJpaAuditing + @EnableConfigurationProperties
├── config.WebConfig            # CORS (driven by AppProperties)
├── config.AppProperties        # @ConfigurationProperties — status names, CORS origins
├── config.StorageProperties    # @ConfigurationProperties — storage.local-path
├── controller.*                # REST layer (no @CrossOrigin — handled globally)
├── dto.*                       # API contracts
├── exception.*                 # GlobalExceptionHandler + domain exceptions
├── model.*                     # JPA entities
├── model.work.*                # WorkItem, LeafTask, CompositeTask, WorkItemVisitor  (Composite)
├── repository.*                # JpaRepository interfaces
└── service
    ├── events.*                # TaskMutatedEvent, ProgressRecalculator
    ├── factory.*               # EntityFactory + TaskFactory, ProjectFactory, ResourceFactory
    ├── factory.report.*        # ReportArtifactFactory family (Abstract Factory)
    ├── iteration.*             # PagedRepositoryIterator, ProjectScanner, ProjectTaskTreeIterator
    ├── mapping.*               # 9 XxxMapper components
    ├── mediator.allocation.*   # AllocationMediator + 4 colleagues
    ├── mediator.discussion.*   # AnnouncementRoom + 3 participants
    ├── observer.*              # ActivityHub (enum singleton) + 3 observers + Activity variants
    ├── storage.*               # DocumentStorage (port) + LocalFileSystemStorageAdapter
    ├── strategy.progress.*     # ProgressStrategy + 4 implementations
    ├── strategy.prioritisation.*  # PrioritisationStrategy + 4 implementations
    ├── template.export.*       # AbstractDocumentExporter + PDF/CSV/ZIP
    └── template.allocation.*   # AbstractAllocationValidator + Material/Equipment/Labor
```

---

## 4. Domain Model (Entities)

All entities extend [BaseEntity](../backend/src/main/java/com/constructflow/model/BaseEntity.java) which provides:
- `UUID id` (generated)
- `LocalDateTime createdAt` (`@CreatedDate`)
- `LocalDateTime lastModifiedAt` (`@LastModifiedDate`)
- `Long version` (optimistic locking via `@Version`)

| Entity | Key fields | Relationships |
|---|---|---|
| **Project** | name, client, location, budget, actualCost, startDate, endDate, progress, status, objectives, milestones, **progressModel** (new — enum `ProgressModel`) | 1..N Tasks, Resources, Documents, Stakeholders, DailyReports |
| **Task** | name, projectId, **parentTaskId** (new), assignee, dueDate, status, priority, description, actualCost, dependencies | belongs to Project; self-referential parent/child; 1..N DailyLogs, WorkLogs, TaskAllocations |
| **Resource** | name, category (Material/Equipment/Labor), quantity, unit, allocationPercentage, cost, projectId | 1..N TaskAllocations |
| **TaskAllocation** | taskId, resourceId, quantityAllocated, allocatedAt, notes | join entity |
| **DailyReport** | projectId, activities, issues, completionPercentage, photos, submittedBy | belongs to Project |
| **DailyLog** | taskId, logEntry, dateCreated | belongs to Task |
| **WorkLog** | taskId, hours, notes, date, submittedBy | belongs to Task |
| **Announcement** | title, content, priority, datePosted, author | 1..N Comments |
| **AnnouncementComment** | announcement (`@ManyToOne LAZY`), author, content | belongs to Announcement |
| **Document** | name, type, folder, uploadDate, size, **storageKey** (new), projectId | belongs to Project |
| **Stakeholder** | name, role, company, email, phone, projectId | belongs to Project |

---

## 5. Repositories

All under [backend/src/main/java/com/constructflow/repository/](../backend/src/main/java/com/constructflow/repository/). Each extends `JpaRepository<Entity, UUID>`. Highlights:

- `ProjectRepository` — `findByStatus`, aggregate queries (`getTotalBudget`, `getAverageActualCost`), sub-query `findHighValueProjects`.
- `TaskRepository` — `findByProjectId`, `findByPriority`, `findCriticalTasks`, `findExpensiveTasks` (sub-query).
- `ResourceRepository` — `findResourcesInActiveProjects` (multi-join).
- `DailyLogRepository` — `findLogsByProjectLocation`, `findTasksUsingResourceCategory` (4-table joins).
- `WorkLogRepository` — `findByTaskId` (used by the effort-based progress strategy).

---

## 6. Services

All `@Service` + `@RequiredArgsConstructor`. Each service was refactored to inject dedicated mappers, factories, resolvers, or mediators rather than embed mapping, construction, or orchestration logic inline.

- [ProjectService.java](../backend/src/main/java/com/constructflow/service/ProjectService.java) — CRUD; `updateProjectProgress(UUID)` delegates progress to the resolved `ProgressStrategy` (Pattern 12.10) and cost to `TaskTreeBuilder` (Composite, 12.5).
- [TaskService.java](../backend/src/main/java/com/constructflow/service/TaskService.java) — CRUD; publishes `TaskMutatedEvent` via `ApplicationEventPublisher`; `getCriticalTasks(sortKey)` delegates ordering to the resolved `PrioritisationStrategy` (12.11); publishes `ActivityHub` events on create/complete (12.12).
- [ResourceService.java](../backend/src/main/java/com/constructflow/service/ResourceService.java) — CRUD; `allocateResource(...)` is a single line that delegates to `AllocationMediator` (12.8), which internally uses the allocation Template Method (12.7) and the ActivityHub (12.12).
- [DocumentService.java](../backend/src/main/java/com/constructflow/service/DocumentService.java) — delegates all byte-level I/O to the injected `DocumentStorage` port (Adapter, 12.1); correctly deletes the physical file on `deleteDocument`.
- [GlobalReportService.java](../backend/src/main/java/com/constructflow/service/GlobalReportService.java) — uses `ProjectScanner` and `ProjectTaskTreeIterator` (Iterator, 12.4) for O(1)-memory traversal; reads status constants from `AppProperties`.
- [ReportService.java](../backend/src/main/java/com/constructflow/service/ReportService.java) — dispatcher that resolves a `ReportArtifactFactory` by `ReportKind` (Abstract Factory, 12.3).
- [AnalyticsService.java](../backend/src/main/java/com/constructflow/service/AnalyticsService.java) — uses `PagedRepositoryIterator` for memory-safe streaming.
- [AnnouncementCommentService.java](../backend/src/main/java/com/constructflow/service/AnnouncementCommentService.java) — now a thin forwarder onto `DiscussionRoomMediator` (12.9).
- [TaskTreeBuilder.java](../backend/src/main/java/com/constructflow/service/TaskTreeBuilder.java) — assembles `WorkItem` composite trees from flat task rows (Composite, 12.5).
- Other services (DailyReport, DailyLog, WorkLog, Announcement, Stakeholder, Search) — thin CRUD wrappers using injected mappers.

---

## 7. Controllers & REST API

All per-controller `@CrossOrigin` annotations were removed; CORS is handled globally in `WebConfig`.

| Controller | Base path | Notable endpoints |
|---|---|---|
| ProjectController | `/api/projects` | GET list (paged), GET/POST/PUT/DELETE by id |
| TaskController | `/api/tasks` | `/project/{id}`, `/critical`, **`/critical?sortBy={DUE_DATE\|COST_DESC\|RISK\|DEPENDENCIES}`** (Strategy, 12.11) |
| ResourceController | `/api/resources` | `POST /allocate` (Mediator, 12.8), `POST /{id}/inventory` |
| AnnouncementController | `/api/announcements` | nested `/{id}/comments` (Mediator, 12.9) |
| AnalyticsController | `/api/analytics` | `/dashboard`, `/advanced` |
| DailyReportController | `/api/daily-reports` | `/project/{id}` |
| DocumentController | `/api/documents` | upload (multipart), **`GET /{id}/export?format={PDF\|CSV\|ZIP}`** (Template Method, 12.6) |
| StakeholderController | `/api/stakeholders` | CRUD |
| SearchController | `/api/search` | cross-entity search |
| ReportController | `/api/reports` | `GET /summary`, **`GET /{kind}`** (EXECUTIVE/PROJECT/FINANCIAL; Abstract Factory, 12.3) |

---

## 8. DTOs

Under [backend/src/main/java/com/constructflow/dto/](../backend/src/main/java/com/constructflow/dto/). Split into `*RequestDTO` (input, validated with Jakarta annotations) and `*ResponseDTO` (output). DTO construction was moved entirely out of services into dedicated mapper components (`service/mapping/`). Two in-package records support the new features: `ExportRequest`/`ExportResult` (in `service/template/export/`) and `AllocationCommand`/`AllocationRequest` (in `service/mediator/allocation/` and `service/template/allocation/`).

---

## 9. Configuration & Cross-Cutting

- **CORS**: [WebConfig.java](../backend/src/main/java/com/constructflow/config/WebConfig.java) reads allowed origins from `AppProperties` (`app.cors.allowed-origins`). The wildcard `"*"` origin was removed.
- **AppProperties**: `@ConfigurationProperties(prefix = "app")` exposes CORS origins and the three canonical status strings (`active`, `in-progress`, `completed`) so they can be changed without touching Java code.
- **StorageProperties**: `@ConfigurationProperties(prefix = "storage")` exposes `local-path` for the filesystem adapter.
- **Exception handling**: [GlobalExceptionHandler.java](../backend/src/main/java/com/constructflow/exception/GlobalExceptionHandler.java) now handles `ResourceNotFoundException` (404), `InsufficientResourceException` (422), `DomainValidationException` (400), and `MethodArgumentNotValidException` (400) in addition to the catch-all `RuntimeException` (500).
- **Auditing**: `@EnableJpaAuditing` confirmed active on `BackendApplication`.
- **Security**: No Spring Security, JWT, or auth layer — out of scope for this refactor.
- **Events**: Two coexisting event mechanisms — Spring's `ApplicationEventPublisher` + `@TransactionalEventListener` (for intra-request, transactional concerns like progress recalculation) and the manual `ActivityHub` enum singleton (for cross-cutting broadcast concerns — audit, dashboard counters, overdue alerts).

---

## 10. Frontend Overview

Next.js 16 App Router app in [construct-flow-nextjs-frontend/](../construct-flow-nextjs-frontend/). Communicates with the backend via Axios (`lib/api-service.ts`, base URL `http://localhost:8080/api`). Main screens: dashboard, projects, tasks, resources, documents, daily reports, announcements, analytics. State managed via React Context (`app-context.tsx`). The frontend was not changed as part of either refactor phase.

---

## 11. SOLID Audit — Violations Addressed

Every finding below was resolved as part of Phase 1.

| # | Principle | Original location | Violation | Resolution |
|---|---|---|---|---|
| 1 | **SRP** | `ResourceService.allocateResource` | Validated, mutated inventory, created allocation, and logged in one method. | Mapping extracted to `ResourceMapper`; validation and orchestration later moved to the Template Method (12.7) and Mediator (12.8) in Phase 2. |
| 2 | **SRP** | `DocumentService.uploadDocument` | Mixed filesystem I/O with JPA persistence. | I/O delegated to `DocumentStorage` adapter. |
| 3 | **SRP** | `GlobalReportService.generateExecutiveSummary` | Queried, filtered, formatted, printed debug logs, and assembled DTO in ~60 lines. | Debug prints removed; iteration extracted to iterators; report composition moved to `ReportService` + factory family. |
| 4 | **SRP** | Every service | `mapToResponseDTO` / `convertToResponseDTO` duplicated across all 13 services. | Moved to dedicated `service/mapping/XxxMapper` components injected via constructor. |
| 5 | **OCP** | `GlobalReportService` | Adding a new report type required editing the existing class. | `ReportArtifactFactory` abstract factory — new report kinds are new `@Component` classes. |
| 6 | **OCP** | `AnalyticsService` | New aggregation rules required modifying the existing service. | `PagedRepositoryIterator` makes the scan strategy injectable/replaceable. |
| 7 | **LSP** | `BaseEntity` hierarchy | Resources (Material/Equipment/Labor) distinguished by a `String category`, not subclasses — no hierarchy to violate. | Acknowledged; the Composite hierarchy (`WorkItem → LeafTask / CompositeTask`) was designed to be fully LSP-compliant. |
| 8 | **ISP** | Repository interfaces | Mix reads, writes, and projections on the same interface. | Not split (Spring Data conventions are the right trade-off here), but noted for future custom query interfaces. |
| 9 | **DIP** | `TaskService` | Depended directly on the concrete `ProjectService` class. | `TaskService` now publishes `TaskMutatedEvent`; `ProgressRecalculator` listens with `@TransactionalEventListener`. |
| 10 | **DIP** | `DocumentService` | Depended directly on `java.nio.file`. | `DocumentStorage` interface injected; `LocalFileSystemStorageAdapter` wraps NIO. |
| 11 | **DIP** | `ResourceService` | Used `System.out.println` instead of an injected logger. | Replaced with `LoggerFactory.getLogger` + `log.info(...)`. |
| 12 | Exceptions | `GlobalExceptionHandler` | Caught only `RuntimeException` — every error returned 500. | Three typed domain exceptions added; handler maps each to the correct HTTP status. |
| 13 | Config | `DocumentService`, `WebConfig`, `GlobalReportService` | Hard-coded `"uploads/"`, `"Active"`, `"In Progress"`, `"Completed"`, CORS origins. | All moved to `AppProperties` and `StorageProperties` via `@ConfigurationProperties`. |

Phase 2 did not surface new SOLID violations; it added new extension points (strategies, mediators, templates, observers) that reinforce OCP and SRP in areas the previous refactor had already simplified.

---

## 12. Design-Pattern Implementation

## Phase 1 — Creational & Structural

---

### 12.1 Adapter — Document Storage Decoupled

**Before.** `DocumentService` called `java.nio.file` APIs directly, with an un-configurable `UPLOAD_DIR = "uploads/"` constant. Deleting a document did not remove the physical file.

**After.** A `DocumentStorage` port was defined:

```java
// service/storage/DocumentStorage.java
public interface DocumentStorage {
    StoredFile store(MultipartFile file) throws IOException;
    InputStream load(String storageKey) throws IOException;
    void delete(String storageKey) throws IOException;
}

public record StoredFile(String storageKey, long sizeBytes) {}
```

`LocalFileSystemStorageAdapter` implements this port by wrapping NIO. The upload path was externalised to `StorageProperties` (`storage.local-path` in `application.properties`). `DocumentService` now depends only on the `DocumentStorage` interface — switching to S3 or Azure Blob requires only a new `@Component` adapter, with zero changes to the service.

The `Document` entity gained a `storageKey` field so deletion is file-aware:

```java
documentStorage.delete(doc.getStorageKey()); // called inside deleteDocument()
```

**SOLID closed.** DIP #10, OCP (new adapter = new class), SRP (service no longer manages bytes).

---

### 12.2 Factory Method — Entity Construction

**Before.** Every service contained an ad-hoc `new Entity(); entity.setX(...); entity.setY(...)` block for creation, and a parallel null-guarded block for updates. This duplicated construction rules and mixed them with persistence logic.

**After.** An `EntityFactory<E, D>` interface defined two factory methods:

```java
public interface EntityFactory<E, D> {
    E create(D dto);           // create with defaults applied
    void apply(E existing, D dto); // patch-update (null-safe)
}
```

Three concrete factories were implemented: `TaskFactory`, `ProjectFactory` (reads default status from `AppProperties`), and `ResourceFactory`. Services now delegate:

```java
// TaskService.createTask — before: 10 lines of setters
Task saved = taskRepository.save(taskFactory.create(dto));
```

```java
// TaskService.updateTask — before: 8 null-guarded setters
taskFactory.apply(task, dto);
```

**SOLID closed.** SRP (construction rules live in the factory), OCP (extend the factory for specialised creation logic), DRY.

---

### 12.3 Abstract Factory — Report Families

**Before.** `GlobalReportService` only produced one report type (`ExecutiveSummaryDTO`). Adding a project-level or financial report would have required editing that class.

**After.** A `ReportArtifactFactory` interface (the abstract factory) was defined with three products: `kind()`, `header(ctx)`, and `sections()`. Three concrete factories were implemented as Spring `@Component`s:

| Factory | `kind()` | Sections produced |
|---|---|---|
| `ExecutiveReportFactory` | `EXECUTIVE` | Project Overview, Financial Summary, Task Health, Recent Activity |
| `ProjectReportFactory` | `PROJECT` | Project Counts, Task Progress, Overdue Alerts |
| `FinancialReportFactory` | `FINANCIAL` | Budget vs Actual, Cost Efficiency |

`ReportService` resolves the correct factory from a `List<ReportArtifactFactory>` injected by Spring, builds a `ReportContext` via iterators, and assembles the output:

```java
ReportArtifactFactory factory = factories.stream()
    .filter(f -> f.kind() == kind)
    .findFirst()
    .orElseThrow(...);
```

A new endpoint was added: `GET /api/reports/{kind}` (where `kind` is `EXECUTIVE`, `PROJECT`, or `FINANCIAL`).

**SOLID closed.** OCP #5 (new report family = new `@Component`, no existing file touched), SRP (each `ReportSection` renders itself), DIP (`ReportService` depends on the abstract factory interface).

---

### 12.4 Iterator — Lazy Repository Traversal

**Before.** `AnalyticsService` and `GlobalReportService` called `projectRepository.findAll()` and `taskRepository.findAll()`, loading every row into memory before filtering with streams. This was an O(n) memory operation on the heap.

**After.** Three iterator types were introduced in `service/iteration/`:

**`PagedRepositoryIterator<T>`** — a generic `Iterator<T>` that fetches one page at a time (page size 100) using a `Function<Pageable, Page<T>>`. Callers iterate with a standard `for` loop; memory use stays constant regardless of row count.

**`ProjectScanner`** — a Spring `@Component` implementing `Iterable<Project>` backed by `PagedRepositoryIterator`. Can be used in `for (Project p : projectScanner)`.

**`ProjectTaskTreeIterator`** — walks a project iterator and issues per-project task queries, yielding every `Task` across all projects one at a time. Used by `GlobalReportService` and `ReportService`.

Services were rewritten to consume these iterators:

```java
for (Project p : projectScanner) {
    totalProjects++;
    if (activeStatus.equalsIgnoreCase(p.getStatus())) activeProjects++;
    ...
}
```

**SOLID closed.** SRP (iteration concern extracted from business logic), OCP (swap the paging strategy by changing the `Function<Pageable, Page<T>>`), memory safety.

UML: [`docs/uml/08_iterator_paged_repository.puml`](uml/08_iterator_paged_repository.puml).

---

### 12.5 Composite — Task Hierarchy & Progress Aggregation

**Before.** `Task` had no parent/child relationship. `ProjectService.updateProjectProgress` computed progress as `completedCount / totalCount` across a flat list — a model that cannot express subtask nesting.

**After.** A Composite pattern was implemented in `model/work/`:

```
WorkItem (interface)
├── LeafTask     — wraps a single Task entity
└── CompositeTask — aggregates children WorkItems
```

`WorkItem` declares `progress()` and `actualCost()`, so both leaf and composite are interchangeable from the caller's perspective. `CompositeTask.progress()` recursively averages its children; `CompositeTask.actualCost()` sums them.

A nullable `parentTaskId` column was added to `Task` to persist the parent/child relationship in the database.

`TaskTreeBuilder` reconstructs the tree from a flat task list:

```java
WorkItem root = taskTreeBuilder.buildForProject(projectId);
project.setActualCost(root.actualCost());
// progress is now owned by the Strategy in 12.10
```

`WorkItemVisitor` was also defined to enable future report generation (e.g. "print all overdue leaf tasks in a tree") without modifying the node classes.

**SOLID closed.** OCP (new node type = new class), SRP (aggregation rules inside the composite, not the service), LSP (leaf and composite are interchangeable under `WorkItem`).

---

## Phase 2 — Behavioural

UML diagrams for every case below live under [`docs/uml/`](uml/) as both PlantUML sources and rendered PNGs, with a per-diagram explainer in [`docs/uml/README.md`](uml/README.md). Full technical write-ups of each pattern (including participants tables, sequence notes, and per-case assumptions) live in [`BEHAVIOURAL_PATTERNS_REPORT.md`](BEHAVIOURAL_PATTERNS_REPORT.md).

---

### 12.6 Template Method — Document Export

**Problem.** Users need to export uploaded documents in multiple formats (PDF, CSV, ZIP). The pipeline — validate requester → resolve document → load bytes → transform → log — is fixed; only the transform, content-type, and file-extension steps vary per format.

**Design.** `AbstractDocumentExporter.export(ExportRequest)` is the `final` template method. Subclasses override three abstract primitives (`transform`, `contentType`, `fileExtension`) and may override the `validateAccess`, `buildFilename`, and `logExport` hooks.

```java
public final ExportResult export(ExportRequest request) throws IOException {
    validateAccess(request);
    Document document = resolveDocument(request);
    byte[] rawContent = loadContent(document);
    byte[] transformed = transform(document, rawContent);
    String filename = buildFilename(document);
    logExport(request, filename);
    return new ExportResult(filename, contentType(), transformed);
}
```

Three concrete subclasses ship as `@Component`s: `PdfDocumentExporter`, `CsvDocumentExporter`, `ZipArchiveExporter`. `DocumentExporterResolver` maps the `ExportFormat` enum to the correct exporter at startup. The controller exposes `GET /api/documents/{id}/export?format=PDF|CSV|ZIP`.

**SOLID wins.** OCP (new format = new `@Component` + one enum entry), SRP (each exporter owns only its encoding), LSP (all exporters interchangeable under `AbstractDocumentExporter`).

UML: [`docs/uml/01_template_method_document_export.puml`](uml/01_template_method_document_export.puml).

---

### 12.7 Template Method — Allocation Validation

**Problem.** `ResourceService.allocateResource` used to run a single availability check. In reality, Material, Equipment, and Labor allocations have different rules: materials must not drop below a reorder buffer; equipment must be allocated in whole units and cannot exceed 100% allocation; labor must not exceed a per-booking hour cap. The skeleton — basic input checks → resolve resource → availability → category rule → optional budget — is identical across categories.

**Design.** `AbstractAllocationValidator.validate(AllocationRequest)` is the `final` template method that enforces the order. Subclasses implement the `checkCategoryRules` abstract primitive. `AllocationValidatorRegistry` dispatches to the right subclass by `resource.getCategory()`, with `MaterialAllocationValidator` acting as the strictest fallback.

```java
public final Resource validate(AllocationRequest request) {
    checkBasicInputs(request);
    Resource resource = resolveResource(request);
    checkAvailability(resource, request);
    checkCategoryRules(resource, request);   // abstract
    checkProjectBudget(resource, request);   // hook (default no-op)
    return resource;
}
```

The validator is consumed inside `ResourceColleague.reserve(command)` — part of the Mediator workflow in 12.8.

**SOLID wins.** OCP (new category = new subclass), SRP (each validator owns one rule), DRY.

UML: [`docs/uml/02_template_method_allocation_validator.puml`](uml/02_template_method_allocation_validator.puml).

---

### 12.8 Mediator — Resource-Allocation Workflow

**Problem.** Allocating a resource touches four concerns (stock, allocation row, broadcast, audit). Directly coupling `ResourceService` to all four created a mesh where the business rule — "reserve → record → broadcast → audit, in that order" — was hidden inside one long method.

**Design.** `AllocationMediator` is the interface; `DefaultAllocationMediator` is the concrete mediator that sequences four colleagues:

| Colleague | Responsibility |
|---|---|
| `ResourceColleague` | Runs the allocation Template Method (12.7) and deducts the quantity |
| `TaskAllocationColleague` | Persists the `TaskAllocation` row |
| `NotificationColleague` | Publishes `Activity.ResourceAllocated` to the `ActivityHub` (12.12) |
| `AuditColleague` | Writes the audit log line via SLF4J |

`ResourceService.allocateResource(taskId, resourceId, qty)` is now one line:

```java
allocationMediator.allocate(new AllocationCommand(taskId, resourceId, qty));
```

The mediator is `@Transactional` — a failure in any colleague rolls back the whole operation atomically.

**SOLID wins.** SRP (each colleague has one job; the mediator owns sequencing), DIP (`ResourceService` depends on an interface).

UML: [`docs/uml/03_mediator_allocation.puml`](uml/03_mediator_allocation.puml).

---

### 12.9 Mediator — Announcement Discussion Room

**Problem.** Posting a comment on an announcement should, in principle, trigger several side effects: notify the author, refresh a dashboard counter, relay the event onto the global activity stream, and (future) email mentioned users. Each side effect should be independently addable and removable.

**Design.** `DiscussionRoomMediator` (interface) + `AnnouncementRoom` (concrete) implement the classic chat-room form of the pattern. Each side effect is a `Participant`; posting a comment persists the row and then fans out to every registered participant. Three default participants ship:

| Participant | Behaviour |
|---|---|
| `AuthorParticipant` | Notifies the announcement's author |
| `DashboardParticipant` | Increments an in-memory per-announcement comment counter |
| `ActivityRelayParticipant` | Publishes `Activity.CommentPosted` to the `ActivityHub` (12.12) |

Dynamic participants (mentions, subscribers) can `join(announcementId, participant)` and `leave(announcementId, participant)` at runtime. Dispatch is wrapped in `safeDispatch` so a misbehaving participant cannot poison the room.

`AnnouncementCommentService.addComment` became a thin forwarder: `return discussionRoomMediator.post(announcementId, dto);`.

**SOLID wins.** OCP (new participant = new class + one `join` call), SRP (each participant models one reaction).

UML: [`docs/uml/04_mediator_discussion_room.puml`](uml/04_mediator_discussion_room.puml).

---

### 12.10 Strategy — Project-Progress Calculation

**Problem.** `ProjectService.updateProjectProgress` used only the `completed / total` task count model. Real construction projects weight progress differently — by cost, by milestones, or by effort hours. Baking one formula into the service blocked the other three.

**Design.** `ProgressStrategy` is the common interface; four interchangeable implementations ship:

| Strategy | Formula |
|---|---|
| `TaskCountProgressStrategy` (default) | completed task count / total |
| `WeightedByCostProgressStrategy` | share of total `actualCost` booked against completed tasks |
| `MilestoneBasedProgressStrategy` | share of milestones hit (milestone-to-task match by name) |
| `EffortBasedProgressStrategy` | share of logged hours on completed tasks (reads `WorkLog`) |

A new `progress_model` column (enum `ProgressModel`) was added to `Project` so each project picks its own model. `ProgressStrategyResolver` dispatches by enum key. `ProjectService.updateProjectProgress` delegates the percentage to the resolved strategy and the cost rollup to the Composite tree from 12.5.

**SOLID wins.** OCP (new model = new `@Component` + enum entry), SRP (each strategy owns one formula), DIP (service depends on the interface).

UML: [`docs/uml/05_strategy_progress.puml`](uml/05_strategy_progress.puml).

---

### 12.11 Strategy — Critical-Task Prioritisation

**Problem.** `GET /api/tasks/critical` returned critical-priority tasks in insertion order. Site managers need different orderings depending on context — by due date, by cost exposure, by dependency fan-out, or by a composite risk score.

**Design.** `PrioritisationStrategy` is the interface; four implementations ship:

| Strategy | Ordering |
|---|---|
| `DueDatePrioritisationStrategy` (default) | Earliest due date first; null dates sort last |
| `CostDescPrioritisationStrategy` | Highest `actualCost` first |
| `DependencyCountPrioritisationStrategy` | Most dependents first |
| `RiskWeightedPrioritisationStrategy` | Composite score blending urgency, cost, and fan-out |

Unlike 12.10, the strategy key (`PrioritisationKey`) is supplied at request time via the `sortBy` query parameter: `GET /api/tasks/critical?sortBy=RISK`. `PrioritisationStrategyResolver` dispatches by enum.

The risk-weighted score:

```
priorityMultiplier × (urgencyScore × 10 + log10(max(cost,1)) + dependencyCount × 2)
```

where `urgencyScore` is 10 for overdue, 8 for due-today, and fades linearly over two weeks otherwise.

**SOLID wins.** OCP (new ordering = new `@Component` + enum entry), SRP (each strategy is a comparator).

UML: [`docs/uml/06_strategy_prioritisation.puml`](uml/06_strategy_prioritisation.puml).

---

### 12.12 Observer + Singleton — Global Activity Hub

**Problem.** Several cross-cutting concerns — audit trail, dashboard counters, overdue alerting, future WebSocket broadcast — all need to react to the same stream of system events. Wiring each as a direct dependency of every publishing service creates a ~15-edge mesh. Spring's event bus partly solves this but is invisible outside the Spring container; a manual, textbook implementation makes the pattern explicit and testable.

**Design.** `ActivityHub` is an **enum-based singleton** (Effective Java, Item 3) — JVM-guaranteed single instance, thread-safe without synchronisation, immune to reflection and serialisation attacks:

```java
public enum ActivityHub {
    INSTANCE;

    private final List<ActivityObserver> observers = new CopyOnWriteArrayList<>();

    public void subscribe(ActivityObserver observer)   { observers.addIfAbsent(observer); }
    public void unsubscribe(ActivityObserver observer) { observers.remove(observer); }
    public void publish(Activity activity) {
        for (ActivityObserver o : observers) {
            try { o.onActivity(activity); }
            catch (RuntimeException ex) { /* log + continue */ }
        }
    }
}
```

`Activity` is a sealed interface with five record variants: `TaskCreated`, `TaskCompleted`, `TaskOverdue`, `ResourceAllocated`, `CommentPosted`. Three observers ship as `@Component`s that self-register in `@PostConstruct`:

| Observer | Reaction |
|---|---|
| `AuditLogObserver` | Writes every activity to the audit logger |
| `DashboardCounterObserver` | Maintains in-memory tallies by event type |
| `OverdueAlertObserver` | Emits WARN-level alerts on `TaskOverdue` events |

Publishers: `TaskService` (on create/complete), `NotificationColleague` (via the allocation mediator), `ActivityRelayParticipant` (via the discussion-room mediator). Dispatch is synchronous; a misbehaving observer is caught and logged so downstream observers still receive the event.

**Why both patterns together.** Observer alone with multiple hubs would let observers miss events; Singleton alone gives no dispatch semantics. The combination delivers one JVM-wide publish point with pluggable reactions.

**SOLID wins.** OCP (new observer = new `@Component`, no edits elsewhere), SRP (hub dispatches, observers react, publishers publish), DIP (publishers depend on the hub's public API, not on the concrete observers).

UML: [`docs/uml/07_observer_singleton_activity_hub.puml`](uml/07_observer_singleton_activity_hub.puml). Test: [`backend/src/test/java/com/constructflow/service/observer/ActivityHubTest.java`](../backend/src/test/java/com/constructflow/service/observer/ActivityHubTest.java) (9 cases — see §15).

---

## 13. Final Package Structure

```
com.constructflow
├── config/
│   ├── WebConfig.java                    # CORS (reads AppProperties)
│   ├── AppProperties.java                # @ConfigurationProperties("app")
│   └── StorageProperties.java            # @ConfigurationProperties("storage")
├── controller/                           # (thinner — no @CrossOrigin)
├── dto/
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java    # 404
│   ├── InsufficientResourceException.java # 422
│   └── DomainValidationException.java    # 400
├── model/
│   └── work/                             # Composite (Pattern 12.5)
│       ├── WorkItem.java                 # Component interface
│       ├── LeafTask.java                 # Leaf
│       ├── CompositeTask.java            # Composite
│       └── WorkItemVisitor.java          # Visitor hook
├── repository/
├── service/
│   ├── events/                           # Spring event bus (DIP fix in Phase 1)
│   │   ├── TaskMutatedEvent.java
│   │   └── ProgressRecalculator.java     # @TransactionalEventListener
│   ├── factory/                          # Factory Method (12.2)
│   │   ├── EntityFactory.java
│   │   ├── TaskFactory.java
│   │   ├── ProjectFactory.java
│   │   ├── ResourceFactory.java
│   │   └── report/                       # Abstract Factory (12.3)
│   │       ├── ReportKind.java
│   │       ├── ReportContext.java
│   │       ├── ReportSection.java
│   │       ├── ReportArtifactFactory.java
│   │       ├── ExecutiveReportFactory.java
│   │       ├── ProjectReportFactory.java
│   │       └── FinancialReportFactory.java
│   ├── iteration/                        # Iterator (12.4)
│   │   ├── PagedRepositoryIterator.java
│   │   ├── ProjectScanner.java
│   │   └── ProjectTaskTreeIterator.java
│   ├── mapping/                          # Per-aggregate DTO mappers (Phase 1 SRP fix)
│   │   ├── ProjectMapper.java
│   │   ├── TaskMapper.java
│   │   ├── ResourceMapper.java
│   │   ├── DocumentMapper.java
│   │   ├── DailyReportMapper.java
│   │   ├── DailyLogMapper.java
│   │   ├── WorkLogMapper.java
│   │   ├── StakeholderMapper.java
│   │   └── AnnouncementMapper.java
│   ├── mediator/
│   │   ├── allocation/                   # Mediator (12.8)
│   │   │   ├── AllocationMediator.java
│   │   │   ├── DefaultAllocationMediator.java
│   │   │   ├── AllocationCommand.java
│   │   │   ├── ResourceColleague.java
│   │   │   ├── TaskAllocationColleague.java
│   │   │   ├── NotificationColleague.java
│   │   │   └── AuditColleague.java
│   │   └── discussion/                   # Mediator (12.9)
│   │       ├── DiscussionRoomMediator.java
│   │       ├── AnnouncementRoom.java
│   │       ├── Participant.java
│   │       ├── AuthorParticipant.java
│   │       ├── DashboardParticipant.java
│   │       └── ActivityRelayParticipant.java
│   ├── observer/                         # Observer + Singleton (12.12)
│   │   ├── ActivityHub.java              # enum singleton
│   │   ├── Activity.java                 # sealed interface + 5 record variants
│   │   ├── ActivityObserver.java
│   │   ├── AuditLogObserver.java
│   │   ├── DashboardCounterObserver.java
│   │   └── OverdueAlertObserver.java
│   ├── storage/                          # Adapter (12.1)
│   │   ├── DocumentStorage.java          # port
│   │   ├── StoredFile.java
│   │   └── LocalFileSystemStorageAdapter.java
│   ├── strategy/
│   │   ├── progress/                     # Strategy (12.10)
│   │   │   ├── ProgressStrategy.java
│   │   │   ├── ProgressModel.java
│   │   │   ├── ProgressStrategyResolver.java
│   │   │   ├── TaskCountProgressStrategy.java
│   │   │   ├── WeightedByCostProgressStrategy.java
│   │   │   ├── MilestoneBasedProgressStrategy.java
│   │   │   └── EffortBasedProgressStrategy.java
│   │   └── prioritisation/               # Strategy (12.11)
│   │       ├── PrioritisationStrategy.java
│   │       ├── PrioritisationKey.java
│   │       ├── PrioritisationStrategyResolver.java
│   │       ├── DueDatePrioritisationStrategy.java
│   │       ├── CostDescPrioritisationStrategy.java
│   │       ├── DependencyCountPrioritisationStrategy.java
│   │       └── RiskWeightedPrioritisationStrategy.java
│   ├── template/
│   │   ├── export/                       # Template Method (12.6)
│   │   │   ├── AbstractDocumentExporter.java
│   │   │   ├── PdfDocumentExporter.java
│   │   │   ├── CsvDocumentExporter.java
│   │   │   ├── ZipArchiveExporter.java
│   │   │   ├── DocumentExporterResolver.java
│   │   │   ├── ExportFormat.java
│   │   │   ├── ExportRequest.java
│   │   │   └── ExportResult.java
│   │   └── allocation/                   # Template Method (12.7)
│   │       ├── AbstractAllocationValidator.java
│   │       ├── MaterialAllocationValidator.java
│   │       ├── EquipmentAllocationValidator.java
│   │       ├── LaborAllocationValidator.java
│   │       ├── AllocationValidatorRegistry.java
│   │       └── AllocationRequest.java
│   ├── ProjectService.java               # uses ProgressStrategy + Composite
│   ├── TaskService.java                  # publishes ActivityHub + uses PrioritisationStrategy
│   ├── ResourceService.java              # delegates to AllocationMediator
│   ├── DocumentService.java              # depends on DocumentStorage port
│   ├── GlobalReportService.java          # uses Iterator
│   ├── ReportService.java                # Abstract Factory dispatcher
│   ├── AnalyticsService.java             # uses Iterator
│   ├── AnnouncementCommentService.java   # forwards to DiscussionRoomMediator
│   ├── TaskTreeBuilder.java              # Composite tree assembler
│   └── ...other services
└── BackendApplication.java
```

---

## 14. Testing

### 14.1 `ActivityHubTest`

[`backend/src/test/java/com/constructflow/service/observer/ActivityHubTest.java`](../backend/src/test/java/com/constructflow/service/observer/ActivityHubTest.java) runs under JUnit 5 (included via `spring-boot-starter-test`). Nine cases cover both the Singleton invariants and the Observer contract:

| # | Test | What it proves |
|---|---|---|
| 1 | `sameInstanceAcrossLookups` | `ActivityHub.INSTANCE` resolves to the same object on every call |
| 2 | `enumValuesContainsExactlyOneInstance` | `ActivityHub.values().length == 1` |
| 3 | `reflectionCannotInstantiateASecondHub` | JDK blocks `Constructor.newInstance` on enum types |
| 4 | `serialisationRoundTripReturnsSameInstance` | Enum singletons survive serialise/deserialise as themselves |
| 5 | `subscribedObserversReceivePublishedActivities` | Basic dispatch works |
| 6 | `allRegisteredObserversReceiveTheSameEvent` | Fan-out is to the same object reference |
| 7 | `unsubscribedObserverStopsReceivingEvents` | `unsubscribe` actually unsubscribes |
| 8 | `misbehavingObserverDoesNotPreventOthersFromReceiving` | Exception isolation between observers |

Run:

```bash
cd backend
mvn test -Dtest=ActivityHubTest
```

### 14.2 Future testing

Not in scope for this phase but worth tracking:

- Integration tests on the new REST endpoints (`/documents/{id}/export`, `/tasks/critical?sortBy=...`).
- Unit tests per `ProgressStrategy` and `PrioritisationStrategy` implementation against synthetic fixtures.
- Integration tests on both mediators exercising transactional rollback on colleague failure.

---

*Last updated: 2026-04-21.*
