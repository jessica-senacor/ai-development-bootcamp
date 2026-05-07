const BASE = 'http://localhost:8080';

export async function fetchTodos() {
  const res = await fetch(`${BASE}/api/todos`);
  if (!res.ok) throw new Error(`GET /api/todos failed: ${res.status}`);
  return res.json();
}

export async function createTodo(title, dueDate = null) {
  const body = { title };
  if (dueDate) body.dueDate = dueDate;
  const res = await fetch(`${BASE}/api/todos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`POST /api/todos failed: ${res.status}`);
  return res.json();
}

export async function toggleTodo(id) {
  const res = await fetch(`${BASE}/api/todos/${id}`, { method: 'PATCH' });
  if (!res.ok) throw new Error(`PATCH /api/todos/${id} failed: ${res.status}`);
  return res.json();
}

export async function deleteTodo(id) {
  const res = await fetch(`${BASE}/api/todos/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error(`DELETE /api/todos/${id} failed: ${res.status}`);
}
