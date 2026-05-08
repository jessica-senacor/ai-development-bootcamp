# Architecture

## TODO App — v2.0

---

## Overview

Single-page, in-browser TODO application with a Spring Boot REST backend and PostgreSQL database. The frontend is plain HTML/CSS/JS (no build tools, no frameworks). State is persisted via the backend API; `localStorage` is no longer the primary storage.

> **Integration status:** The backend currently supports listing and creating todos — toggle and delete are not yet implemented there. The frontend currently still reads/writes `localStorage` directly and does not call the backend API yet.

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
│   ├── app.js          # Event wiring and DOM references
│   ├── auth.js         # Login/register/logout UI and token storage
│   ├── addTodo.js      # Creates and appends todo items
│   ├── toggleTodo.js   # Toggles completed state
│   ├── deleteTodo.js   # Removes todo items
│   └── storage.js      # localStorage persistence (save/load) — to be replaced by API calls
└── backend/            # Spring Boot application (Java 21, Maven)
    └── src/main/java/com/example/todoapp/
        ├── domain/
        │   ├── model/
        │   │   ├── Todo.java
        │   │   └── User.java
        │   └── port/
        │       ├── in/
        │       │   ├── TodoUseCase.java
        │       │   └── UserUseCase.java
        │       └── out/
        │           ├── TodoRepository.java
        │           └── UserRepository.java
        ├── application/
        │   ├── TodoUseCaseImpl.java
        │   └── UserUseCaseImpl.java
        └── adapter/
            ├── in/http/
            │   ├── TodoController.java
            │   ├── AuthController.java
            │   ├── CreateTodoRequest.java
            │   ├── RegisterRequest.java
            │   ├── LoginRequest.java
            │   ├── TodoResponse.java
            │   ├── TokenResponse.java
            │   ├── JwtFilter.java
            │   └── TestResetController.java
            └── out/persistence/
                ├── TodoJpaEntity.java
                ├── TodoJpaRepository.java
                ├── TodoPersistenceAdapter.java
                ├── UserJpaEntity.java
                ├── UserJpaRepository.java
                └── UserPersistenceAdapter.java
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

## storage.js

Responsibilities:
- Encapsulates all `localStorage` access under a single key (`'todos'`)
- Keeps persistence details out of `app.js`

| Export | Description |
|---|---|
| `save(todos)` | Serializes `todos` array to JSON and writes to `localStorage` |
| `load()` | Reads and deserializes from `localStorage`; returns `[]` if nothing stored |

---

## app.js

Responsibilities:
- Loads initial state from `storage.js` on startup
- Maintains the `todos` array and renders the full list on every state change
- Handles all user events (add, toggle, delete) and persists after each mutation

### Data model

```js
// Persisted to localStorage under key 'todos'
let todos = load();

// Each item:
{ id: string, title: string, completed: boolean, dueDate: string | null }
```

`id` is generated via `crypto.randomUUID()`.

### Functions

| Function | Description |
|---|---|
| `addTodo(title)` | Appends a new item to `todos`, saves, re-renders |
| `toggleTodo(id)` | Flips `completed` on the matching item, saves, re-renders |
| `deleteTodo(id)` | Removes item by `id`, saves, re-renders |
| `render()` | Clears and rebuilds `#todo-list` from `todos`; toggles empty-state |

### Event wiring (set up on `DOMContentLoaded`)

- `#add-btn` click → `addTodo`
- `#todo-input` keydown `Enter` → `addTodo`
- Delegated click on `#todo-list` → `toggleTodo` (checkbox) or `deleteTodo` (delete button)

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
| `Todo` | Domain model: `id` (UUID), `title` (String), `completed` (boolean) |
| `TodoUseCase` | Inbound port — `getAll()`, `create(title)` |
| `TodoRepository` | Outbound port — `save()`, `findAll()`, `deleteAll()` |

### Application (`application/`)

| Class | Description |
|---|---|
| `TodoUseCaseImpl` | Implements `TodoUseCase`; orchestrates domain logic via `TodoRepository` |

### Adapters

**Inbound (`adapter/in/http/`)**

| Class | Description |
|---|---|
| `TodoController` | `GET /api/todos`, `POST /api/todos` |
| `CreateTodoRequest` | Request DTO: `{ title: String }` |
| `TodoResponse` | Response DTO: `{ id: UUID, title: String, completed: boolean }` |
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
| `POST` | `/api/todos` | ✅ implemented | Creates a new todo; body: `{ "title": "..." }` |
| `PATCH` | `/api/todos/{id}` | ❌ not yet | Toggle completed state |
| `DELETE` | `/api/todos/{id}` | ❌ not yet | Delete a single todo |
| `DELETE` | `/api/todos/reset` | ✅ test only | Deletes all todos (test profile only) |

### Tech Stack

| Attribute | Details |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.x |
| Persistence | Spring Data JPA + PostgreSQL |
| DB migrations | Flyway |
| Build | Maven |
| Test DB | H2 (in-memory), Testcontainers (integration) |

---

## Data Flow

```
Page load
    │
    ▼
load() — read todos[] from localStorage     [current]
    OR
GET /api/todos                              [target]
    │
    ▼
render() — initial UI

User action
    │
    ▼
Event handler (app.js)
    │
    ├── add: POST /api/todos               [target — GET/POST already in backend]
    ├── toggle: PATCH /api/todos/{id}      [target — not yet in backend]
    └── delete: DELETE /api/todos/{id}     [target — not yet in backend]
    │
    ▼
render() — rebuild DOM from response
    │
    ▼
Updated UI
```

---

## Constraints (from PRD)

- Frontend: Plain HTML/CSS/JS only — no npm, no bundler, no framework
- Backend: Spring Boot, Java 21, Maven, PostgreSQL
- No auth, no routing, no native apps
