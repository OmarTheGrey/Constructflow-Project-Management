# UML Class Diagrams

Each diagram below documents one design-pattern case that is live in the ConstructFlow Java backend. The `.puml` file is the source of truth; the PNG next to it is the rendered class diagram.

---

## 1. Template Method — Document Export pipeline

![Document Export](template%20method%20document%20export.png)

**Source**: [01_template_method_document_export.puml](01_template_method_document_export.puml)
**Lives in**: [`service/template/export/`](../../backend/src/main/java/com/constructflow/service/template/export)

`AbstractDocumentExporter.export(ExportRequest)` is the final template method. It fixes the five-step export algorithm — validate access → resolve document → load content → transform → log — while leaving `transform`, `contentType`, and `fileExtension` abstract. The three concrete exporters (`PdfDocumentExporter`, `CsvDocumentExporter`, `ZipArchiveExporter`) plug in their format-specific logic only. `DocumentExporterResolver` keyed by the `ExportFormat` enum dispatches the right exporter when a request hits `GET /api/documents/{id}/export?format=PDF|CSV|ZIP` on `DocumentController`. Adding a new format (e.g. XLSX) is a new subclass plus one enum entry — no changes to the controller, the resolver, or existing exporters.

---

## 2. Template Method — Resource-allocation validation pipeline

![Allocation Validator](template%20method%20resource%20allocation.png)

**Source**: [02_template_method_allocation_validator.puml](02_template_method_allocation_validator.puml)
**Lives in**: [`service/template/allocation/`](../../backend/src/main/java/com/constructflow/service/template/allocation)

`AbstractAllocationValidator.validate(AllocationRequest)` is the final template method that enforces the universal skeleton — basic input checks → resolve the resource → availability check → category-specific rule → optional budget hook. The three subclasses each implement a different category rule: `MaterialAllocationValidator` refuses to drop stock below the reorder buffer; `EquipmentAllocationValidator` rejects fractional units and fully-allocated items; `LaborAllocationValidator` caps a single booking at 12 hours. `AllocationValidatorRegistry` routes incoming requests to the right subclass by `resource.getCategory()`. The concrete `ResourceColleague` (see diagram 3) calls the registry inside the allocation mediator's workflow.

---

## 3. Mediator — Resource-allocation workflow

![Allocation Mediator](mediator%20resource%20allocation.png)

**Source**: [03_mediator_allocation.puml](03_mediator_allocation.puml)
**Lives in**: [`service/mediator/allocation/`](../../backend/src/main/java/com/constructflow/service/mediator/allocation)

`AllocationMediator` is the interface; `DefaultAllocationMediator` is the concrete mediator that sequences the four colleagues — `ResourceColleague` (validates and deducts stock), `TaskAllocationColleague` (persists the allocation row), `NotificationColleague` (broadcasts a `ResourceAllocated` activity through the hub in diagram 7), and `AuditColleague` (writes the audit log line). None of the colleagues reference one another; they each talk only to the mediator. `ResourceService.allocateResource` is therefore a single line — `allocationMediator.allocate(command)` — and the business rule "validate → reserve → record → notify → audit" is encoded in one place instead of scattered across services.

---

## 4. Mediator — Announcement discussion room

![Discussion Room](mediator%20discussion%20room.png)

**Source**: [04_mediator_discussion_room.puml](04_mediator_discussion_room.puml)
**Lives in**: [`service/mediator/discussion/`](../../backend/src/main/java/com/constructflow/service/mediator/discussion)

`DiscussionRoomMediator` is the interface; `AnnouncementRoom` is the concrete mediator that manages every announcement thread. Posting a comment persists the row and then dispatches `onCommentPosted(...)` to every `Participant` registered for that room. Three default participants are installed at startup: `AuthorParticipant` (notifies the original announcement author), `DashboardParticipant` (keeps an in-memory comment-count cache per announcement), and `ActivityRelayParticipant` (forwards the post onto the global `ActivityHub`). Extra participants (e.g. @mention handlers, email subscribers) can `join` and `leave` individual rooms at runtime. `AnnouncementCommentService.addComment` is now a thin forwarder that hands off to the mediator.

---

## 5. Strategy — Project-progress calculation

![Progress Strategy](strategy%20progress.png)

