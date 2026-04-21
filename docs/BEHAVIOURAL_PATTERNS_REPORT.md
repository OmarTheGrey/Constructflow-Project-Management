# ConstructFlow — Behavioural Design Patterns Technical Report

**Project**: ConstructFlow — Construction Project Management System
**Module**: Java backend (Spring Boot 3.2.1, Java 17)
**Date**: 2026-04-21
**Authors**: CTRL ALT ELITE

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Background](#2-background)
3. [Design Overview](#3-design-overview)
4. [Pattern 1 — Template Method: Document Export](#4-pattern-1--template-method-document-export)
5. [Pattern 2 — Template Method: Resource-Allocation Validation](#5-pattern-2--template-method-resource-allocation-validation)
6. [Pattern 3 — Mediator: Resource-Allocation Workflow](#6-pattern-3--mediator-resource-allocation-workflow)
7. [Pattern 4 — Mediator: Announcement Discussion Room](#7-pattern-4--mediator-announcement-discussion-room)
8. [Pattern 5 — Strategy: Project-Progress Calculation](#8-pattern-5--strategy-project-progress-calculation)
9. [Pattern 6 — Strategy: Critical-Task Prioritisation](#9-pattern-6--strategy-critical-task-prioritisation)
10. [Pattern 7 — Observer + Singleton: Global Activity Hub](#10-pattern-7--observer--singleton-global-activity-hub)
11. [Pattern 8 — Iterator: Lazy Repository Traversal](#11-pattern-8--iterator-lazy-repository-traversal)
12. [Integration Notes](#12-integration-notes)
13. [Testing](#13-testing)
14. [Build and Run](#14-build-and-run)
15. [Commit Timeline](#15-commit-timeline)
16. [Assumptions](#16-assumptions)
17. [References](#17-references)

---

## 1. Introduction

### 1.1 Document purpose

This report documents the behavioural design patterns introduced into the ConstructFlow Java backend. For each pattern it states the problem being solved, justifies the pattern choice, presents a UML class diagram, enumerates the participants, describes the implementation, and lists the assumptions made along the way.

### 1.2 Project summary

ConstructFlow is a full-stack construction project management platform. The backend exposes a Spring Boot REST API over a Microsoft SQL Server database; the frontend is a Next.js / React 19 / TypeScript single-page app. The backend manages eleven JPA entities covering projects, tasks, resources, allocations, daily reports, work logs, stakeholders, documents, announcements, and discussion threads. A SOLID-compliance refactor preceded this phase and introduced five creational/structural patterns (Adapter, Factory Method, Abstract Factory, Iterator, Composite).

### 1.3 Scope of this report

This report covers eight behavioural-pattern cases delivered in the current phase:

| # | Pattern | Case |
|---|---|---|
| 1 | Template Method | Document Export pipeline |
| 2 | Template Method | Resource-allocation validation pipeline |
| 3 | Mediator | Resource-allocation workflow |
| 4 | Mediator | Announcement discussion room |
| 5 | Strategy | Project-progress calculation |
| 6 | Strategy | Critical-task prioritisation |
| 7 | Observer + Singleton | Global activity hub |
| 8 | Iterator | Lazy repository traversal (already present; documented for completeness) |

Out of scope: anything unrelated to behavioural patterns (e.g. the earlier structural/creational refactor is summarised in §2 but detailed in [`ARCHITECTURE_AND_PATTERNS.md`](ARCHITECTURE_AND_PATTERNS.md)).

---

## 2. Background

### 2.1 Preceding work

Before this phase the codebase was refactored for SOLID compliance (see [`ARCHITECTURE_AND_PATTERNS.md`](ARCHITECTURE_AND_PATTERNS.md)). That phase added:

- Three typed domain exceptions with correct HTTP status mapping.
- Nine `XxxMapper` components removing duplicated DTO mapping from services.
- An `EntityFactory<E, D>` family (Factory Method) for entity construction.
- A `DocumentStorage` port with a `LocalFileSystemStorageAdapter` (Adapter).
- A `ReportArtifactFactory` family (Abstract Factory) with an Executive, Project, and Financial variant.
- `PagedRepositoryIterator` / `ProjectScanner` / `ProjectTaskTreeIterator` (Iterator).
- `WorkItem` / `LeafTask` / `CompositeTask` (Composite) driving project-progress aggregation.
- A Spring-event bus (`TaskMutatedEvent` + `ProgressRecalculator`) decoupling `TaskService` from `ProjectService`.

That earlier work closed thirteen catalogued SOLID violations. This report builds on top of it.

### 2.2 Motivation for the behavioural pass

The behavioural patterns target a different class of problem:

- **Template Method** captures fixed algorithms whose steps vary by subtype — useful when the same skeleton keeps getting duplicated with small per-variant edits.
- **Mediator** replaces many-to-many direct dependencies between services with a many-to-one relationship through an orchestrator.
- **Strategy** makes algorithms interchangeable at runtime. Construction projects use different progress models; dashboards prioritise differently per user.
- **Observer + Singleton** give us a single, global, type-safe activity stream that services publish to without knowing who listens.
- **Iterator** is carried over from the previous pass as it is a classic behavioural pattern in its own right.

Each pattern closes a specific pain point in the codebase; these pain points are spelled out in the "Problem" subsection of each case below.

---

## 3. Design Overview

### 3.1 Package layout added in this phase

```
backend/src/main/java/com/constructflow/
├── service/
│   ├── template/
│   │   ├── export/              Document export pipeline (Pattern 1)
│   │   └── allocation/          Allocation validation pipeline (Pattern 2)
│   ├── mediator/
│   │   ├── allocation/          Resource-allocation mediator (Pattern 3)
│   │   └── discussion/          Discussion-room mediator (Pattern 4)
│   ├── strategy/
│   │   ├── progress/            Project-progress strategies (Pattern 5)
│   │   └── prioritisation/      Critical-task prioritisation (Pattern 6)
│   ├── observer/                ActivityHub singleton + observers (Pattern 7)
│   └── iteration/               (carried forward) Iterator (Pattern 8)
└── src/test/java/.../observer/
    └── ActivityHubTest.java     Demonstrates the Singleton + Observer contract
```

Eight PlantUML sources and their rendered PNGs live under [`docs/uml/`](uml/), one per pattern case, with a per-diagram explainer in [`docs/uml/README.md`](uml/README.md).

### 3.2 How the patterns interlock

The patterns are deliberately composable. Concrete collaborations:

- The allocation **Mediator** (Pattern 3) calls the allocation **Template Method** (Pattern 2) via `ResourceColleague`, and broadcasts the outcome through the `ActivityHub` **Observer-Singleton** (Pattern 7) via `NotificationColleague`.
- The discussion-room **Mediator** (Pattern 4) relays every comment onto the `ActivityHub` via `ActivityRelayParticipant`.
- `ProjectService.updateProjectProgress` delegates to the progress **Strategy** (Pattern 5) for the percentage and to the earlier Composite (`TaskTreeBuilder`) for the cost rollup.
- Report generation in `ReportService` still uses the earlier Abstract Factory, but its data is streamed through the **Iterator** (Pattern 8) via `ProjectScanner` + `ProjectTaskTreeIterator`.

The result is that each pattern has one crisp job; their interactions read as a short, declarative orchestration.

---

## 4. Pattern 1 — Template Method: Document Export

### 4.1 Problem

Users need to export uploaded documents in several formats (PDF, CSV, ZIP archive). Every format follows the same skeleton — validate the requester → resolve the document row → load the bytes through the storage adapter → transform into the target format → log the export — but the transform, content-type, and file-extension steps differ per format. Implementing three independent methods would duplicate the skeleton three times.

### 4.2 Pattern selection

**Template Method** is the canonical solution: encode the invariant skeleton once in a base class as a `final` template method, leave the variable steps abstract, and let each subclass fill them in. The extension point is the step, not the algorithm.

### 4.3 UML

![Document Export](uml/template%20method%20document%20export.png)

### 4.4 Participants

| GoF role | Concrete class |
|---|---|
| `AbstractClass` | `AbstractDocumentExporter` |
| `ConcreteClass` | `PdfDocumentExporter`, `CsvDocumentExporter`, `ZipArchiveExporter` |
| Template method | `AbstractDocumentExporter.export(ExportRequest)` |
| Primitive operations (abstract) | `transform(...)`, `contentType()`, `fileExtension()` |
| Hooks (overridable) | `validateAccess(...)`, `buildFilename(...)`, `logExport(...)` |
| Client | `DocumentController.exportDocument(...)` via `DocumentExporterResolver` |

### 4.5 Implementation

Location: [`service/template/export/`](../backend/src/main/java/com/constructflow/service/template/export)

Entry point:

```java
@GetMapping("/{id}/export")
public ResponseEntity<byte[]> exportDocument(
        @PathVariable UUID id,
        @RequestParam(defaultValue = "PDF") ExportFormat format,
        @RequestParam(defaultValue = "system") String requestedBy) throws IOException {
    ExportResult result = exporterResolver.forFormat(format)
            .export(new ExportRequest(id, format, requestedBy));
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + result.filename() + "\"")
            .contentType(MediaType.parseMediaType(result.contentType()))
            .body(result.bytes());
}
```

The `DocumentExporterResolver` maps `ExportFormat` enum values to the corresponding `@Component` exporter at startup.

### 4.6 SOLID dimensions exercised

- **OCP**: adding a new format (e.g. XLSX) is a new `@Component` plus one enum entry; neither the controller nor the resolver changes.
- **SRP**: each exporter does one job — encode the bytes for a specific format.
- **LSP**: every concrete exporter is usable anywhere an `AbstractDocumentExporter` is expected; the template method's contract holds regardless of subclass.

### 4.7 Assumptions

1. All exporters can work on the same `byte[]` content payload loaded from the storage adapter — there is no format that needs random-access seeking over the original file.
2. The PDF exporter is a stub that prepends a minimal PDF header; a real implementation would use iText or PDFBox. The pattern structure is production-grade regardless.
3. The requester identity is a simple string; authentication/authorisation is out of scope for this phase.

---

## 5. Pattern 2 — Template Method: Resource-Allocation Validation

### 5.1 Problem

Allocating a resource to a task had a single hard-coded check (`resource.quantity < requested`). In reality, Material, Equipment, and Labor allocations have different validation rules:

- **Material**: on-hand quantity must not drop below a reorder buffer.
- **Equipment**: must be allocated in whole units; already-full allocations are refused.
- **Labor**: a single allocation must not exceed a daily hour cap.

The common skeleton — exists → available → category-specific rule → budget — is identical; only the category-specific step varies.

### 5.2 Pattern selection

**Template Method** again. The category-specific rule is one abstract step inside a fixed validation skeleton; the skeleton itself should never vary. A `final` template method enforces the order (reject bad inputs before reading the DB, then check availability before running category-specific logic), which is the business rule.

### 5.3 UML

![Allocation Validator](uml/template%20method%20resource%20allocation.png)

### 5.4 Participants

| GoF role | Concrete class |
|---|---|
| `AbstractClass` | `AbstractAllocationValidator` |
| `ConcreteClass` | `MaterialAllocationValidator`, `EquipmentAllocationValidator`, `LaborAllocationValidator` |
| Template method | `AbstractAllocationValidator.validate(AllocationRequest)` |
| Primitive operations (abstract) | `checkCategoryRules(...)`, `handlesCategory()` |
| Hook (optional override) | `checkProjectBudget(...)` |
| Registry | `AllocationValidatorRegistry` — dispatch by `resource.category` |
| Client | `ResourceColleague` inside the allocation mediator (Pattern 3) |

### 5.5 Implementation

Location: [`service/template/allocation/`](../backend/src/main/java/com/constructflow/service/template/allocation)

The registry builds a lookup by category from the list of `@Component` validators Spring injects at startup:

```java
@Component
public class AllocationValidatorRegistry {
    private final Map<String, AbstractAllocationValidator> byCategory = new HashMap<>();
    private final AbstractAllocationValidator fallback;

    public AllocationValidatorRegistry(List<AbstractAllocationValidator> validators,
                                       MaterialAllocationValidator fallback) {
        validators.forEach(v -> byCategory.put(v.handlesCategory(), v));
        this.fallback = fallback;
    }
    public AbstractAllocationValidator forCategory(String category) {
        return category == null ? fallback : byCategory.getOrDefault(category, fallback);
    }
}
```

`ResourceColleague.reserve(command)` fetches the resource, resolves the validator via the registry, runs `validator.validate(...)`, and then deducts the quantity.

### 5.6 SOLID dimensions exercised

- **OCP**: adding a new resource category (e.g. "Subcontractor") is a new subclass + `@Component`; no existing validator, registry, or service changes.
- **SRP**: each concrete validator owns exactly one rule.
- **DRY**: the common skeleton lives in one place.

### 5.7 Assumptions

1. A single `Resource.category` string discriminates allocations — we have not split Material/Equipment/Labor into subclasses at the entity level. This is consistent with the existing schema.
2. Validator selection is by exact-match on the category string; misspellings fall back to the strictest validator (Material) rather than silently skipping validation.
3. Hour quantities for Labor are modelled as `double` on `AllocationRequest.quantity`, matching `TaskAllocation.quantityAllocated`.

---

## 6. Pattern 3 — Mediator: Resource-Allocation Workflow

### 6.1 Problem

The allocation operation touches four concerns: stock, allocation rows, broadcast notifications, and the audit log. Wiring each concern as a direct dependency of `ResourceService` created tight coupling — every service ended up reaching into multiple others, and the business rule ("reserve → record → broadcast → audit, in that order") was invisible inside a single long method.

### 6.2 Pattern selection

**Mediator** breaks the mesh: each colleague talks only to the mediator, and the mediator encodes the sequence. Each colleague shrinks to a single responsibility. `ResourceService.allocateResource` collapses to a one-liner.

### 6.3 UML

![Allocation Mediator](uml/mediator%20resource%20allocation.png)

### 6.4 Participants

| GoF role | Concrete class |
|---|---|
| `Mediator` | `AllocationMediator` (interface) |
| `ConcreteMediator` | `DefaultAllocationMediator` |
| `Colleague` | `ResourceColleague` (validates + deducts stock — uses Pattern 2), `TaskAllocationColleague` (persists row), `NotificationColleague` (broadcasts to `ActivityHub`), `AuditColleague` (writes audit log) |
| Client | `ResourceService.allocateResource(...)` |
| Command object | `AllocationCommand` record |

### 6.5 Sequence

```
ResourceService.allocateResource(taskId, resourceId, qty)
  └─▶ AllocationMediator.allocate(new AllocationCommand(...))
        1. ResourceColleague.reserve(cmd)
             │ (validates via Pattern 2, deducts quantity, saves Resource)
        2. TaskAllocationColleague.record(cmd)
             │ (saves TaskAllocation row)
        3. NotificationColleague.broadcast(cmd)
             │ (ActivityHub.INSTANCE.publish(ResourceAllocated))
        4. AuditColleague.recordAllocation(cmd, resourceName)
             │ (SLF4J audit line)
```

### 6.6 Implementation

Location: [`service/mediator/allocation/`](../backend/src/main/java/com/constructflow/service/mediator/allocation)

```java
@Component
@RequiredArgsConstructor
public class DefaultAllocationMediator implements AllocationMediator {
    private final ResourceColleague resourceColleague;
    private final TaskAllocationColleague taskAllocationColleague;
    private final NotificationColleague notificationColleague;
    private final AuditColleague auditColleague;

    @Override
    @Transactional
    public void allocate(AllocationCommand command) {
        Resource resource = resourceColleague.reserve(command);
        taskAllocationColleague.record(command);
        notificationColleague.broadcast(command);
        auditColleague.recordAllocation(command, resource.getName());
    }
}
```

If any step throws, the `@Transactional` annotation rolls everything back — `TaskAllocation` row inserts and stock deductions are atomic.

### 6.7 SOLID dimensions exercised

- **SRP**: each colleague has one responsibility; the mediator owns only sequencing.
- **DIP**: `ResourceService` depends on the `AllocationMediator` interface, not concrete classes.

### 6.8 Assumptions

1. The allocation workflow is synchronous. A later phase could switch `NotificationColleague` to asynchronous dispatch without affecting any other colleague.
2. Audit is log-based rather than a dedicated `audit_log` table. Adding the table would not change the mediator or its colleagues, only the `AuditColleague` implementation.

---

## 7. Pattern 4 — Mediator: Announcement Discussion Room

### 7.1 Problem

Posting a comment on an announcement must, in principle, trigger several side effects: notify the announcement's author, refresh a dashboard counter, relay the event onto the global activity stream, and — in a future phase — email mentioned users or push to WebSocket subscribers. Each side effect should be easy to add or remove without changing the others.

### 7.2 Pattern selection

**Mediator** again, but in its classic "chat room" form. Each side effect becomes a `Participant`; the `AnnouncementRoom` fans every posted comment out to every participant. Participants never reference each other; they cannot directly corrupt each other's state.

### 7.3 UML

![Discussion Room](uml/mediator%20discussion%20room.png)

### 7.4 Participants

| GoF role | Concrete class |
|---|---|
| `Mediator` | `DiscussionRoomMediator` (interface) |
| `ConcreteMediator` | `AnnouncementRoom` |
| `Colleague` | `Participant` (interface) |
| `ConcreteColleague` | `AuthorParticipant`, `DashboardParticipant`, `ActivityRelayParticipant` |
| Client | `AnnouncementCommentService.addComment(...)` |

### 7.5 Implementation

Location: [`service/mediator/discussion/`](../backend/src/main/java/com/constructflow/service/mediator/discussion)

`AnnouncementRoom` maintains two lists: `defaultParticipants` (installed at startup — `AuthorParticipant`, `DashboardParticipant`, `ActivityRelayParticipant`) and `dynamicParticipants` keyed by announcement id (for mention-based subscribers). Posting a comment persists the row and then calls `safeDispatch(p, comment)` for each participant; a misbehaving participant cannot poison dispatch to the others.

`AnnouncementCommentService.addComment` becomes a single mediator call:

```java
@Transactional
public CommentResponseDTO addComment(UUID announcementId, CommentRequestDTO dto) {
    return discussionRoomMediator.post(announcementId, dto);
}
```

### 7.6 SOLID dimensions exercised

- **OCP**: adding an @mention notifier is a new `Participant` implementation plus a `room.join(...)` call; no existing code touched.
- **SRP**: each participant has one reaction to model.

### 7.7 Assumptions

1. The mediator persists the comment synchronously before notifying participants so that participants that read comment state see a consistent view.
2. A single `AnnouncementRoom` bean manages every announcement thread via a `Map<UUID, List<Participant>>`. An alternative (one room instance per announcement) would be identical pattern-wise but wastes memory.
3. "Author" is treated as a string (no user table yet); the notification is a log line. A production deployment would swap `AuthorParticipant` for a real email/push notifier.

---

## 8. Pattern 5 — Strategy: Project-Progress Calculation

### 8.1 Problem

`ProjectService.updateProjectProgress` used a single algorithm — completed task count / total — which is only one of several legitimate progress models for construction projects. Different clients expect different models:

- **TASK_COUNT** — simple and deterministic; the default.
- **WEIGHTED_BY_COST** — weights completed tasks by their `actualCost` contribution.
- **MILESTONE_BASED** — counts project milestones hit (milestone-to-task match by name).
- **EFFORT_BASED** — weights by logged hours via `WorkLog`.

Baking one algorithm into the service blocks the other three.

### 8.2 Pattern selection

**Strategy** — each model is a self-contained algorithm behind a common `ProgressStrategy` interface. The project itself declares which strategy to use via a new `progress_model` column. `ProjectService` asks the resolver for the strategy and delegates the calculation.

### 8.3 UML

![Progress Strategy](uml/strategy%20progress.png)

### 8.4 Participants

| GoF role | Concrete class |
|---|---|
| `Strategy` | `ProgressStrategy` (interface) |
| `ConcreteStrategy` | `TaskCountProgressStrategy` (default), `WeightedByCostProgressStrategy`, `MilestoneBasedProgressStrategy`, `EffortBasedProgressStrategy` |
| `Context` | `ProjectService` (via `ProgressStrategyResolver`) |
| Key | `ProgressModel` enum stored on `Project.progress_model` |

### 8.5 Implementation

Location: [`service/strategy/progress/`](../backend/src/main/java/com/constructflow/service/strategy/progress)

```java
@Transactional
public void updateProjectProgress(UUID projectId) {
    Project project = projectRepository.findById(projectId).orElse(null);
    if (project == null) return;

    List<Task> tasks = taskRepository.findByProjectId(projectId);
    ProgressStrategy strategy = progressStrategyResolver.resolve(project.getProgressModel());
    project.setProgress(strategy.calculate(project, tasks));

    // actualCost continues to use the earlier Composite tree
    WorkItem root = taskTreeBuilder.buildForProject(projectId);
    project.setActualCost(root.actualCost());

    projectRepository.save(project);
}
```

### 8.6 SOLID dimensions exercised

- **OCP**: a new progress model is a new `@Component` plus one enum entry.
- **SRP**: each strategy owns its own formula.
- **DIP**: `ProjectService` depends on `ProgressStrategy` via the resolver.

### 8.7 Assumptions

1. `EffortBasedProgressStrategy` reads `WorkLog.hours` — projects with no work logs fall through to a 0% progress result, not a divide-by-zero error (handled explicitly).
2. `MilestoneBasedProgressStrategy` treats a milestone as hit if any completed task's name contains the milestone text (case-insensitive). Projects that wish to use this strategy are expected to name tasks descriptively.
3. Null / missing `progressModel` on a project defaults to `TASK_COUNT`, preserving pre-existing behaviour.

---

## 9. Pattern 6 — Strategy: Critical-Task Prioritisation

### 9.1 Problem

`GET /api/tasks/critical` returned `priority = "Critical"` tasks in insertion order. Site managers, however, prioritise differently depending on context — sometimes by due date, sometimes by cost exposure, sometimes by how many other tasks depend on them, sometimes by a composite "risk" score.

### 9.2 Pattern selection

**Strategy** once more — this time with a request-time key (`sortBy` query parameter) rather than an entity field.

### 9.3 UML

![Prioritisation Strategy](uml/strategy%20prioristisation.png)

### 9.4 Participants

| GoF role | Concrete class |
|---|---|
| `Strategy` | `PrioritisationStrategy` (interface) |
| `ConcreteStrategy` | `DueDatePrioritisationStrategy` (default), `CostDescPrioritisationStrategy`, `DependencyCountPrioritisationStrategy`, `RiskWeightedPrioritisationStrategy` |
| Key | `PrioritisationKey` enum (`DUE_DATE`, `COST_DESC`, `RISK`, `DEPENDENCIES`) |
| `Context` | `TaskService.getCriticalTasks(sortKey)` |
| Client | `TaskController.getCriticalTasks(sortBy)` (`GET /api/tasks/critical?sortBy=...`) |

### 9.5 Implementation

Location: [`service/strategy/prioritisation/`](../backend/src/main/java/com/constructflow/service/strategy/prioritisation)

```java
public List<TaskResponseDTO> getCriticalTasks(PrioritisationKey sortKey) {
    List<Task> raw = taskRepository.findCriticalTasks();
    return prioritisationResolver.resolve(sortKey).prioritise(raw).stream()
            .map(taskMapper::toResponse)
            .collect(Collectors.toList());
}
```

The risk-weighted strategy blends three factors:

- Urgency (days-to-due, linear fade over two weeks, 10 for overdue)
- Cost exposure (`log10(actualCost)`)
- Dependency fan-out (×2 per dependent)
- × 2 multiplier if `priority == "Critical"` (all candidates in this endpoint already are)

### 9.6 SOLID dimensions exercised

- **OCP**: adding a new ordering (e.g. by assignee workload) is a new `@Component` + enum entry.
- **SRP**: each strategy encodes a single comparator.

### 9.7 Assumptions

1. All candidate tasks are already `priority = "Critical"` at the repository level; the strategies never widen the candidate set.
2. Null `dueDate` sorts last under `DueDatePrioritisationStrategy` — a null due date is treated as "no deadline" rather than "unspecified / treat as urgent".
3. Null `actualCost` sorts as zero cost, not as missing data.

---

## 10. Pattern 7 — Observer + Singleton: Global Activity Hub

### 10.1 Problem

Several cross-cutting concerns — audit trail, dashboard counters, overdue alerting, (future) WebSocket broadcast — all need to react to the same set of system events. Wiring each as a direct dependency of every publishing service would create a ~15-edge mesh. Spring's event bus partially solves this but is invisible outside the Spring container; a manual, textbook implementation makes the pattern explicit.

### 10.2 Pattern selection

**Observer** for fan-out; **Singleton** to guarantee exactly one hub instance. Two patterns are used together in one design because the problem genuinely requires both: fan-out alone with multiple hubs would let observers miss events; a singleton without an Observer contract gives no dispatch semantics.

The Singleton is implemented as an enum — the idiom recommended in *Effective Java* (Item 3) because:

- The JVM guarantees exactly one instance per classloader.
- Initialisation is thread-safe without synchronization.
- Reflection attacks are blocked (`Constructor.newInstance` on an enum throws `IllegalArgumentException`).
- Serialisation round-trips return the same instance automatically.

### 10.3 UML

![Activity Hub](uml/observer%20singleton%20activity%20hub.png)

### 10.4 Participants

| GoF role | Concrete class |
|---|---|
| `Subject` (Observer) + `Singleton` | `ActivityHub` (enum with a single `INSTANCE` value) |
| `Observer` | `ActivityObserver` (interface) |
| `ConcreteObserver` | `AuditLogObserver`, `DashboardCounterObserver`, `OverdueAlertObserver` |
| Event model | `Activity` (sealed interface) with record variants: `TaskCreated`, `TaskCompleted`, `TaskOverdue`, `ResourceAllocated`, `CommentPosted` |
| Publishers | `TaskService`, `NotificationColleague` (inside the allocation mediator), `ActivityRelayParticipant` (inside the discussion-room mediator) |

### 10.5 Implementation

Location: [`service/observer/`](../backend/src/main/java/com/constructflow/service/observer)

Singleton:

```java
public enum ActivityHub {
    INSTANCE;

    private final List<ActivityObserver> observers = new CopyOnWriteArrayList<>();

    public void subscribe(ActivityObserver observer)   { observers.addIfAbsent(observer); }
    public void unsubscribe(ActivityObserver observer) { observers.remove(observer); }
    public void publish(Activity activity) {
        for (ActivityObserver o : observers) {
            try { o.onActivity(activity); }
            catch (RuntimeException ex) { /* log + carry on */ }
        }
    }
}
```

Observers self-register in `@PostConstruct`:

```java
@Component
public class AuditLogObserver implements ActivityObserver {
    @PostConstruct public void register() { ActivityHub.INSTANCE.subscribe(this); }
    @Override public void onActivity(Activity a) { /* write audit line */ }
}
```

Publishing:

```java
// TaskService.createTask
ActivityHub.INSTANCE.publish(new Activity.TaskCreated(saved.getId(), saved.getProjectId(), saved.getName()));
```

### 10.6 SOLID dimensions exercised

- **OCP**: adding an observer is a new class; no existing publisher or observer is touched.
- **SRP**: the hub owns dispatch; observers own their own reactions; publishers own their own business logic.
- **DIP**: publishers depend on the `ActivityHub` abstraction (an enum type with a fixed public API), not on the concrete observers.

### 10.7 Testing

[`ActivityHubTest`](../backend/src/test/java/com/constructflow/service/observer/ActivityHubTest.java) (JUnit 5, 9 test cases) proves:

- **Singleton invariants**: `INSTANCE` is reference-equal across lookups; `values()` contains exactly one instance; reflection through `Constructor.newInstance` is blocked; serialisation round-trip returns the same instance.
- **Observer contract**: subscribed observers receive published events; all observers receive the same event reference; unsubscribed observers stop receiving; a throwing observer does not block downstream observers.

### 10.8 Assumptions

1. Dispatch is synchronous. A misbehaving observer is caught and logged, not retried. If any observer needs long-running work, it must dispatch the work onto its own executor.
2. The hub maintains its subscriber list in a `CopyOnWriteArrayList`, optimised for read-heavy traffic (many publishes, rare subscribe/unsubscribe).
3. Spring-managed observers are ordinary `@Component`s that self-register in `@PostConstruct`. There are no bean ordering concerns because observers do not depend on the order of registration.
4. The hub survives the lifetime of the JVM; `clear()` is exposed for tests only.

---

## 11. Pattern 8 — Iterator: Lazy Repository Traversal

### 11.1 Problem

Reporting and analytics code originally called `repository.findAll()` before filtering — memory use scaled with table size. Report generation should not load a million rows to compute a counter.

### 11.2 Pattern selection

**Iterator** — expose traversal behind `hasNext()` / `next()` while the underlying fetch pages transparently. Callers get constant memory use and idiomatic `for (T t : iterable)` usage.

Iterator is delivered as part of the earlier structural/creational phase (commit `85dcf06`) but is catalogued as a behavioural pattern in the GoF taxonomy, so it is documented here for completeness.

### 11.3 UML

![Iterator](uml/iterator%20paged%20repo.png)

### 11.4 Participants

| GoF role | Concrete class |
|---|---|
| `Iterator` | `java.util.Iterator<T>` (built-in) |
| `ConcreteIterator` | `PagedRepositoryIterator<T>` (generic), `ProjectTaskTreeIterator` (domain-specific) |
| `Aggregate` | `java.lang.Iterable<T>` (built-in) |
| `ConcreteAggregate` | `ProjectScanner` |
| Clients | `GlobalReportService`, `ReportService`, `AnalyticsService` |

### 11.5 Implementation

Location: [`service/iteration/`](../backend/src/main/java/com/constructflow/service/iteration)

```java
public class PagedRepositoryIterator<T> implements Iterator<T> {
    private final Function<Pageable, Page<T>> pageFetcher;
    private final int pageSize;
    private Page<T> currentPage;
    private Iterator<T> inner;
    private int nextPageIndex = 0;

    public PagedRepositoryIterator(Function<Pageable, Page<T>> fetcher, int pageSize) {
        this.pageFetcher = fetcher;
        this.pageSize = pageSize;
        advance();
    }
    // hasNext() / next() / advance() chain the pages
}
```

`ProjectScanner implements Iterable<Project>` so a client can simply:

```java
for (Project p : projectScanner) {
    // process one project at a time, DB page-by-page
}
```

`ProjectTaskTreeIterator` composes the project iterator with per-project task fetches, yielding every `Task` across every `Project` as a single stream.

### 11.6 Assumptions

1. Page size 100 is a reasonable default for typical table sizes; very large rows (documents) would want a smaller page.
2. The iterators are **not** thread-safe; each is intended for use within a single request.
3. Deletions during iteration may cause the iterator to skip rows (standard Spring Data `Page` semantics). This is acceptable for read-only reporting.

---

## 12. Integration Notes

The eight cases are not island implementations; they cooperate through well-defined seams.

- The **allocation Mediator (3)** uses the **allocation Template Method (2)** via `ResourceColleague.reserve(...)` and the **Observer+Singleton (7)** via `NotificationColleague.broadcast(...)`. Those three patterns together replace one 40-line `ResourceService.allocateResource` method with three focused collaborators.
- The **discussion Mediator (4)** relays comments onto the **Observer+Singleton (7)** via `ActivityRelayParticipant`, which means any future subscriber of `ActivityHub` automatically sees comment events without touching the mediator.
- The **progress Strategy (5)** composes cleanly with the earlier **Composite** pattern: the strategy computes the progress percentage, the composite tree computes the cost rollup, and `ProjectService.updateProjectProgress` orchestrates both.
- The **Iterator (8)** is used by the earlier Abstract Factory (`ReportService`) when it builds the `ReportContext`, so even reports that haven't yet been written benefit from the constant-memory traversal.

Spring's event bus (`TaskMutatedEvent` + `@TransactionalEventListener`, from the earlier phase) coexists with the manual `ActivityHub`. They serve distinct purposes: the Spring event bus handles intra-request progress recalculation with transactional semantics; the `ActivityHub` handles cross-cutting broadcast with no transactional coupling. Both fire for the same user actions; observers pick whichever contract suits their concern.

---

## 13. Testing

### 13.1 ActivityHubTest

[`backend/src/test/java/com/constructflow/service/observer/ActivityHubTest.java`](../backend/src/test/java/com/constructflow/service/observer/ActivityHubTest.java) runs under JUnit 5 (already a transitive dependency of `spring-boot-starter-test`). Coverage:

| # | Test | What it proves |
|---|---|---|
| 1 | `sameInstanceAcrossLookups` | `INSTANCE` identity is stable |
| 2 | `enumValuesContainsExactlyOneInstance` | Only one value in the enum |
| 3 | `reflectionCannotInstantiateASecondHub` | JDK-level defence against reflection attacks on enum singletons |
| 4 | `serialisationRoundTripReturnsSameInstance` | Serialisation does not spawn a second instance |
| 5 | `subscribedObserversReceivePublishedActivities` | Basic observer dispatch works |
| 6 | `allRegisteredObserversReceiveTheSameEvent` | Fan-out is to the same object reference |
| 7 | `unsubscribedObserverStopsReceivingEvents` | `unsubscribe` actually unsubscribes |
| 8 | `misbehavingObserverDoesNotPreventOthersFromReceiving` | Exception isolation between observers |

Execution:

```bash
cd backend
mvn test -Dtest=ActivityHubTest
```

### 13.2 Additional testing

A full Spring integration test suite is out of scope for this phase. Manual smoke tests have been run for each new endpoint (§14). Future work could add:

- Integration tests on the two new REST endpoints (`/documents/{id}/export`, `/tasks/critical?sortBy=...`).
- Unit tests covering every `ProgressStrategy` and `PrioritisationStrategy` implementation against synthetic fixtures.
- Integration tests on the two mediators exercising transactional rollback on colleague failure.

---

## 14. Build and Run

### 14.1 Prerequisites

- JDK 17 (the `pom.xml` compiles to Java 17; Java 21 is compatible).
- Apache Maven 3.8+
- Microsoft SQL Server 2019+

### 14.2 Build

```bash
cd backend
mvn clean package
```

### 14.3 Run the unit tests

```bash
mvn test                         # full suite
mvn test -Dtest=ActivityHubTest  # just the singleton + observer test
```

### 14.4 Start the backend

```bash
mvn spring-boot:run
```

### 14.5 Smoke-test the new endpoints

```bash
# Export a document as CSV
curl -OJ "http://localhost:8080/api/documents/{documentId}/export?format=CSV&requestedBy=omar"

# Critical tasks ordered by composite risk score
curl "http://localhost:8080/api/tasks/critical?sortBy=RISK"
```

### 14.6 NetBeans

The project is a standard Maven project — it opens unchanged in NetBeans via *File → Open Project → backend*. No additional NetBeans configuration is required.

---

## 15. Commit Timeline

Every behavioural-pattern case shipped as its own atomic commit on `main`.

| Commit | Scope |
|---|---|
| `63c77ab` | Template Method — Document Export pipeline |
| `da7a126` | Template Method — Allocation Validator pipeline |
| `4faf155` | Strategy — Progress calculation |
| `35ae7bc` | Strategy — Critical-task prioritisation |
| `af7b93e` | Observer + Singleton — `ActivityHub` + 3 observers + 9 test cases |
| `64bb549` | Mediator — Resource-allocation workflow |
| `4cb5814` | Mediator — Announcement discussion room |
| `b535c20` | PlantUML diagrams (initial) |
| `d1d7039` | PlantUML syntax fixes + rendered PNGs + per-diagram explainer |
| `71da0a6` | README update documenting this phase |

---

## 16. Assumptions

Collected from every section above, in one place.

1. **Document export** works on full in-memory byte arrays — adequate for typical document sizes.
2. **Document export (PDF)** is a stub that prepends a minimal header; a production implementation would drop in iText / PDFBox. The pattern structure is unaffected.
3. **Allocation validator** registration is keyed by the existing `Resource.category` string; unknown categories fall through to the strictest (Material) validator.
4. **Allocation mediator** is synchronous and transactional; a failure in any colleague rolls back the entire operation.
5. **Discussion room** persists the comment before notifying participants so every participant sees committed state.
6. **Discussion room** uses a single `AnnouncementRoom` bean keyed by announcement id rather than one room bean per announcement.
7. **Progress strategy** defaults to `TASK_COUNT` when `Project.progressModel` is null, preserving legacy behaviour.
8. **Milestone strategy** relies on milestone text appearing in task names; projects opting into it name their tasks descriptively.
9. **Effort strategy** requires `WorkLog` entries; a project with no logs reports 0% progress rather than a divide-by-zero.
10. **Prioritisation strategy** sorts null `dueDate` last and treats null `actualCost` as zero.
11. **Risk-weighted score** is a weighted blend chosen for readability, not calibrated against a specific construction-industry heuristic.
12. **ActivityHub** dispatch is synchronous; observers needing to block should hand off to their own executors.
13. **ActivityHub** is enum-based — no attempt has been made to support Spring-provided observers in a container-managed lifecycle beyond self-registration in `@PostConstruct`.
14. **Iterator** page size of 100 is a reasonable default; bulkier rows (documents) would want a smaller page.
15. **Iterator** instances are not thread-safe; each is scoped to a single request.
16. **Database** column `progress_model` is nullable; existing rows keep behaving as they did before this phase.

---

## 17. References

1. Gamma, Helm, Johnson, Vlissides. *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994.
2. Joshua Bloch. *Effective Java, 3rd Edition*. Addison-Wesley, 2018 — Item 3 ("Enforce the singleton property with a private constructor or an enum type").
3. Robert C. Martin. *Clean Architecture*. Prentice Hall, 2017.
4. Spring Framework Reference Documentation — ApplicationEventPublisher, `@TransactionalEventListener`, `@ConditionalOnProperty`.
5. ConstructFlow internal documentation:
   - [`docs/ARCHITECTURE_AND_PATTERNS.md`](ARCHITECTURE_AND_PATTERNS.md) — earlier SOLID + creational/structural phase.
   - [`docs/uml/README.md`](uml/README.md) — per-diagram explainer.
   - Root [`README.md`](../README.md) — §🎭 Behavioural Design Patterns.

---

*End of report — 2026-04-21.*
