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

  Scenario: Todo als abgeschlossen markieren
    When I create a todo with title "Buy milk"
    And I toggle the todo
    Then the response status is 200
    And the response todo is completed

  Scenario: Abgeschlossenes Todo zurück auf aktiv setzen
    When I create a todo with title "Buy milk"
    And I toggle the todo
    And I toggle the todo
    Then the response status is 200
    And the response todo is not completed

  Scenario: Nur das richtige Todo wird getoggled
    Given a todo with title "Walk the dog" exists
    When I create a todo with title "Buy milk"
    And I toggle the todo
    And I get all todos
    Then the todo with title "Buy milk" in the list is completed
    And the todo with title "Walk the dog" in the list is not completed

  Scenario: Toggle-Zustand ist in der Gesamtliste sichtbar
    When I create a todo with title "Buy milk"
    And I toggle the todo
    And I get all todos
    Then the todo with title "Buy milk" in the list is completed

  Scenario: Toggle eines nicht existierenden Todos
    When I toggle a todo with id "00000000-0000-0000-0000-000000000000"
    Then the response status is 404

  Scenario: Todo löschen
    When I create a todo with title "Buy milk"
    And I delete the todo
    Then the response status is 204

  Scenario: Gelöschtes Todo ist nicht mehr in der Liste
    When I create a todo with title "Buy milk"
    And I delete the todo
    And I get all todos
    Then the response contains 0 todos

  Scenario: Löschen eines nicht existierenden Todos
    When I delete a todo with id "00000000-0000-0000-0000-000000000000"
    Then the response status is 404

  Scenario: Todo mit leerem Titel wird abgelehnt
    When I create a todo with title ""
    Then the response status is 400