**Source**: [05_strategy_progress.puml](05_strategy_progress.puml)
**Lives in**: [`service/strategy/progress/`](../../backend/src/main/java/com/constructflow/service/strategy/progress)

`ProgressStrategy` is the common interface; four concrete strategies are interchangeable behind it — `TaskCountProgressStrategy` (the default: completed / total), `WeightedByCostProgressStrategy` (share of `actualCost` booked against completed tasks), `MilestoneBasedProgressStrategy` (share of milestones hit), and `EffortBasedProgressStrategy` (share of logged hours on completed tasks). Each project picks its model by storing a `ProgressModel` enum value on the entity (the new `progress_model` column). `ProgressStrategyResolver` resolves the right bean for a given project. `ProjectService.updateProjectProgress` delegates to the resolved strategy, so changing a project's progress model is a data change, not a code change.

---

## 6. Strategy — Critical-task prioritisation

![Prioritisation Strategy](strategy%20prioristisation.png)

**Source**: [06_strategy_prioritisation.puml](06_strategy_prioritisation.puml)
**Lives in**: [`service/strategy/prioritisation/`](../../backend/src/main/java/com/constructflow/service/strategy/prioritisation)

`PrioritisationStrategy` has four interchangeable implementations: `DueDatePrioritisationStrategy` (earliest first, the default), `CostDescPrioritisationStrategy` (highest cost first), `DependencyCountPrioritisationStrategy` (most dependents first), and `RiskWeightedPrioritisationStrategy` (a composite score that blends urgency, cost, and dependency fan-out). `PrioritisationStrategyResolver` looks up the strategy by a `PrioritisationKey` enum. The frontend picks the ordering via the `sortBy` query parameter on `GET /api/tasks/critical?sortBy=RISK`; `TaskController` forwards the enum into `TaskService.getCriticalTasks(sortKey)`, which runs the repository query and then hands the results to the resolved strategy.

---

## 7. Observer + Singleton — Global activity stream (`ActivityHub`)

![Activity Hub](observer%20singleton%20activity%20hub.png)

**Source**: [07_observer_singleton_activity_hub.puml](07_observer_singleton_activity_hub.puml)
**Lives in**: [`service/observer/`](../../backend/src/main/java/com/constructflow/service/observer)

`ActivityHub` is an enum-based singleton (`public enum ActivityHub { INSTANCE; ... }`) — the JVM guarantees a single instance, the initialisation is thread-safe without locks, and the class is immune to reflection and serialisation attacks. It holds a `CopyOnWriteArrayList<ActivityObserver>` so subscribers register themselves once in `@PostConstruct` and then receive every published `Activity`. The sealed `Activity` interface has five record variants — `TaskCreated`, `TaskCompleted`, `TaskOverdue`, `ResourceAllocated`, `CommentPosted` — published by `TaskService`, the allocation `NotificationColleague`, and `ActivityRelayParticipant` inside `AnnouncementRoom`. Three observers ship: `AuditLogObserver` (append-only audit trail), `DashboardCounterObserver` (in-memory counters by event type), and `OverdueAlertObserver` (raises WARN-level alerts for `TaskOverdue`). `ActivityHubTest` proves the singleton invariants (same instance, reflection-proof, serialisation round-trip) and the observer contract (fan-out, unsubscribe, isolation from a misbehaving observer).

---

## 8. Iterator — Lazy traversal of projects and tasks

![Paged Iterator](iterator%20paged%20repo.png)

**Source**: [08_iterator_paged_repository.puml](08_iterator_paged_repository.puml)
**Lives in**: [`service/iteration/`](../../backend/src/main/java/com/constructflow/service/iteration)

`PagedRepositoryIterator<T>` implements `java.util.Iterator<T>` and wraps any `Function<Pageable, Page<T>>` — it fetches one page at a time (default 100 rows) and advances transparently. `ProjectScanner` is the `Iterable<Project>` bean wrapping the project repository, so callers write `for (Project p : projectScanner)` without ever materialising the whole table. `ProjectTaskTreeIterator` composes a project iterator with per-project task queries, yielding every `Task` across every `Project` as a single stream. `GlobalReportService`, `ReportService`, and `AnalyticsService` all iterate through these instead of `findAll()`, keeping memory use constant regardless of database size.
