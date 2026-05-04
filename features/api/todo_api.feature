@api
Feature: TODO REST API

  Scenario: Todo erstellen
    When I create a todo with title "Buy milk"
    Then the response status is 201
    And the response todo has title "Buy milk"
    And the response todo is not completed

  Scenario: Alle Todos abrufen
    Given a todo with title "Buy milk" exists
    And a todo with title "Walk the dog" exists
    When I get all todos
    Then the response status is 200
    And the response contains 2 todos
    And the response todos include a todo with title "Buy milk"
    And the response todos include a todo with title "Walk the dog"
