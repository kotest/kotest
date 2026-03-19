---
id: sql-matchers
title: SQL Matchers
slug: sql-matchers.html
sidebar_label: SQL
---

Matchers for `java.sql.ResultSet` are included in the `kotest-assertions-core` module.
No additional dependency is required.

These matchers are JVM only.

| ResultSet                                           |                                                                                                                                                                                |
|-----------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `resultSet.shouldHaveRows(count)`                   | Asserts that the ResultSet is positioned at the given row number.                                                                                                              |
| `resultSet.shouldNotHaveRows(count)`                | Asserts that the ResultSet is not positioned at the given row number.                                                                                                          |
| `resultSet.shouldHaveColumns(count)`                | Asserts that the ResultSet has the given number of columns.                                                                                                                    |
| `resultSet.shouldNotHaveColumns(count)`             | Asserts that the ResultSet does not have the given number of columns.                                                                                                          |
| `resultSet.shouldContainColumn(name)`               | Asserts that the ResultSet contains a column with the given name.                                                                                                              |
| `resultSet.shouldNotContainColumn(name)`            | Asserts that the ResultSet does not contain a column with the given name.                                                                                                      |
| `resultSet.shouldHaveColumn<T>(name) { values -> }` | Asserts that the ResultSet contains a column with the given name, then collects all values from that column as `List<T>` and passes them to the lambda for further assertions. |
| `resultSet.shouldHaveRow(rowNum) { values -> }`     | Navigates to the given row number and passes all column values from that row as `List<Any>` to the lambda for further assertions.                                              |

### Examples

```kotlin
// Simple assertions
resultSet shouldHaveRows 3
resultSet shouldHaveColumns 5
resultSet shouldContainColumn "user_id"

// Assert column values
resultSet.shouldHaveColumn<String>("username") { values ->
    values shouldContainAll listOf("alice", "bob")
}

// Assert a specific row
resultSet.shouldHaveRow(1) { row ->
    row shouldContain "alice"
    row shouldContain 42
}
```

