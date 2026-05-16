# ConstructFlow — Master Technical Documentation

> **Classification:** Internal Technical Reference  
> **Version:** 2.0.0  
> **Authors:** CTRL ALT ELITE  
> **Last Updated:** April 2026  
> **Stack:** Spring Boot 3.2.1 · Java 21 · Next.js 14 · MS SQL Server

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [System Architecture](#2-system-architecture)
   - 2.1 [Three-Tier Architecture Overview](#21-three-tier-architecture-overview)
   - 2.2 [Reference Architecture Block Diagram](#22-reference-architecture-block-diagram)
   - 2.3 [Component Diagram](#23-component-diagram)
   - 2.4 [Deployment Diagram](#24-deployment-diagram)
3. [Technology Stack](#3-technology-stack)
4. [Database Design](#4-database-design)
   - 4.1 [Entity Relationship Overview](#41-entity-relationship-overview)
   - 4.2 [Table Schemas](#42-table-schemas)
   - 4.3 [SQL Query Reference](#43-sql-query-reference)
5. [Backend Architecture](#5-backend-architecture)
   - 5.1 [Package Structure](#51-package-structure)
   - 5.2 [Domain Model](#52-domain-model)
   - 5.3 [Service Layer](#53-service-layer)
   - 5.4 [REST API Reference](#54-rest-api-reference)
6. [SOLID Compliance Audit](#6-solid-compliance-audit)
7. [Design Patterns — Phase 1: Creational & Structural](#7-design-patterns--phase-1-creational--structural)
   - 7.1 [Adapter — Document Storage](#71-adapter--document-storage)
   - 7.2 [Factory Method — Entity Construction](#72-factory-method--entity-construction)
   - 7.3 [Abstract Factory — Report Families](#73-abstract-factory--report-families)
   - 7.4 [Iterator — Lazy Repository Traversal](#74-iterator--lazy-repository-traversal)
   - 7.5 [Composite — Task Hierarchy](#75-composite--task-hierarchy)
8. [Design Patterns — Phase 2: Behavioural](#8-design-patterns--phase-2-behavioural)
   - 8.1 [Template Method — Document Export](#81-template-method--document-export)
   - 8.2 [Template Method — Allocation Validation](#82-template-method--allocation-validation)
   - 8.3 [Mediator — Resource Allocation Workflow](#83-mediator--resource-allocation-workflow)
   - 8.4 [Mediator — Discussion Room](#84-mediator--discussion-room)
   - 8.5 [Strategy — Project Progress Calculation](#85-strategy--project-progress-calculation)
   - 8.6 [Strategy — Critical Task Prioritisation](#86-strategy--critical-task-prioritisation)
   - 8.7 [Observer + Singleton — Global Activity Hub](#87-observer--singleton--global-activity-hub)
9. [UML Diagrams](#9-uml-diagrams)
   - 9.1 [Sequence Diagram — Resource Allocation Flow](#91-sequence-diagram--resource-allocation-flow)
   - 9.2 [Activity Diagram — Allocation Workflow](#92-activity-diagram--allocation-workflow)
   - 9.3 [Class Diagrams — All Patterns](#93-class-diagrams--all-patterns)
10. [Application Features & UI Showcase](#10-application-features--ui-showcase)
11. [Setup & Installation Guide](#11-setup--installation-guide)
12. [Testing](#12-testing)
13. [Pattern Integration Map](#13-pattern-integration-map)
14. [Assumptions & Design Decisions](#14-assumptions--design-decisions)
15. [References](#15-references)

---

## 1. Executive Summary

**ConstructFlow** is a full-stack construction project management platform engineered for real-world site operations. It provides project teams, site managers, and executives with a unified environment to track projects, allocate resources, manage documentation, generate reports, and communicate through structured announcement threads.

### What Makes ConstructFlow Different

| Dimension            | Approach                                                                                                         |
| -------------------- | ---------------------------------------------------------------------------------------------------------------- |
| **Architecture**     | Clean three-tier separation: Next.js frontend · Spring Boot API · MS SQL Server                                  |
| **Design Quality**   | 12 GoF design patterns across two dedicated refactor phases                                                      |
| **SOLID Compliance** | 13 catalogued violations identified and closed before pattern work began                                         |
| **Extensibility**    | Every major algorithm (progress models, sort orders, export formats) is swappable without touching existing code |
| **Data Safety**      | Optimistic locking on all entities · ACID-compliant transactions · typed domain exceptions                       |
| **Observability**    | Enum-based `ActivityHub` singleton streams typed events to pluggable audit, dashboard, and alert observers       |

### System at a Glance

![](C:\Users\dr_sh\AppData\Roaming\marktext\images\2026-05-16-23-03-14-Block%20Diagram.png)

```
┌─────────────────────────────────────────────────────────────────┐
│                        ConstructFlow                             │
│                                                                  │
│  Next.js 14 UI  ──HTTP/JSON──▶  Spring Boot API  ──JDBC──▶  SQL │
│  TypeScript 5                   Java 21                  Server  │
│  Tailwind CSS                   Hibernate ORM            2019+   │
│  React Context                  12 GoF Patterns                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. System Architecture

### 2.1 Three-Tier Architecture Overview

ConstructFlow follows a strict **three-tier architecture**. Each tier has a single, well-defined responsibility. Tiers communicate only with their adjacent neighbour — the frontend never queries the database directly, and the database layer is never invoked from controllers.

```
╔══════════════════════════════════════════════════════════╗
║              TIER 1 — PRESENTATION LAYER                 ║
║                                                          ║
║   ┌──────────────────────────────────────────────────┐  ║
║   │  Next.js 14 / React 19 / TypeScript 5            │  ║
║   │  Tailwind CSS · Radix UI · Recharts · Axios      │  ║
║   │                                                  │  ║
║   │  Pages: Dashboard · Projects · Tasks ·           │  ║
║   │         Resources · Documents · Reports ·        │  ║
║   │         Announcements · Analytics                │  ║
║   └──────────────────────────────────────────────────┘  ║
║                          │ HTTP/JSON                     ║
╠══════════════════════════╪═══════════════════════════════╣
║         TIER 2 — APPLICATION / BUSINESS LOGIC LAYER      ║
║                          │                               ║
║   ┌──────────────────────▼───────────────────────────┐  ║
║   │  Spring Boot 3.2.1 REST API (Port 8080)          │  ║
║   │                                                  │  ║
║   │  Controllers  ──▶  Services  ──▶  Repositories   │  ║
║   │                                                  │  ║
║   │  ┌─────────────────────────────────────────┐    │  ║
║   │  │  Design Pattern Layer                   │    │  ║
║   │  │  Template Method · Mediator · Strategy  │    │  ║
║   │  │  Observer+Singleton · Iterator          │    │  ║
║   │  │  Adapter · Factory · Composite          │    │  ║
║   │  └─────────────────────────────────────────┘    │  ║
║   └──────────────────────────────────────────────────┘  ║
║                          │ JDBC / TCP 1433              ║
╠══════════════════════════╪═══════════════════════════════╣
║              TIER 3 — DATA PERSISTENCE LAYER             ║
║                          │                               ║
║   ┌──────────────────────▼───────────────────────────┐  ║
║   │  Microsoft SQL Server 2019+                      │  ║
║   │  Hibernate ORM / Spring Data JPA                │  ║
║   │  11 tables · Optimistic Locking · ACID           │  ║
║   └──────────────────────────────────────────────────┘  ║
╚══════════════════════════════════════════════════════════╝
```

### 2.2 Reference Architecture Block Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│         WIS Reference Architecture: ConstructFlow Mapping          │
│                                                                    │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │  TIER 1: Presentation Layer                                 │  │
│  │  ┌──────────────────┐    ┌──────────────────────────────┐  │  │
│  │  │  Next.js React UI│    │  Handles user input & display │  │  │
│  │  └──────────────────┘    └──────────────────────────────┘  │  │
│  └─────────────────────────────┬───────────────────────────────┘  │
│                    HTTP/JSON Requests ↕                            │
│  ┌─────────────────────────────▼───────────────────────────────┐  │
│  │  TIER 2: Application / Business Logic Layer                 │  │
│  │  ┌──────────────────────┐  ┌────────────────────────────┐   │  │
│  │  │  Spring Boot REST API│  │  Enforces construction     │   │  │
│  │  ├──────────────────────┤  │  business rules            │   │  │
│  │  │  Services & Strategy │  └────────────────────────────┘   │  │
│  │  │  /Mediator Patterns  │                                    │  │
│  │  └──────────────────────┘                                    │  │
│  └─────────────────────────────┬───────────────────────────────┘  │
│                    Database Queries ↕                              │
│  ┌─────────────────────────────▼───────────────────────────────┐  │
│  │  TIER 3: Data Persistence Layer                             │  │
│  │  ┌──────────────┐  ┌──────────────────┐  ┌─────────────┐   │  │
│  │  │ Hibernate ORM│  │  MS SQL Server   │  │ ACID        │   │  │
│  │  └──────────────┘  └──────────────────┘  │ Compliant   │   │  │
│  │                                           └─────────────┘   │  │
│  └─────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────┘
```

### 2.3 Component Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                    FRONTEND APPLICATION                          │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │            Next.js Client Components                       │  │
│  │   [Dashboard] [Projects] [Tasks] [Resources] [Reports]     │  │
│  └────────────────────────────────────────────────────────────┘  │
└───────────────────────────┬──────────────────────────────────────┘
                            │ REST / JSON
┌───────────────────────────▼──────────────────────────────────────┐
│                   SPRING BOOT BACKEND                            │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                   REST Controllers                        │   │
│  │  Project · Task · Resource · Announcement · Analytics    │   │
│  │  Document · Report · DailyReport · Stakeholder · Search  │   │
│  └───────────────────────────┬──────────────────────────────┘   │
│                              │ uses (via interfaces)             │
│  ┌───────────────────────────▼──────────────────────────────┐   │
│  │  «interface» Service Interfaces                          │   │
│  └───────────────┬───────────────────────────────────────────┘   │
│                  │ implements                                     │
│  ┌───────────────▼──────────────────┐  ┌──────────────────────┐  │
│  │  Business Services & Mediators   │  │ «interface»          │  │
│  │  Strategy Resolvers · Observers  │  │ Repository Interfaces │  │
│  └───────────────┬──────────────────┘  └──────┬───────────────┘  │
│                  │ uses                        │ implements       │
│                  │             ┌───────────────▼───────────────┐  │
│                  └────────────▶│  Spring Data JPA Repositories │  │
│                                └───────────────┬───────────────┘  │
└────────────────────────────────────────────────┼─────────────────┘
                                                 │ JDBC
┌────────────────────────────────────────────────▼─────────────────┐
│                    MICROSOFT SQL SERVER                           │
│              ConstructFlowDB · 11 Tables · ACID                   │
└──────────────────────────────────────────────────────────────────┘
```

### 2.4 Deployment Diagram

```
┌──────────────────────────┐        ┌──────────────────────────────┐
│  User Device             │        │  Application Server Node     │
│  «Client Node»           │        │  «Ubuntu / Linux»            │
│                          │        │                              │
│  ┌────────────────────┐  │        │  ┌──────────────────────┐   │
│  │   Web Browser      │  │        │  │  SpringBoot Embedded  │   │
│  │                    │  │HTTPS   │  │  Webserver (Tomcat)   │   │
│  │  ┌──────────────┐  │  │◀──────▶│  │                      │   │
│  │  │  Compiled    │  │  │Port 443│  │  ┌────────────────┐  │   │
│  │  │  Next.js     │  │  │        │  │  │constructflow-  │  │   │
│  │  │  HTML/JS     │  │  │        │  │  │backend.jar     │  │   │
│  │  └──────────────┘  │  │        │  │  └────────────────┘  │   │
│  └────────────────────┘  │        │  └──────────────────────┘   │
└──────────────────────────┘        └──────────────┬───────────────┘
                                                   │ TCP/IP · Port 1433
                                    ┌──────────────▼───────────────┐
                                    │  Database Server Node        │
                                    │  «Windows Server»            │
                                    │                              │
                                    │  ┌────────────────────────┐  │
                                    │  │  MS SQL Server 2019+   │  │
                                    │  │                        │  │
                                    │  │  ┌──────────────────┐  │  │
                                    │  │  │ ConstructFlowDB  │  │  │
                                    │  │  │ (11 tables)      │  │  │
                                    │  │  └──────────────────┘  │  │
                                    │  └────────────────────────┘  │
                                    └──────────────────────────────┘
```

---

## 3. Technology Stack

### Backend

| Component   | Technology                             | Version |
| ----------- | -------------------------------------- | ------- |
| Framework   | Spring Boot                            | 3.2.1   |
| Language    | Java                                   | 21      |
| ORM         | Hibernate / Spring Data JPA            | —       |
| Database    | Microsoft SQL Server                   | 2019+   |
| Build Tool  | Maven                                  | 3.8+    |
| Validation  | Jakarta Bean Validation                | —       |
| Boilerplate | Lombok                                 | —       |
| Testing     | JUnit 5 (via spring-boot-starter-test) | —       |
| UML         | PlantUML                               | —       |

### Frontend

| Component   | Technology            | Version |
| ----------- | --------------------- | ------- |
| Framework   | Next.js               | 14      |
| UI Library  | React                 | 19      |
| Language    | TypeScript            | 5       |
| Styling     | Tailwind CSS          | 4       |
| Components  | Radix UI              | —       |
| HTTP Client | Axios                 | —       |
| Charts      | Recharts              | —       |
| Forms       | React Hook Form + Zod | —       |

---

## 4. Database Design

### 4.1 Entity Relationship Overview

```
                           ┌──────────────┐
                           │   projects   │
                           │──────────────│
                           │ id (PK UUID) │
                           │ name         │
                           │ client       │
                           │ budget       │
                           │ actual_cost  │
                           │ status       │
                           │ progress     │
                           │ progress_    │
                           │   model ◀───────────── (Strategy enum)
                           └──────┬───────┘
              ┌───────────────────┼──────────────────────┐
              │                   │                      │
    ┌─────────▼──────┐   ┌───────▼────────┐   ┌────────▼────────┐
    │     tasks      │   │   resources    │   │  daily_reports  │
    │────────────────│   │────────────────│   │─────────────────│
    │ id (PK)        │   │ id (PK)        │   │ id (PK)         │
    │ project_id (FK)│   │ project_id (FK)│   │ project_id (FK) │
    │ parent_task_id │   │ name           │   │ report_date     │
    │ name           │   │ category       │   │ work_summary    │
    │ assignee       │   │ quantity       │   │ issues          │
    │ due_date       │   │ cost           │   │ weather_conds   │
    │ status         │   └───────┬────────┘   └─────────────────┘
    │ priority       │           │
    │ actual_cost    │   ┌───────▼────────────┐
    └──────┬─────────┘   │  task_allocations  │
           │             │────────────────────│
      ┌────┴────┐        │ task_id (FK)       │
      │         │        │ resource_id (FK)   │
┌─────▼───┐ ┌──▼──────┐  │ quantity           │
│daily_   │ │work_logs│  └────────────────────┘
│logs     │ │─────────│
│─────────│ │task_id  │   ┌─────────────────┐
│task_id  │ │worker_  │   │  announcements  │
│entry_   │ │  name   │   │─────────────────│
│  date   │ │hours_   │   │ id (PK)         │
│hours_   │ │  worked │   │ title           │
│  worked │ └─────────┘   │ content         │
└─────────┘               │ priority        │
                          └────────┬────────┘
                                   │
                    ┌──────────────▼──────────────┐
                    │   announcement_comments      │
                    │─────────────────────────────│
                    │ id (PK)                      │
                    │ announcement_id (FK)          │
                    │ author · content             │
                    └──────────────────────────────┘

projects also has: stakeholders (1→N), documents (1→N)
tasks: self-referential parent_task_id (Composite pattern)
```

### 4.2 Table Schemas

#### `projects`

| Column             | Type             | Notes                                     |
| ------------------ | ---------------- | ----------------------------------------- |
| `id`               | UNIQUEIDENTIFIER | PK, auto-generated UUID                   |
| `name`             | VARCHAR(255)     | Project name                              |
| `client`           | VARCHAR(255)     | Client name                               |
| `location`         | VARCHAR(255)     | Site location                             |
| `budget`           | DECIMAL(19,2)    | Total budget                              |
| `actual_cost`      | DECIMAL(19,2)    | Running actual cost                       |
| `start_date`       | DATE             | —                                         |
| `end_date`         | DATE             | —                                         |
| `progress`         | DECIMAL(5,2)     | 0–100%                                    |
| `status`           | VARCHAR(50)      | Active / Completed / On Hold              |
| `objectives`       | TEXT             | —                                         |
| `progress_model`   | VARCHAR(50)      | `ProgressModel` enum *(added in Phase 2)* |
| `created_at`       | DATETIME2        | Auto via `@CreatedDate`                   |
| `last_modified_at` | DATETIME2        | Auto via `@LastModifiedDate`              |
| `version`          | INTEGER          | Optimistic locking                        |

#### `tasks`

| Column           | Type             | Notes                                      |
| ---------------- | ---------------- | ------------------------------------------ |
| `id`             | UNIQUEIDENTIFIER | PK                                         |
| `project_id`     | UNIQUEIDENTIFIER | FK → projects                              |
| `parent_task_id` | UNIQUEIDENTIFIER | FK → tasks (nullable) *(added in Phase 1)* |
| `name`           | VARCHAR(255)     | —                                          |
| `assignee`       | VARCHAR(255)     | —                                          |
| `due_date`       | DATE             | —                                          |
| `status`         | VARCHAR(50)      | Pending / In Progress / Completed          |
| `priority`       | VARCHAR(50)      | Low / Medium / High / Critical             |
| `description`    | TEXT             | —                                          |
| `actual_cost`    | DECIMAL(19,2)    | —                                          |
| `dependencies`   | VARCHAR(MAX)     | Comma-separated task IDs                   |
| `version`        | INTEGER          | Optimistic locking                         |

#### `resources`

| Column                  | Type             | Notes                        |
| ----------------------- | ---------------- | ---------------------------- |
| `id`                    | UNIQUEIDENTIFIER | PK                           |
| `project_id`            | UNIQUEIDENTIFIER | FK → projects                |
| `name`                  | VARCHAR(255)     | —                            |
| `category`              | VARCHAR(100)     | Material / Equipment / Labor |
| `quantity`              | DECIMAL(19,2)    | —                            |
| `unit`                  | VARCHAR(50)      | e.g. kg, hrs, units          |
| `allocation_percentage` | DECIMAL(5,2)     | —                            |
| `cost`                  | DECIMAL(19,2)    | —                            |

#### `task_allocations`

| Column        | Type             | Notes              |
| ------------- | ---------------- | ------------------ |
| `id`          | UNIQUEIDENTIFIER | PK                 |
| `task_id`     | UNIQUEIDENTIFIER | FK → tasks         |
| `resource_id` | UNIQUEIDENTIFIER | FK → resources     |
| `quantity`    | DECIMAL(19,2)    | Allocated quantity |

#### `documents`

| Column        | Type             | Notes                                    |
| ------------- | ---------------- | ---------------------------------------- |
| `id`          | UNIQUEIDENTIFIER | PK                                       |
| `project_id`  | UNIQUEIDENTIFIER | FK → projects                            |
| `name`        | VARCHAR(255)     | —                                        |
| `file_path`   | VARCHAR(500)     | Legacy path                              |
| `storage_key` | VARCHAR(500)     | Adapter storage key *(added in Phase 1)* |
| `uploaded_by` | VARCHAR(255)     | —                                        |

> All tables carry `created_at`, `last_modified_at`, `version` inherited from `BaseEntity`.

### 4.3 SQL Query Reference

#### Standard CRUD Pattern

```sql
-- CREATE
INSERT INTO projects (id, name, client, location, budget, actual_cost,
    start_date, end_date, progress, status, objectives, created_at, last_modified_at, version)
VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE(), 0);

-- READ by ID
SELECT * FROM projects WHERE id = ?;

-- UPDATE with Optimistic Locking
UPDATE projects
SET name=?, client=?, budget=?, actual_cost=?, progress=?,
    last_modified_at=GETDATE(), version=version+1
WHERE id=? AND version=?;

-- DELETE with Optimistic Locking
DELETE FROM projects WHERE id=? AND version=?;

-- PAGINATED READ
SELECT * FROM projects ORDER BY created_at DESC
OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
```

#### Key Custom Queries

```sql
-- High-value projects (sub-query)
SELECT p.* FROM projects p
WHERE p.budget > (SELECT AVG(p2.budget) FROM projects p2);

-- Critical tasks ordered by due date
SELECT t.* FROM tasks t
WHERE LOWER(t.priority) = 'critical'
ORDER BY t.due_date;

-- Resources in active projects (multi-join)
SELECT DISTINCT r.* FROM resources r
JOIN task_allocations ta ON r.id = ta.resource_id
JOIN tasks t ON ta.task_id = t.id
JOIN projects p ON t.project_id = p.id
WHERE p.status = 'Active';

-- Daily logs by project location (4-table join)
SELECT dl.* FROM daily_logs dl
JOIN tasks t ON dl.task_id = t.id
JOIN projects p ON t.project_id = p.id
WHERE p.location = ?;

-- Dashboard aggregate stats
SELECT COUNT(*) AS total_projects,
       SUM(CASE WHEN LOWER(status)='active' THEN 1 ELSE 0 END) AS active_projects,
       SUM(budget) AS total_budget,
       SUM(actual_cost) AS total_cost
FROM projects;
```

---

## 5. Backend Architecture

### 5.1 Package Structure

```
com.constructflow
├── BackendApplication.java           (@SpringBootApplication)
├── config/
│   ├── WebConfig.java                 (global CORS)
│   ├── AppProperties.java             (@ConfigurationProperties "app")
│   └── StorageProperties.java         (@ConfigurationProperties "storage")
├── controller/                        (9 REST controllers)
├── dto/                               (17 request/response DTOs)
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java  (→ HTTP 404)
│   ├── InsufficientResourceException.java (→ HTTP 422)
│   └── DomainValidationException.java  (→ HTTP 400)
├── model/
│   ├── BaseEntity.java
│   ├── [11 JPA entity classes]
│   └── work/                          ── Composite pattern
│       ├── WorkItem.java
│       ├── LeafTask.java
│       ├── CompositeTask.java
│       └── WorkItemVisitor.java
├── repository/                        (11 Spring Data JPA repos)
└── service/
    ├── events/                        (Spring event bus)
    ├── factory/                       ── Factory Method
    │   └── report/                    ── Abstract Factory
    ├── iteration/                     ── Iterator
    ├── mapping/                       (9 XxxMapper components)
    ├── mediator/
    │   ├── allocation/                ── Mediator
    │   └── discussion/                ── Mediator
    ├── observer/                      ── Observer + Singleton
    ├── storage/                       ── Adapter
    ├── strategy/
    │   ├── progress/                  ── Strategy
    │   └── prioritisation/            ── Strategy
    ├── template/
    │   ├── export/                    ── Template Method
    │   └── allocation/                ── Template Method
    └── [ProjectService, TaskService, ResourceService, ...]
```

### 5.2 Domain Model

All entities extend `BaseEntity`:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    private Long version;   // optimistic locking
}
```

Key domain additions from refactor phases:

| Entity     | New Field       | Purpose                        |
| ---------- | --------------- | ------------------------------ |
| `Task`     | `parentTaskId`  | Enables Composite subtask tree |
| `Document` | `storageKey`    | Decoupled storage via Adapter  |
| `Project`  | `progressModel` | Per-project Strategy selection |

### 5.3 Service Layer

Key delegation patterns after refactor:

```java
// ResourceService — entire allocation reduced to 1 line
public void allocateResource(UUID taskId, UUID resourceId, double qty) {
    allocationMediator.allocate(new AllocationCommand(taskId, resourceId, qty));
}

// ProjectService — delegates progress to Strategy, cost to Composite
public void updateProjectProgress(UUID projectId) {
    Project project = projectRepository.findById(projectId).orElseThrow();
    List<Task> tasks = taskRepository.findByProjectId(projectId);
    ProgressStrategy strategy = progressStrategyResolver.resolve(project.getProgressModel());
    project.setProgress(strategy.calculate(project, tasks));
    WorkItem root = taskTreeBuilder.buildForProject(projectId);
    project.setActualCost(root.actualCost());
    projectRepository.save(project);
}

// TaskService — critical tasks use PrioritisationStrategy
public List<TaskResponseDTO> getCriticalTasks(PrioritisationKey sortKey) {
    List<Task> raw = taskRepository.findCriticalTasks();
    return prioritisationResolver.resolve(sortKey).prioritise(raw).stream()
            .map(taskMapper::toResponse)
            .collect(Collectors.toList());
}

// AnnouncementCommentService — single mediator call
public CommentResponseDTO addComment(UUID announcementId, CommentRequestDTO dto) {
    return discussionRoomMediator.post(announcementId, dto);
}
```

### 5.4 REST API Reference

| Method   | Endpoint                                  | Description                    | Pattern             |
| -------- | ----------------------------------------- | ------------------------------ | ------------------- |
| `GET`    | `/api/projects`                           | List all (paginated)           | Iterator            |
| `POST`   | `/api/projects`                           | Create                         | Factory Method      |
| `PUT`    | `/api/projects/{id}`                      | Update                         | —                   |
| `DELETE` | `/api/projects/{id}`                      | Delete                         | —                   |
| `GET`    | `/api/tasks/critical`                     | Critical tasks (default order) | Strategy            |
| `GET`    | `/api/tasks/critical?sortBy=RISK`         | Critical tasks by risk score   | Strategy            |
| `GET`    | `/api/tasks/critical?sortBy=DUE_DATE`     | By due date                    | Strategy            |
| `GET`    | `/api/tasks/critical?sortBy=COST_DESC`    | By cost descending             | Strategy            |
| `GET`    | `/api/tasks/critical?sortBy=DEPENDENCIES` | By dependency fan-out          | Strategy            |
| `POST`   | `/api/resources/allocate`                 | Allocate resource to task      | Mediator + Template |
| `GET`    | `/api/documents/{id}/export?format=PDF`   | Export document as PDF         | Template Method     |
| `GET`    | `/api/documents/{id}/export?format=CSV`   | Export as CSV                  | Template Method     |
| `GET`    | `/api/documents/{id}/export?format=ZIP`   | Export as ZIP                  | Template Method     |
| `GET`    | `/api/reports/EXECUTIVE`                  | Executive summary report       | Abstract Factory    |
| `GET`    | `/api/reports/PROJECT`                    | Project status report          | Abstract Factory    |
| `GET`    | `/api/reports/FINANCIAL`                  | Financial report               | Abstract Factory    |
| `GET`    | `/api/analytics/dashboard`                | Dashboard statistics           | Iterator            |
| `POST`   | `/api/announcements/{id}/comments`        | Post comment                   | Mediator + Observer |

---

## 6. SOLID Compliance Audit

Before the pattern phases, a full SOLID audit identified and closed **13 violations**:

| #   | Principle  | Location                           | Violation                                                 | Resolution                                       |
| --- | ---------- | ---------------------------------- | --------------------------------------------------------- | ------------------------------------------------ |
| 1   | **SRP**    | All 13 services                    | `mapToResponseDTO` duplicated everywhere                  | Extracted to 9 `XxxMapper` `@Component`s         |
| 2   | **SRP**    | `DocumentService`                  | Mixed NIO file I/O with JPA persistence                   | Delegated to `DocumentStorage` adapter           |
| 3   | **SRP**    | `GlobalReportService`              | Queried, filtered, formatted, printed all in one          | Decomposed into iterators + factory              |
| 4   | **SRP**    | `ResourceService.allocateResource` | Validation, mutation, logging, notification in one method | Mediator + Template Method in Phase 2            |
| 5   | **OCP**    | `GlobalReportService`              | Adding a report type required editing the class           | `ReportArtifactFactory` — new kind = new class   |
| 6   | **OCP**    | `AnalyticsService`                 | New aggregations required editing                         | `PagedRepositoryIterator` makes scan injectable  |
| 7   | **OCP**    | Progress calculation               | Single hard-coded formula                                 | Strategy — 4 interchangeable models              |
| 8   | **OCP**    | Task ordering                      | Insertion-order only                                      | Strategy — 4 sort algorithms                     |
| 9   | **DIP**    | `TaskService`                      | Directly imported concrete `ProjectService`               | Publishes `TaskMutatedEvent`; listener decoupled |
| 10  | **DIP**    | `DocumentService`                  | Coupled to `java.nio.file`                                | Depends on `DocumentStorage` interface           |
| 11  | **DIP**    | `ResourceService`                  | `System.out.println` instead of injected logger           | SLF4J `LoggerFactory`                            |
| 12  | Exceptions | `GlobalExceptionHandler`           | Everything mapped to HTTP 500                             | 3 typed exceptions with correct status codes     |
| 13  | Config     | Multiple classes                   | Hard-coded status strings, CORS origins, paths            | `AppProperties` + `StorageProperties`            |

---

## 7. Design Patterns — Phase 1: Creational & Structural

### 7.1 Adapter — Document Storage

**Problem:** `DocumentService` called `java.nio.file` APIs directly. Deleting a document did not remove the physical file.

**Solution:** A `DocumentStorage` port interface with a `LocalFileSystemStorageAdapter` implementation.

```
«interface»
DocumentStorage
──────────────────────────
+ store(file) : StoredFile
+ load(key)   : InputStream
+ delete(key) : void
        ▲
        │ implements
LocalFileSystemStorageAdapter
──────────────────────────────
- basePath : Path (from StorageProperties)
+ store(file) : StoredFile
+ load(key)   : InputStream
+ delete(key) : void
```

**Impact:** Switching to S3 or Azure Blob = one new `@Component`. No service change required.

---

### 7.2 Factory Method — Entity Construction

**Problem:** Every service had ad-hoc `new Entity(); entity.setX(...)` blocks, duplicating construction rules across 13 services.

**Solution:** `EntityFactory<E, D>` interface with `create(dto)` and `apply(entity, dto)` methods.

```java
// Before — 10 setter lines per service
Task task = new Task();
task.setName(dto.getName());
// ...

// After — 1 line
Task saved = taskRepository.save(taskFactory.create(dto));
```

---

### 7.3 Abstract Factory — Report Families

**Problem:** Adding a new report kind required editing `GlobalReportService`.

**Solution:** `ReportArtifactFactory` with three `@Component` implementations:

| Factory                  | Kind        | Sections                                                             |
| ------------------------ | ----------- | -------------------------------------------------------------------- |
| `ExecutiveReportFactory` | `EXECUTIVE` | Project Overview · Financial Summary · Task Health · Recent Activity |
| `ProjectReportFactory`   | `PROJECT`   | Project Counts · Task Progress · Overdue Alerts                      |
| `FinancialReportFactory` | `FINANCIAL` | Budget vs Actual · Cost Efficiency                                   |

---

### 7.4 Iterator — Lazy Repository Traversal

**Problem:** `findAll()` loaded every row into memory before filtering — O(n) heap usage.

**Solution:** `PagedRepositoryIterator<T>` fetches one DB page at a time.

```java
// Before — dangerous on large tables
List<Project> all = projectRepository.findAll();

// After — constant memory regardless of table size
for (Project p : projectScanner) {
    // one DB page (100 rows) at a time
}
```

---

### 7.5 Composite — Task Hierarchy

**Problem:** Tasks were flat. Progress could not aggregate recursively across subtasks.

**Solution:** `WorkItem` Composite tree:

```
WorkItem (interface)         progress() · actualCost()
├── LeafTask                 wraps a single Task entity
└── CompositeTask            aggregates List<WorkItem> children
                             progress() = avg(children)
                             actualCost() = sum(children)
```

`parentTaskId` nullable column added to `tasks`. `TaskTreeBuilder` reconstructs the tree from flat rows.

---

## 8. Design Patterns — Phase 2: Behavioural

### 8.1 Template Method — Document Export

**Problem:** PDF, CSV, and ZIP export share the same 5-step pipeline but differ in the transform step.

**Design:**

```
AbstractDocumentExporter
─────────────────────────────────────────────────────
+ export(request) : ExportResult  ← FINAL template method
  1. validateAccess(request)         ← hook (default: no-op)
  2. resolveDocument(request)        ← concrete
  3. loadContent(document)           ← concrete
  4. transform(document, raw)  ★     ← ABSTRACT
  5. logExport(request, filename)    ← hook (default: SLF4J)

+ contentType()    : String  ★  ABSTRACT
+ fileExtension()  : String  ★  ABSTRACT
         ▲
    ┌────┴──────────────┬──────────────────┐
    │                   │                  │
PdfDocumentExporter CsvDocumentExporter ZipArchiveExporter
```

**Endpoint:** `GET /api/documents/{id}/export?format=PDF|CSV|ZIP`

---

### 8.2 Template Method — Allocation Validation

**Problem:** Material, Equipment, and Labor allocations share a validation skeleton but each has a different category-specific rule.

**Design:**

```
AbstractAllocationValidator
─────────────────────────────────────────────────────
+ validate(request) : Resource  ← FINAL template method
  1. checkBasicInputs(request)
  2. resolveResource(request)
  3. checkAvailability(resource, request)
  4. checkCategoryRules(resource, request)  ← ABSTRACT
  5. checkProjectBudget(resource, request)  ← hook

         ▲
    ┌────┴─────────────────┬──────────────────────────┐
    │                      │                          │
MaterialAllocation   EquipmentAllocation      LaborAllocation
Validator            Validator                Validator
─────────────────    ───────────────────      ──────────────────
Min reorder buffer   Whole units only         Max 12 hrs/booking
                     Cannot exceed 100%
```

---

### 8.3 Mediator — Resource Allocation Workflow

**Problem:** Allocating a resource touched 4 concerns directly in `ResourceService`, hiding the business rule.

**Design:**

```
«interface» AllocationMediator
+ allocate(command)
         ▲
DefaultAllocationMediator  @Transactional
──────────────────────────────────────────────────
+ allocate(command):
  1 → ResourceColleague.reserve(cmd)
        ↳ Template Method validation + stock deduction
  2 → TaskAllocationColleague.record(cmd)
        ↳ persist TaskAllocation row
  3 → NotificationColleague.broadcast(cmd)
        ↳ ActivityHub.INSTANCE.publish(ResourceAllocated)
  4 → AuditColleague.recordAllocation(cmd, name)
        ↳ SLF4J audit line

ResourceService.allocateResource(...) ──▶ allocationMediator.allocate(cmd)
                                          (1 line)
```

---

### 8.4 Mediator — Discussion Room

**Problem:** Posting a comment must trigger multiple side effects that need to be independently addable/removable.

**Design:**

```
AnnouncementRoom
─────────────────────────────────────────────────
+ post(announcementId, dto):
    persist comment → safeDispatch to all participants
              │
    ┌─────────┼──────────────────┐
    │         │                  │
AuthorParticipant  Dashboard    ActivityRelay
                   Participant  Participant
────────────────   ───────────  ──────────────────
Notifies author    Increments   Publishes
(log line)         comment      CommentPosted
                   counter      to ActivityHub
```

---

### 8.5 Strategy — Project Progress Calculation

**Problem:** A single `completed / total` formula was hard-coded in `ProjectService`.

**Design:**

```
«interface» ProgressStrategy
+ calculate(project, tasks) : double
+ model() : ProgressModel

«enum» ProgressModel
  TASK_COUNT | WEIGHTED_BY_COST | MILESTONE_BASED | EFFORT_BASED

Concrete Strategies:
  TaskCountProgressStrategy      completed / total tasks (default)
  WeightedByCostProgressStrategy share of actualCost on completed tasks
  MilestoneBasedProgressStrategy milestones hit by name-match
  EffortBasedProgressStrategy    logged hours on completed tasks

Project.progressModel ──▶ ProgressStrategyResolver ──▶ correct bean
```

**Changing a project's progress model = data change, not a code change.**

---

### 8.6 Strategy — Critical Task Prioritisation

**Problem:** `GET /api/tasks/critical` returned tasks in insertion order only.

**Design:**

```
«interface» PrioritisationStrategy
+ prioritise(tasks) : List<Task>
+ key() : PrioritisationKey

«enum» PrioritisationKey
  DUE_DATE | COST_DESC | RISK | DEPENDENCIES

Concrete Strategies:
  DueDatePrioritisationStrategy       Earliest due date first (default)
  CostDescPrioritisationStrategy      Highest actualCost first
  DependencyCountPrioritisationStrategy Most dependents first
  RiskWeightedPrioritisationStrategy  score = mult × (urgency×10 + log10(cost) + deps×2)
```

**Endpoint:** `GET /api/tasks/critical?sortBy=RISK`

---

### 8.7 Observer + Singleton — Global Activity Hub

**Problem:** Audit, dashboard counters, and overdue alerting all need the same events — wiring each directly creates a ~15-edge mesh.

**Design:**

```
«enum singleton»
ActivityHub { INSTANCE }                ← Effective Java Item 3
──────────────────────────────────────────────────────────────
- observers : CopyOnWriteArrayList<ActivityObserver>

+ subscribe(observer)
+ unsubscribe(observer)
+ publish(activity)  ← exception-isolated: bad observer can't poison others

Why enum?
  ✓ JVM guarantees 1 instance per classloader
  ✓ Thread-safe init without synchronization
  ✓ Immune to reflection attacks
  ✓ Serialisation returns the same instance

«sealed interface» Activity records:
  TaskCreated · TaskCompleted · TaskOverdue · ResourceAllocated · CommentPosted

Observers (self-register @PostConstruct):
  AuditLogObserver       → append-only audit trail
  DashboardCounterObserver → in-memory tallies by event type
  OverdueAlertObserver   → WARN-level alerts for TaskOverdue

Publishers:
  TaskService            → TaskCreated, TaskCompleted
  NotificationColleague  → ResourceAllocated
  ActivityRelayParticipant → CommentPosted
```

---

## 9. UML Diagrams

### 9.1 Sequence Diagram — Resource Allocation Flow           ![](C:\Users\dr_sh\OneDrive\Desktop\swe\Constructflow-Project-Management\docs\diagrams\Sequence%20Diagram.png)

### 9.2 Activity Diagram — Allocation Workflow

```
                              ● START
                              │
                              ▼
                 ┌────────────────────────┐
                 │  User clicks           │
                 │  "Allocate Resource"   │
                 └─────────┬──────────────┘
                           ▼
                 ┌─────────────────────────┐
                 │  System receives        │
                 │  AllocationCommand      │
                 └─────────┬───────────────┘
                           ▼
                 ┌─────────────────────────┐
                 │  Validate basic inputs  │
                 │  (Task ID, Resource ID) │
                 └──────┬──────────┬───────┘
                      YES          NO
                       │           │
                       │    ┌──────▼────────────────────┐
                       │    │ Throw ValidationException  │──▶ ◉ END
                       │    └───────────────────────────┘
                       ▼
                 ┌─────────────────────────┐
                 │  Check inventory levels │
                 └──────┬──────────┬───────┘
                      YES          NO
                       │           │
                       │    ┌──────▼──────────────────────────┐
                       │    │ Throw InsufficientResource       │──▶ ◉ END
                       │    │ Exception                        │
                       │    └──────────────────────────────────┘
                       ▼
                 ┌────────────────────────────┐
                 │ Apply category rules        │
                 │ Material / Equipment / Labor│
                 └──────┬──────────┬───────────┘
                      YES          NO
                       │           │
                       │    ┌──────▼──────────────────────┐
                       │    │ Throw DomainValidation       │──▶ ◉ END
                       │    │ Exception                    │
                       │    └──────────────────────────────┘
                       ▼
                 ┌───────────────────────────────┐
                 │  Deduct quantity from Resource │
                 └─────────┬─────────────────────┘
                 ┌─────────▼──────────────────────┐
                 │ Save TaskAllocation record to DB│
                 └─────────┬──────────────────────┘
                 ┌─────────▼────────────────────────┐
                 │ Publish Activity to Global Hub    │
                 └─────────┬────────────────────────┘
                 ┌─────────▼──────────────────┐
                 │  Write to Audit Log         │
                 └─────────┬──────────────────┘
                 ┌─────────▼──────────────┐
                 │  Return 200 OK          │
                 └─────────┬──────────────┘
                           ◉ END
```

### 9.3 Class Diagrams — All Patterns

#### Template Method — Document Export

```
┌──────────────────────────────────────────────────────────────┐
│              AbstractDocumentExporter                        │
│──────────────────────────────────────────────────────────────│
│ + export(request : ExportRequest) : ExportResult  «final»    │
│ # validateAccess(request)                         «hook»     │
│ # resolveDocument(request) : Document             «concrete» │
│ # loadContent(document) : byte[]                  «concrete» │
│ # transform(document, raw) : byte[]               «abstract» │
│ # contentType() : String                          «abstract» │
│ # fileExtension() : String                        «abstract» │
│ # buildFilename(document) : String                «hook»     │
│ # logExport(request, filename)                    «hook»     │
└──────────────────┬───────────────────────────────────────────┘
                   │
      ┌────────────┼────────────┐
      │            │            │
┌─────▼──────┐ ┌───▼──────┐ ┌──▼───────────┐
│PdfDocument │ │CsvDocument│ │ZipArchive    │
│Exporter    │ │Exporter   │ │Exporter      │
├────────────┤ ├───────────┤ ├──────────────┤
│transform() │ │transform()│ │transform()   │
│contentType │ │contentType│ │contentType() │
│fileExt()   │ │fileExt()  │ │fileExt()     │
└────────────┘ └───────────┘ └──────────────┘

DocumentExporterResolver ──▶ AbstractDocumentExporter
DocumentController ───────▶ DocumentExporterResolver
```

#### Mediator — Resource Allocation

```
«interface»
AllocationMediator                    AllocationCommand «record»
──────────────────                    ─────────────────────────
+ allocate(command)                   taskId : UUID
        ▲                             resourceId : UUID
        │ implements                  quantity : double
DefaultAllocationMediator  @Transactional
──────────────────────────────────────────────────────────────
+ allocate(command):
  ├──▶ ResourceColleague        (validates + deducts stock)
  ├──▶ TaskAllocationColleague  (persists allocation row)
  ├──▶ NotificationColleague    (publishes to ActivityHub)
  └──▶ AuditColleague           (writes SLF4J audit line)

ResourceService ──▶ AllocationMediator (interface, not concrete)
```

#### Strategy — Progress Calculation

```
«interface» ProgressStrategy     «enum» ProgressModel
──────────────────────────       ─────────────────────
+ calculate(project, tasks)      TASK_COUNT
+ model()                        WEIGHTED_BY_COST
        ▲                        MILESTONE_BASED
        │                        EFFORT_BASED
   ┌────┴─────────────────────────────────┐
   │          │           │               │
TaskCount  Weighted    Milestone       Effort
Progress   ByCost      Based           Based
Strategy   Progress    Progress        Progress
           Strategy    Strategy        Strategy
                       ↳ name match    ↳ reads WorkLog

ProgressStrategyResolver ──▶ ProgressStrategy
ProjectService ───────────▶ ProgressStrategyResolver
```

#### Observer + Singleton — Activity Hub

```
«enum singleton»
ActivityHub { INSTANCE }
──────────────────────────────────────────────────────────
- observers : CopyOnWriteArrayList<ActivityObserver>
+ subscribe(observer)
+ unsubscribe(observer)
+ publish(activity)  ← exception-isolated dispatch
        │ notifies
        ▼
«interface» ActivityObserver
+ onActivity(activity)
        ▲
   ┌────┴─────────────────┐
   │            │          │
AuditLog   Dashboard  Overdue
Observer   Counter    Alert
           Observer   Observer

«sealed interface» Activity
  ├── TaskCreated «record»
  ├── TaskCompleted «record»
  ├── TaskOverdue «record»
  ├── ResourceAllocated «record»
  └── CommentPosted «record»
```

#### Iterator — Lazy Repository Traversal

```
«interface» Iterator<T>          «interface» Iterable<T>
+ hasNext() : boolean            + iterator() : Iterator<T>
+ next() : T                              ▲
        ▲                                 │ implements
        │                        ProjectScanner
PagedRepositoryIterator          ──────────────────────────
──────────────────────────       - projectRepository
- pageFetcher : Function         + iterator()
- pageSize : int (default 100)     └─▶ PagedRepositoryIterator
- currentPage, inner, index
+ hasNext(), next()
- advance()  ← fetches next page when inner exhausted

ProjectTaskTreeIterator          Clients:
──────────────────────────       GlobalReportService ──▶ ProjectScanner
- projects : Iterator            AnalyticsService    ──▶ PagedRepositoryIterator
- taskRepo                       ReportService       ──▶ ProjectTaskTreeIterator
- currentTasks : Iterator<Task>
+ streams all tasks across all projects in O(1) memory
```

---

## 10. Application Features & UI Showcase

### 10.1 Feature Overview

| Section           | Core Functionality                                                                       |
| ----------------- | ---------------------------------------------------------------------------------------- |
| **Dashboard**     | KPI overview · Critical task alerts · Real-time announcements · Activity feed            |
| **Projects**      | Full CRUD · Budget vs cost tracking · Progress monitoring · Status management            |
| **Tasks**         | Priority-based management · Critical task view · Resource allocation · Subtask support   |
| **Resources**     | Material / Equipment / Labor tracking · Daily inventory updates · Allocation management  |
| **Documents**     | Upload / organize · Folder structure · Multi-format export (PDF / CSV / ZIP)             |
| **Reports**       | Daily reports with auto change tracking · Executive / Project / Financial global reports |
| **Announcements** | Priority-tagged announcements · Discussion threads · Comment management                  |

---

### 10.2 Dashboard

> ### 📸 Screenshot — Dashboard Overview
> 
> ![](C:\Users\dr_sh\OneDrive\Pictures\Screenshots\Screenshot%202026-05-16%20234553.png)

**What you see on the Dashboard:**

- **KPI Cards** — Total Budget vs Actual Cost, Active Project count, Pending Task count, overall budget utilization %
- **Critical Tasks Panel** — All tasks with `priority = "Critical"`, sortable via `sortBy` query parameter
- **Announcements Feed** — Latest system-wide announcements with priority badges (Critical · High · Normal)
- **Activity Log** — Recent changes tracked by the `ActivityHub` Observer (audit trail surface)

---

### 10.3 Project Management

> ### 📸 Screenshot — Projects List View
> 
> ![](C:\Users\dr_sh\AppData\Roaming\marktext\images\2026-05-16-23-13-39-Screenshot%202026-05-16%20234558.png)

> ### 📸 Screenshot — Create / Edit Project Modal![](C:\Users\dr_sh\OneDrive\Pictures\Screenshots\Screenshot%202026-05-16%20234704.png)

**Key interactions:**

- Creating a project triggers `ProjectFactory.create(dto)` on the backend (Factory Method)
- The **Progress Model** dropdown sets `Project.progressModel`, which determines which `ProgressStrategy` is used at calculation time
- Progress bars auto-update when tasks are marked complete
- Status badges: 🟢 Active · 🔵 On Hold · ✅ Completed

---

### 10.4 Task Management

> ### 📸 Screenshot — Tasks Board / Table
> 
> ![](C:\Users\dr_sh\OneDrive\Pictures\Screenshots\Screenshot%202026-05-16%20234602.png)

**Priority colour system:**

| Priority    | Indicator    | Behaviour                                                 |
| ----------- | ------------ | --------------------------------------------------------- |
| 🔴 Critical | Red badge    | Appears on Dashboard · Sortable via 4 Strategy algorithms |
| 🟠 High     | Orange badge | Standard priority                                         |
| 🟡 Medium   | Yellow badge | Standard priority                                         |
| ⚪ Low       | Grey badge   | Standard priority                                         |

**Risk score formula (RiskWeightedPrioritisationStrategy):**

```
score = priorityMultiplier × (urgencyScore × 10 + log₁₀(max(cost, 1)) + dependencyCount × 2)

urgency Score:
  10 → overdue
  8  → due today
  n  → linear fade over 2 weeks otherwise
```

---

### 10.5 Resource Management

> ### 📸 Screenshot — Resources Table
> 
> ![](C:\Users\dr_sh\OneDrive\Pictures\Screenshots\Screenshot%202026-05-16%20234606.png)

**Allocation flow (Mediator + Template Method):**

1. User selects resource and target task, enters quantity
2. `ResourceColleague` runs the appropriate `AllocationValidator` (Material buffer check / Equipment whole-units / Labor 12-hr cap)
3. On success: stock deducted · row persisted · `ResourceAllocated` event published to `ActivityHub` · audit logged
4. On failure: typed exception surfaces a descriptive, status-coded error to the UI

---

### 10.6 Document Management

Export is powered by the **Template Method** pattern. Each format is a `@Component` that overrides only `transform()`, `contentType()`, and `fileExtension()` — the validate/load/log skeleton is shared and immutable.

---

### 10.7 Reports

> ### 📸 Screenshot — Daily Report Form
> 
> ![](C:\Users\dr_sh\OneDrive\Pictures\Screenshots\Screenshot%202026-05-16%20234618.png)

> ### 📸 Screenshot — Global Executive Report
> 
> ![](C:\Users\dr_sh\OneDrive\Pictures\Screenshots\Screenshot%202026-05-17%20001747.png)

**Report types (Abstract Factory):**

| Report            | Endpoint                     | Contents                                                 |
| ----------------- | ---------------------------- | -------------------------------------------------------- |
| Executive Summary | `GET /api/reports/EXECUTIVE` | Budget · Active Projects · Task Health · Recent Activity |
| Project Status    | `GET /api/reports/PROJECT`   | Project Counts · Task Progress · Overdue Alerts          |
| Financial         | `GET /api/reports/FINANCIAL` | Budget vs Actual · Cost Efficiency %                     |

---

### 10.8 Announcements & Discussion Threads

> ### 📸 Screenshot — Announcement Board with Discussion Thread![](C:\Users\dr_sh\OneDrive\Pictures\Screenshots\Screenshot%202026-05-16%20234622.png)

**Discussion flow (Discussion Room Mediator):**

1. User posts a comment → `AnnouncementCommentService.addComment()` called
2. `AnnouncementRoom.post()` persists the `AnnouncementComment` row
3. `AuthorParticipant.onCommentPosted()` — notifies the announcement author (log line / future email)
4. `DashboardParticipant.onCommentPosted()` — increments per-announcement comment counter
5. `ActivityRelayParticipant.onCommentPosted()` — publishes `CommentPosted` to `ActivityHub`
6. `ActivityHub` fans out to `AuditLogObserver`, `DashboardCounterObserver`, `OverdueAlertObserver`

---

## 11. Setup & Installation Guide

### Prerequisites

| Tool          | Minimum Version | Verify               |
| ------------- | --------------- | -------------------- |
| Node.js       | 18.0.0          | `node --version`     |
| JDK           | 21              | `java --version`     |
| Maven         | 3.8+            | `mvn --version`      |
| MS SQL Server | 2019+           | SSMS connection test |

### Step 1 — Database Setup

```sql
-- Create the database
CREATE DATABASE ConstructFlowDB;

-- Option A: SQL Server Authentication
ALTER LOGIN sa WITH PASSWORD = 'YourStrongPassword123!';
ALTER LOGIN sa ENABLE;
```

### Step 2 — Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# SQL Server Authentication
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ConstructFlowDB;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=YourStrongPassword123!
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# App configuration
app.cors.allowed-origins=http://localhost:3000,http://localhost:3001
storage.local-path=uploads/
```

### Step 3 — Run the Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
# Starts on http://localhost:8080
# Hibernate auto-creates all 11 tables on first run
```

### Step 4 — Run the Frontend

```bash
cd construct-flow-nextjs-frontend
npm install
npm run dev
# Starts on http://localhost:3000
```

### Step 5 — Optional Sample Data

Execute in SSMS in this order:

1. `database_schema.sql`
2. `sample_data.sql` — 15 projects with tasks and resources
3. `add_critical_tasks.sql` — critical priority tasks for testing
4. `announcement_comments_schema.sql`

### Troubleshooting

| Symptom              | Likely Cause                      | Fix                                        |
| -------------------- | --------------------------------- | ------------------------------------------ |
| Backend won't start  | SQL Server not running            | Services → SQL Server → Start              |
| `Table not found`    | First run, tables not created yet | Let Hibernate `ddl-auto=update` run        |
| `403 Forbidden` CORS | Origin not allowed                | Add origin to `app.cors.allowed-origins`   |
| `422` on allocation  | Insufficient stock                | Check resource quantity in Resources view  |
| `400` on allocation  | Category rule violation           | Equipment: whole units; Labor: ≤12 hrs     |
| Frontend blank       | Backend not running               | Check `http://localhost:8080/api/projects` |

---

## 12. Testing

### 12.1 ActivityHubTest

`backend/src/test/java/com/constructflow/service/observer/ActivityHubTest.java`

JUnit 5, **9 test cases** covering both Singleton invariants and Observer dispatch contract:

| #   | Test Name                                              | What It Proves                                         |
| --- | ------------------------------------------------------ | ------------------------------------------------------ |
| 1   | `sameInstanceAcrossLookups`                            | `INSTANCE` is reference-equal on every lookup          |
| 2   | `enumValuesContainsExactlyOneInstance`                 | `values().length == 1`                                 |
| 3   | `reflectionCannotInstantiateASecondHub`                | JDK blocks `Constructor.newInstance` on enum types     |
| 4   | `serialisationRoundTripReturnsSameInstance`            | Deserialising returns the same object                  |
| 5   | `subscribedObserversReceivePublishedActivities`        | Basic dispatch works end-to-end                        |
| 6   | `allRegisteredObserversReceiveTheSameEvent`            | Fan-out delivers the same object reference             |
| 7   | `unsubscribedObserverStopsReceivingEvents`             | `unsubscribe()` actually removes the observer          |
| 8   | `misbehavingObserverDoesNotPreventOthersFromReceiving` | Exception isolation — bad observer can't poison others |

```bash
# Just the singleton + observer tests
cd backend
mvn test -Dtest=ActivityHubTest

# Full test suite
mvn test
```

### 12.2 Smoke Tests for New Endpoints

```bash
# Export a document as CSV
curl -OJ "http://localhost:8080/api/documents/{documentId}/export?format=CSV"

# Export as PDF
curl -OJ "http://localhost:8080/api/documents/{documentId}/export?format=PDF"

# Critical tasks ordered by composite risk score
curl "http://localhost:8080/api/tasks/critical?sortBy=RISK"

# Critical tasks by due date (default)
curl "http://localhost:8080/api/tasks/critical?sortBy=DUE_DATE"

# Executive report
curl "http://localhost:8080/api/reports/EXECUTIVE"

# Financial report
curl "http://localhost:8080/api/reports/FINANCIAL"
```

### 12.3 Recommended Future Test Coverage

| Area                          | Test Type                                                          |
| ----------------------------- | ------------------------------------------------------------------ |
| Each `ProgressStrategy`       | Unit tests with synthetic task fixtures                            |
| Each `PrioritisationStrategy` | Unit tests verifying sort order                                    |
| `DefaultAllocationMediator`   | Integration test — atomic rollback on colleague failure            |
| `AnnouncementRoom`            | Unit test — all participants dispatched; bad participant isolated  |
| Document export pipeline      | Integration test — correct `Content-Type` + byte output per format |
| `AllocationValidatorRegistry` | Unit tests for each category rule and the fallback                 |

---

## 13. Pattern Integration Map

The 12 patterns compose into a system with well-defined seams:

```
                    USER REQUEST
                         │
                         ▼
              ┌──────────────────────┐
              │   REST Controller    │
              └──────────┬───────────┘
                         │
            ┌────────────▼────────────────────────────────────────┐
            │                  Service Layer                       │
            │                                                     │
            │  ProjectService                                     │
            │    ├──▶ ProgressStrategyResolver [Strategy §8.5]   │
            │    │       └──▶ Strategy.calculate()               │
            │    └──▶ TaskTreeBuilder [Composite §7.5]           │
            │                                                     │
            │  TaskService                                        │
            │    ├──▶ PrioritisationStrategyResolver [§8.6]      │
            │    └──▶ ActivityHub.publish() [Observer §8.7]      │
            │                                                     │
            │  ResourceService                                    │
            │    └──▶ AllocationMediator [Mediator §8.3] ────────┼──┐
            │                                                     │  │
            │  DocumentService                                    │  │
            │    └──▶ DocumentStorage [Adapter §7.1]            │  │
            │    └──▶ AbstractDocumentExporter [Template §8.1]  │  │
            │                                                     │  │
            │  AnnouncementCommentService                         │  │
            │    └──▶ AnnouncementRoom [Mediator §8.4] ──────────┼──┼──▶ ActivityHub [§8.7]
            └─────────────────────────────────────────────────────┘  │
                                                                     │
            Mediator internals [§8.3]:                               │
              ResourceColleague                                      │
                └──▶ AllocationValidatorRegistry [Template §8.2]    │
              NotificationColleague ───────────────────────────────▶ │
              TaskAllocationColleague                                │
              AuditColleague                                         │
                                                                     │
            ActivityHub [§8.7] ◀─────────────────────────────────────┘
              ├── AuditLogObserver
              ├── DashboardCounterObserver
              └── OverdueAlertObserver

            All report services:
              └──▶ ProjectScanner [Iterator §7.4]
                   └──▶ ReportArtifactFactory [Abstract Factory §7.3]
```

**Critical integration paths:**

- The **Allocation Mediator (§8.3)** invokes the **Allocation Template Method (§8.2)** via `ResourceColleague`, then broadcasts through the **Observer+Singleton (§8.7)** via `NotificationColleague` — three patterns cooperating on one user action
- The **Discussion Room Mediator (§8.4)** relays every comment to the **Observer+Singleton (§8.7)** via `ActivityRelayParticipant` — adding a new comment subscriber requires zero changes to the mediator
- The **Progress Strategy (§8.5)** composes with the **Composite (§7.5)** — strategy handles the percentage calculation, composite handles cost rollup, `ProjectService` orchestrates both with two delegation calls
- The **Iterator (§7.4)** feeds data to the **Abstract Factory (§7.3)** report builders at constant memory, regardless of database size

---

## 14. Assumptions & Design Decisions

| #   | Area                  | Assumption / Decision                                                                                                                       |
| --- | --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | Document export       | All exporters operate on full in-memory `byte[]` — adequate for typical construction document sizes                                         |
| 2   | PDF exporter          | Stub implementation prepends a minimal PDF header; iText / PDFBox would be swapped in for production without changing the pattern structure |
| 3   | Allocation validator  | Keyed by `Resource.category` string; unknown categories fall through to `MaterialAllocationValidator` (strictest)                           |
| 4   | Allocation mediator   | Synchronous + `@Transactional`; any colleague failure rolls back all DB writes                                                              |
| 5   | Discussion room       | Comment persisted before dispatching to participants — all participants see committed state                                                 |
| 6   | Discussion room       | Single `AnnouncementRoom` bean keyed by announcement ID; not one bean per announcement                                                      |
| 7   | Progress strategy     | Null `progressModel` defaults to `TASK_COUNT` — backward compatible with all pre-existing data                                              |
| 8   | Milestone strategy    | Milestone hit = any completed task whose name contains the milestone text (case-insensitive)                                                |
| 9   | Effort strategy       | Zero `WorkLog` entries → 0% progress (not divide-by-zero)                                                                                   |
| 10  | Prioritisation        | Null `dueDate` sorts last; null `actualCost` treated as zero                                                                                |
| 11  | Risk score            | Heuristic blend — not calibrated to a specific construction-industry standard                                                               |
| 12  | ActivityHub dispatch  | Synchronous — observers needing long-running work must hand off to their own executor                                                       |
| 13  | ActivityHub observers | Self-register in `@PostConstruct`; no inter-observer dependency, no bean ordering concerns                                                  |
| 14  | Iterator page size    | 100 rows default; high-bandwidth row types (e.g. binary documents) should use smaller pages                                                 |
| 15  | Thread safety         | Iterator instances are not thread-safe — scoped to a single request                                                                         |
| 16  | No auth layer         | Spring Security / JWT is out of scope for this phase; CORS is configured but no authentication enforced                                     |

---

## 15. References

1. Gamma, Helm, Johnson, Vlissides. *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994.
2. Joshua Bloch. *Effective Java, 3rd Edition*. Addison-Wesley, 2018 — Item 3: Enforce the singleton property with an enum type.
3. Robert C. Martin. *Clean Architecture*. Prentice Hall, 2017.
4. Spring Framework Reference Documentation — `ApplicationEventPublisher`, `@TransactionalEventListener`, `@ConfigurationProperties`.
5. Microsoft SQL Server 2019 Documentation — JDBC driver, optimistic locking, ACID compliance.
6. Martin Fowler. *Patterns of Enterprise Application Architecture*. Addison-Wesley, 2002 — Repository, Data Mapper.

### Internal Documents

| Document                         | Contents                                                                                                        |
| -------------------------------- | --------------------------------------------------------------------------------------------------------------- |
| `BEHAVIOURAL_PATTERNS_REPORT.md` | Deep-dive on all 8 behavioural pattern cases with participants tables, sequence notes, and per-case assumptions |
| `ARCHITECTURE_AND_PATTERNS.md`   | Complete map of all 12 patterns across both phases, SOLID audit table, final package structure                  |
| `PROJECT_SETUP.md`               | Database schema, SQL query reference, API endpoint list, installation steps                                     |
| `docs/uml/README.md`             | Per-diagram explainer for all 8 PlantUML class diagrams                                                         |
| `ActivityHubTest.java`           | 9-case JUnit 5 test suite proving singleton invariants and observer contract                                    |

---

*End of ConstructFlow Master Technical Documentation — v2.0.0 — April 2026*

---

> **CTRL ALT ELITE** · Spring Boot 3.2.1 · Java 21 · Next.js 14 · MS SQL Server 2019+
