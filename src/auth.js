const authView  = document.getElementById('auth-view');
const todoView  = document.getElementById('todo-view');
const authError = document.getElementById('auth-error');

function showAuth() {
  authView.classList.remove('hidden');
  todoView.classList.add('hidden');
}

function showTodo() {
  authView.classList.add('hidden');
  todoView.classList.remove('hidden');
}

function showError(msg) {
  authError.textContent = msg;
  authError.classList.remove('hidden');
}

function clearError() {
  authError.textContent = '';
  authError.classList.add('hidden');
}

export function initAuth({ onAuthenticated }) {
  const token = localStorage.getItem('token');
  if (token) {
    showTodo();
    onAuthenticated(token);
  } else {
    showAuth();
  }

  document.getElementById('login-btn').addEventListener('click', async () => {
    clearError();
    const username = document.getElementById('username-input').value.trim();
    const password = document.getElementById('password-input').value;
    if (!username || !password) return;
    try {
      const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      if (!res.ok) { showError('Invalid username or password.'); return; }
      const { token } = await res.json();
      localStorage.setItem('token', token);
      showTodo();
      onAuthenticated(token);
    } catch {
      showError('Could not connect to server.');
    }
  });

  document.getElementById('register-btn').addEventListener('click', async () => {
    clearError();
    const username = document.getElementById('username-input').value.trim();
    const password = document.getElementById('password-input').value;
    if (!username || !password) return;
    try {
      const res = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      if (res.status === 409) { showError('Username already taken.'); return; }
      if (!res.ok) { showError('Registration failed.'); return; }
      const loginRes = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      const { token } = await loginRes.json();
      localStorage.setItem('token', token);
      showTodo();
      onAuthenticated(token);
    } catch {
      showError('Could not connect to server.');
    }
  });

  document.getElementById('logout-btn').addEventListener('click', () => {
    localStorage.removeItem('token');
    document.getElementById('username-input').value = '';
    document.getElementById('password-input').value = '';
    showAuth();
  });
}
