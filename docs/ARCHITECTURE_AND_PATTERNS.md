# ConstructFlow — Architecture, SOLID Compliance & Design-Pattern Implementation

> A complete map of the ConstructFlow codebase documenting the **Adapter**, **Factory Method**, **Abstract Factory**, **Iterator**, and **Composite** patterns that were introduced into the Java backend, and how each one closed a specific SOLID violation.

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
    - [12.1 Adapter](#121-adapter--document-storage-decoupled)
    - [12.2 Factory Method](#122-factory-method--entity-construction)
    - [12.3 Abstract Factory](#123-abstract-factory--report-families)
    - [12.4 Iterator](#124-iterator--lazy-repository-traversal)
    - [12.5 Composite](#125-composite--task-hierarchy--progress-aggregation)
13. [Final Package Structure](#13-final-package-structure)
14. [Migration Log](#14-migration-log)

---

## 1. Repository Layout

```
Constructflow-Project-Management/
├── backend/                         # Spring Boot 3.2.1 Java backend
│   ├── pom.xml
│   └── src/main/java/com/constructflow/
│       ├── BackendApplication.java
│       ├── config/                  # WebConfig, AppProperties, StorageProperties
│       ├── controller/              # 9 REST controllers
│       ├── dto/                     # 17 request/response DTOs
│       ├── exception/               # GlobalExceptionHandler + 3 domain exceptions
│       ├── model/                   # 11 JPA entities + BaseEntity
│       │   └── work/                # Composite pattern nodes
│       ├── repository/              # 11 Spring Data repositories
│       └── service/                 # 13+ service classes
│           ├── events/              # TaskMutatedEvent, ProgressRecalculator
│           ├── factory/             # EntityFactory implementations
│           │   └── report/          # Abstract Factory report family
│           ├── iteration/           # Iterator implementations
│           ├── mapping/             # XxxMapper components
│           └── storage/             # Adapter: DocumentStorage port + adapters
├── construct-flow-nextjs-frontend/  # Next.js 16 / React 19 / TypeScript
├── docs/                            # ERDs, setup docs, this file
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
| Frontend | Next.js 16, React 19, TypeScript 5, TailwindCSS 4, Radix UI, Axios, React Hook Form, Zod, Recharts |

---

## 3. Java Backend — Structure

Entry point: [backend/src/main/java/com/constructflow/BackendApplication.java](backend/src/main/java/com/constructflow/BackendApplication.java)

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
├── model.work.*                # WorkItem, LeafTask, CompositeTask, WorkItemVisitor
├── repository.*                # JpaRepository interfaces
└── service.*                   # Business logic + sub-packages
```

---

## 4. Domain Model (Entities)

All entities extend [BaseEntity](backend/src/main/java/com/constructflow/model/BaseEntity.java) which provides:
- `UUID id` (generated)
- `LocalDateTime createdAt` (`@CreatedDate`)
- `LocalDateTime lastModifiedAt` (`@LastModifiedDate`)
- `Long version` (optimistic locking via `@Version`)

| Entity | Key fields | Relationships |
|---|---|---|
| **Project** | name, client, location, budget, actualCost, startDate, endDate, progress, status, objectives, milestones | 1..N Tasks, Resources, Documents, Stakeholders, DailyReports |
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

All under [backend/src/main/java/com/constructflow/repository/](backend/src/main/java/com/constructflow/repository/). Each extends `JpaRepository<Entity, UUID>`. Highlights:

- `ProjectRepository` — `findByStatus`, aggregate queries (`getTotalBudget`, `getAverageActualCost`), sub-query `findHighValueProjects`.
- `TaskRepository` — `findByProjectId`, `findByPriority`, `findCriticalTasks`, `findExpensiveTasks` (sub-query).
- `ResourceRepository` — `findResourcesInActiveProjects` (multi-join).
- `DailyLogRepository` — `findLogsByProjectLocation`, `findTasksUsingResourceCategory` (4-table joins).

---

## 6. Services

All `@Service` + `@RequiredArgsConstructor`. Each service was refactored to inject a dedicated mapper and factory rather than embed mapping and construction logic inline.

- [ProjectService.java](backend/src/main/java/com/constructflow/service/ProjectService.java) — CRUD; `updateProjectProgress(UUID)` now delegates to `TaskTreeBuilder` (Composite) instead of a flat stream.
- [TaskService.java](backend/src/main/java/com/constructflow/service/TaskService.java) — CRUD; publishes `TaskMutatedEvent` via `ApplicationEventPublisher` instead of calling `ProjectService` directly.
- [ResourceService.java](backend/src/main/java/com/constructflow/service/ResourceService.java) — CRUD + `allocateResource()` + `updateInventory()`; inventory changes are logged via SLF4J.
- [DocumentService.java](backend/src/main/java/com/constructflow/service/DocumentService.java) — delegates all byte-level I/O to the injected `DocumentStorage` port; correctly deletes the physical file on `deleteDocument`.
- [GlobalReportService.java](backend/src/main/java/com/constructflow/service/GlobalReportService.java) — uses `ProjectScanner` and `ProjectTaskTreeIterator` for O(1)-memory traversal; reads status constants from `AppProperties`.
- [ReportService.java](backend/src/main/java/com/constructflow/service/ReportService.java) — dispatcher that resolves a `ReportArtifactFactory` by `ReportKind` and assembles the structured output map.
- [AnalyticsService.java](backend/src/main/java/com/constructflow/service/AnalyticsService.java) — uses `PagedRepositoryIterator` for memory-safe streaming; reads status constants from `AppProperties`.
- Other services (DailyReport, DailyLog, WorkLog, Announcement, AnnouncementComment, Stakeholder, Search) — thin CRUD wrappers using injected mappers.

---

## 7. Controllers & REST API

All per-controller `@CrossOrigin` annotations were removed; CORS is handled globally in `WebConfig`.

| Controller | Base path | Notable endpoints |
|---|---|---|
| ProjectController | `/api/projects` | GET list (paged), GET/POST/PUT/DELETE by id |
| TaskController | `/api/tasks` | `/project/{id}`, `/critical` |
| ResourceController | `/api/resources` | `POST /allocate`, `POST /{id}/inventory` |
| AnnouncementController | `/api/announcements` | nested `/{id}/comments` |
| AnalyticsController | `/api/analytics` | `/dashboard`, `/advanced` |
| DailyReportController | `/api/daily-reports` | `/project/{id}` |
| DocumentController | `/api/documents` | upload (multipart) |
| StakeholderController | `/api/stakeholders` | CRUD |
| SearchController | `/api/search` | cross-entity search |
| ReportController | `/api/reports` | `GET /summary`, **`GET /{kind}`** (EXECUTIVE/PROJECT/FINANCIAL) |

---

## 8. DTOs

Under [backend/src/main/java/com/constructflow/dto/](backend/src/main/java/com/constructflow/dto/). Split into `*RequestDTO` (input, validated with Jakarta annotations) and `*ResponseDTO` (output). DTO construction was moved entirely out of services into dedicated mapper components (`service/mapping/`).

---

## 9. Configuration & Cross-Cutting

- **CORS**: [WebConfig.java](backend/src/main/java/com/constructflow/config/WebConfig.java) reads allowed origins from `AppProperties` (`app.cors.allowed-origins`). The wildcard `"*"` origin was removed.
- **AppProperties**: `@ConfigurationProperties(prefix = "app")` exposes CORS origins and the three canonical status strings (`active`, `in-progress`, `completed`) so they can be changed without touching Java code.
- **StorageProperties**: `@ConfigurationProperties(prefix = "storage")` exposes `local-path` for the filesystem adapter.
- **Exception handling**: [GlobalExceptionHandler.java](backend/src/main/java/com/constructflow/exception/GlobalExceptionHandler.java) now handles `ResourceNotFoundException` (404), `InsufficientResourceException` (422), `DomainValidationException` (400), and `MethodArgumentNotValidException` (400) in addition to the catch-all `RuntimeException` (500).
- **Auditing**: `@EnableJpaAuditing` confirmed active on `BackendApplication`.
- **Security**: No Spring Security, JWT, or auth layer — out of scope for this refactor.

---

## 10. Frontend Overview

Next.js 16 App Router app in [construct-flow-nextjs-frontend/](construct-flow-nextjs-frontend/). Communicates with the backend via Axios (`lib/api-service.ts`, base URL `http://localhost:8080/api`). Main screens: dashboard, projects, tasks, resources, documents, daily reports, announcements, analytics. State managed via React Context (`app-context.tsx`). The frontend was not changed as part of this refactor.

---

## 11. SOLID Audit — Violations Addressed

Every finding below was resolved as part of the 10-step refactor. The commit reference for each fix is in [section 14](#14-migration-log).

| # | Principle | Original location | Violation | Resolution |
|---|---|---|---|---|
| 1 | **SRP** | `ResourceService.allocateResource` | Validated, mutated inventory, created allocation, and logged in one method. | Mapping extracted to `ResourceMapper`; logging uses SLF4J. |
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

---

## 12. Design-Pattern Implementation

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
project.setProgress(root.progress() * 100);
project.setActualCost(root.actualCost());
```

`WorkItemVisitor` was also defined to enable future report generation (e.g. "print all overdue leaf tasks in a tree") without modifying the node classes.

**SOLID closed.** OCP (new node type = new class), SRP (aggregation rules inside the composite, not the service), LSP (leaf and composite are interchangeable under `WorkItem`).

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
│   └── work/
│       ├── WorkItem.java                 # Composite component interface
│       ├── LeafTask.java                 # Leaf
│       ├── CompositeTask.java            # Composite
│       └── WorkItemVisitor.java          # Visitor hook
├── repository/
├── service/
│   ├── events/
│   │   ├── TaskMutatedEvent.java         # Spring event record
│   │   └── ProgressRecalculator.java     # @TransactionalEventListener
│   ├── factory/
│   │   ├── EntityFactory.java            # Factory Method interface
│   │   ├── TaskFactory.java
│   │   ├── ProjectFactory.java
│   │   ├── ResourceFactory.java
│   │   └── report/
│   │       ├── ReportKind.java           # EXECUTIVE / PROJECT / FINANCIAL
│   │       ├── ReportContext.java        # Data bag passed to sections
│   │       ├── ReportSection.java        # Section interface
│   │       ├── ReportArtifactFactory.java # Abstract Factory interface
│   │       ├── ExecutiveReportFactory.java
│   │       ├── ProjectReportFactory.java
│   │       └── FinancialReportFactory.java
│   ├── iteration/
│   │   ├── PagedRepositoryIterator.java  # Generic lazy paging iterator
│   │   ├── ProjectScanner.java           # Iterable<Project> bean
│   │   └── ProjectTaskTreeIterator.java  # Cross-project task iterator
│   ├── mapping/
│   │   ├── ProjectMapper.java
│   │   ├── TaskMapper.java
│   │   ├── ResourceMapper.java
│   │   ├── DocumentMapper.java
│   │   ├── DailyReportMapper.java
│   │   ├── DailyLogMapper.java
│   │   ├── WorkLogMapper.java
│   │   ├── StakeholderMapper.java
│   │   └── AnnouncementMapper.java
│   ├── storage/
│   │   ├── DocumentStorage.java          # Adapter port (interface)
│   │   ├── StoredFile.java               # Value record
│   │   └── LocalFileSystemStorageAdapter.java # NIO implementation
│   ├── ProjectService.java
│   ├── TaskService.java
│   ├── ResourceService.java
│   ├── DocumentService.java
│   ├── GlobalReportService.java
│   ├── ReportService.java                # Abstract Factory dispatcher
│   ├── AnalyticsService.java
│   ├── TaskTreeBuilder.java              # Composite tree assembler
│   └── ...other services
└── BackendApplication.java
```

---

## 14. Migration Log

All ten steps were completed and each was shipped as an independent commit on `main`.

| Step | Commit | What changed | SOLID findings closed |
|---|---|---|---|
| 1 | `a8c01e6` | Added `ResourceNotFoundException`, `InsufficientResourceException`, `DomainValidationException`; enriched `GlobalExceptionHandler` with 404/422/400 handlers; replaced all `throw new RuntimeException(...)` in services | #12 |
| 2 | `55bfd80` | Created `service/mapping/` with 9 `XxxMapper` components; removed every `mapToResponseDTO` / `convertToResponseDTO` from all 13 services | #4 |
| 3 | `f428e3a` | Introduced `EntityFactory<E,D>` + `TaskFactory`, `ProjectFactory`, `ResourceFactory`; services call `factory.create()` and `factory.apply()` | #1 (partial), #3 (partial) |
| 4 | `a0d50b3` | Added `DocumentStorage` port + `LocalFileSystemStorageAdapter`; `StorageProperties` externalised upload path; `Document.storageKey` added; `deleteDocument` now removes the physical file | #2, #10, #13 (partial) |
| 5 | `daa0e8a` | `TaskMutatedEvent` + `ProgressRecalculator @TransactionalEventListener`; `TaskService` removed its `ProjectService` import entirely | #9 |
| 6 | `85dcf06` | `PagedRepositoryIterator<T>`, `ProjectScanner`, `ProjectTaskTreeIterator`; `GlobalReportService` and `AnalyticsService` rewritten to use iterators instead of `findAll()` | #3 (partial), #6 |
| 7 | `0686f86` | `ReportArtifactFactory` + `ExecutiveReportFactory`, `ProjectReportFactory`, `FinancialReportFactory`; `ReportService` dispatcher; `GET /api/reports/{kind}` endpoint | #5 |
| 8 | `a251517` | `WorkItem`, `LeafTask`, `CompositeTask`, `WorkItemVisitor`; `parentTaskId` added to `Task`; `TaskTreeBuilder`; `ProjectService.updateProjectProgress` delegates to composite tree | OCP/SRP on progress aggregation |
| 9 | `caf7fa2` | Removed all per-controller `@CrossOrigin("*")`; CORS origins driven by `AppProperties`; `System.out.println` replaced with SLF4J `log.info` | #11, #13 (partial) |
| 10 | `75d2fe5` | All hard-coded status strings (`"Active"`, `"In Progress"`, `"Completed"`) replaced with `AppProperties.status.*` injected via constructor | #13 |

---

*Last updated: 2026-04-21.*
