# UML Class Diagrams (PlantUML)

Each `.puml` file is a self-contained PlantUML source for one design-pattern case in the ConstructFlow Java backend.

## Index

| File | Pattern | Case |
|---|---|---|
| [01_template_method_document_export.puml](01_template_method_document_export.puml) | Template Method | Document export pipeline (PDF / CSV / ZIP) |
| [02_template_method_allocation_validator.puml](02_template_method_allocation_validator.puml) | Template Method | Resource-allocation validation (Material / Equipment / Labor) |
| [03_mediator_allocation.puml](03_mediator_allocation.puml) | Mediator | Resource-allocation workflow orchestration |
| [04_mediator_discussion_room.puml](04_mediator_discussion_room.puml) | Mediator | Announcement discussion room |
| [05_strategy_progress.puml](05_strategy_progress.puml) | Strategy | Project-progress calculation |
| [06_strategy_prioritisation.puml](06_strategy_prioritisation.puml) | Strategy | Critical-task prioritisation |
| [07_observer_singleton_activity_hub.puml](07_observer_singleton_activity_hub.puml) | Observer + Singleton | Global activity stream (ActivityHub) |
| [08_iterator_paged_repository.puml](08_iterator_paged_repository.puml) | Iterator | Lazy traversal of projects and tasks |

## Rendering

Pick whichever path is convenient — each produces a PNG/SVG alongside the source.

### Option A — online (zero install)

1. Open [plantuml.com/plantuml](https://www.plantuml.com/plantuml)
2. Paste the contents of any `.puml` file
3. Download the PNG/SVG

### Option B — local CLI

```bash
# macOS / Linux
brew install plantuml            # or: apt-get install plantuml
plantuml -tpng docs/uml/*.puml   # produces .png next to each .puml

# Windows (with Java installed)
# download plantuml.jar from https://plantuml.com/download, then:
java -jar plantuml.jar -tpng docs/uml/*.puml
```

### Option C — VS Code

Install the **PlantUML** extension (jebbs.plantuml). Open any `.puml` file and press `Alt+D` to preview.
