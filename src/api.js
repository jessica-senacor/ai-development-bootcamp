const BASE = '';

let _token = null;

export function setToken(token) {
  _token = token;
}

function authHeaders() {
  return {
    'Content-Type': 'application/json',
    ...(_token ? { 'Authorization': `Bearer ${_token}` } : {}),
  };
}

export async function fetchTodos() {
  const res = await fetch(`${BASE}/api/todos`, { headers: authHeaders() });
  if (!res.ok) throw new Error(`GET /api/todos failed: ${res.status}`);
  return res.json();
}

export async function createTodo(title, dueDate = null) {
  const body = { title };
  if (dueDate) body.dueDate = dueDate;
  const res = await fetch(`${BASE}/api/todos`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`POST /api/todos failed: ${res.status}`);
  return res.json();
}

export async function toggleTodo(id) {
  const res = await fetch(`${BASE}/api/todos/${id}`, { method: 'PATCH', headers: authHeaders() });
  if (!res.ok) throw new Error(`PATCH /api/todos/${id} failed: ${res.status}`);
  return res.json();
}

export async function deleteTodo(id) {
  const res = await fetch(`${BASE}/api/todos/${id}`, { method: 'DELETE', headers: authHeaders() });
  if (!res.ok) throw new Error(`DELETE /api/todos/${id} failed: ${res.status}`);
}
