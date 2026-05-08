# Architecture

## TODO App — v2.0

---

## Overview

Single-page, in-browser TODO application with a Spring Boot REST backend and PostgreSQL database. The frontend is plain HTML/CSS/JS (no build tools, no frameworks). State is persisted via the backend API and PostgreSQL.

---

## System Overview

```
Browser (HTML/CSS/JS)
        │
        │  HTTP (REST)
        ▼
Spring Boot Backend  (/api/todos)
        │
        │  JPA
        ▼
PostgreSQL Database
```

---

## File Structure

```
/
├── index.html          # Page structure and DOM skeleton
├── css/
│   └── style.css       # All visual styling
├── src/
│   ├── app.js          # Event wiring, rendering, and DOM references
│   └── api.js          # REST API client (fetch wrappers for all backend calls)
└── backend/            # Spring Boot application (Java 21, Maven)
    └── src/main/java/com/example/todoapp/
        ├── domain/
        │   ├── model/Todo.java
        │   └── port/
        │       ├── in/TodoUseCase.java
        │       └── out/TodoRepository.java
        ├── application/
        │   └── TodoUseCaseImpl.java
        └── adapter/
            ├── in/http/
            │   ├── TodoController.java
            │   ├── CreateTodoRequest.java
            │   ├── TodoResponse.java
            │   └── TestResetController.java
            └── out/persistence/
                ├── TodoJpaEntity.java
                ├── TodoJpaRepository.java
                └── TodoPersistenceAdapter.java
```

---

## index.html

Responsibilities:
- Defines the page skeleton (head, body, meta)
- Links `css/style.css` and `src/app.js`
- Contains the static layout: header, input row, todo list container, empty-state message

Key elements:
- `<input id="todo-input">` — task entry field
- `<button id="add-btn">` — triggers add action
- `<ul id="todo-list">` — dynamic list; `app.js` renders items here
- `<p id="empty-state">` — shown when list is empty

---

## style.css

Responsibilities:
- Layout (centered single column, responsive width)
- Input row and button appearance
- Todo item layout (checkbox left, title center, delete button right)
- Completed-item style (strikethrough text, muted color)
- Empty-state visibility toggle via `.hidden` utility class

No external fonts or icon libraries. Delete button uses a plain `✕` character.

---

## api.js

Responsibilities:
- Encapsulates all HTTP calls to the backend
- Keeps fetch details out of `app.js`

| Export | Description |
|---|---|
| `fetchTodos()` | `GET /api/todos` — returns array of todo objects |
| `createTodo(title, dueDate)` | `POST /api/todos` — creates and returns the new todo |
| `toggleTodo(id)` | `PATCH /api/todos/{id}` — flips completed state, returns updated todo |
| `deleteTodo(id)` | `DELETE /api/todos/{id}` |

---

## app.js

Responsibilities:
- On startup calls `fetchTodos()` and renders the full list
- Handles all user events (add, toggle, delete) via `api.js`, then re-renders

### Functions

| Function | Description |
|---|---|
| `render(todos)` | Clears and rebuilds `#todo-list` from a todos array; toggles empty-state |
| `refresh()` | Calls `fetchTodos()` and passes result to `render()` |
| `handleAdd()` | Reads input, calls `createTodo()`, then `refresh()` |

### Event wiring

- `#add-btn` click → `handleAdd`
- `#todo-input` keydown `Enter` → `handleAdd`
- Delegated `change` on `#todo-list` checkbox → `toggleTodo`, then `refresh`
- Delegated `click` on `.delete-btn` → `deleteTodo`, then `refresh`

Input is trimmed before use; empty/whitespace submissions are ignored.

---

## Backend — Hexagonal Architecture

The backend follows the **Ports & Adapters (Hexagonal) pattern**: business logic in the domain is fully isolated from infrastructure concerns.

### Layers

```
┌──────────────────────────────────────────────────────────────────┐
│  Adapter (in)          Application           Adapter (out)        │
│  TodoController  →→  TodoUseCaseImpl  →→  TodoPersistenceAdapter │
│  (HTTP/REST)          uses ports             (JPA/PostgreSQL)     │
└──────────────────────────────────────────────────────────────────┘
                          ↕ domain ports
                     ┌─────────────────────┐
                     │       Domain        │
                     │  Todo               │
                     │  TodoUseCase (in)   │
                     │  TodoRepository (out)│
                     └─────────────────────┘
```

### Domain (`domain/`)

| Class | Description |
|---|---|
| `Todo` | Domain model: `id` (UUID), `title` (String), `completed` (boolean), `dueDate` (String, nullable) |
| `TodoUseCase` | Inbound port — `getAll()`, `create(title, dueDate)`, `toggle(id)`, `delete(id)` |
| `TodoRepository` | Outbound port — `save()`, `findAll()`, `findById(id)`, `deleteById(id)`, `deleteAll()` |

### Application (`application/`)

| Class | Description |
|---|---|
| `TodoUseCaseImpl` | Implements `TodoUseCase`; orchestrates domain logic via `TodoRepository` |

### Adapters

**Inbound (`adapter/in/http/`)**

| Class | Description |
|---|---|
| `TodoController` | `GET /api/todos`, `POST /api/todos`, `PATCH /api/todos/{id}`, `DELETE /api/todos/{id}` |
| `CreateTodoRequest` | Request DTO: `{ title: String, dueDate: String }` |
| `TodoResponse` | Response DTO: `{ id: UUID, title: String, completed: boolean, dueDate: String }` |
| `TestResetController` | `DELETE /api/todos/reset` — test profile only, clears all data |

**Outbound (`adapter/out/persistence/`)**

| Class | Description |
|---|---|
| `TodoPersistenceAdapter` | Implements `TodoRepository` using Spring Data JPA |
| `TodoJpaEntity` | JPA entity mapped to `todo` table |
| `TodoJpaRepository` | Spring Data `JpaRepository` |

### REST API

| Method | Path | Status | Description |
|---|---|---|---|
| `GET` | `/api/todos` | ✅ implemented | Returns all todos |
| `POST` | `/api/todos` | ✅ implemented | Creates a new todo; body: `{ "title": "...", "dueDate": "..." }` |
| `PATCH` | `/api/todos/{id}` | ✅ implemented | Toggles completed state |
| `DELETE` | `/api/todos/{id}` | ✅ implemented | Deletes a single todo |
| `DELETE` | `/api/todos/reset` | ✅ test only | Deletes all todos (test profile only) |

### Tech Stack

| Attribute | Details |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.x |
| Persistence | Spring Data JPA + PostgreSQL |
| DB migrations | Flyway |
| Build | Maven |
| Test DB | H2 in-memory (unit tests and BDD tests) |

---

## Data Flow

```
Page load
    │
    ▼
GET /api/todos
    │
    ▼
render() — initial UI

User action
    │
    ▼
Event handler (app.js)
    │
    ├── add:    POST /api/todos
    ├── toggle: PATCH /api/todos/{id}
    └── delete: DELETE /api/todos/{id}
    │
    ▼
GET /api/todos → render() — rebuild DOM
    │
    ▼
Updated UI
```

---

## Constraints (from PRD)

- Frontend: Plain HTML/CSS/JS only — no npm, no bundler, no framework
- Backend: Spring Boot, Java 21, Maven, PostgreSQL
- No auth, no routing, no native apps
