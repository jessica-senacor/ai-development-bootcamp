import { Given, When, Then } from '@cucumber/cucumber';
import assert from 'assert';

const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';

When('I create a todo with title {string}', async function (title) {
  this.response = await fetch(`${API_BASE_URL}/api/todos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title }),
  });
  this.responseBody = await this.response.json();
});

Then('the response status is {int}', function (expectedStatus) {
  assert.strictEqual(this.response.status, expectedStatus);
});

Then('the response todo has title {string}', function (expectedTitle) {
  assert.strictEqual(this.responseBody.title, expectedTitle);
});

Then('the response todo is not completed', function () {
  assert.strictEqual(this.responseBody.completed, false);
});

Given('a todo with title {string} exists', async function (title) {
  await fetch(`${API_BASE_URL}/api/todos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title }),
  });
});

When('I get all todos', async function () {
  this.response = await fetch(`${API_BASE_URL}/api/todos`);
  this.responseBody = await this.response.json();
});

Then('the response contains {int} todos', function (expectedCount) {
  assert.strictEqual(this.responseBody.length, expectedCount);
});

Then('the response todos include a todo with title {string}', function (expectedTitle) {
  const found = this.responseBody.some((todo) => todo.title === expectedTitle);
  assert.strictEqual(found, true, `No todo with title "${expectedTitle}" found`);
});
