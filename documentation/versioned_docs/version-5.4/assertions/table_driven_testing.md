# Table-driven testing

> TK: what current exists is not really documented anywhere, am I right?

## Define a table in code

You can define a table of data that will be used for your test like this:

```kotlin
val table = table(
  headers("id", "name", "username"),
  row(4, "Jean-Michel Fayard", "jmfayard"),
  row(6, "Louis CAD", "LouisCAD"),
)
```

It's now easy to run your asserts for all rows of the table:

## Run asserts forAll rows of the table

```kotlin
test("table-driven testing") {
    table.forAll { id, name, username ->
      id shouldBeGreaterThan 0
      username shouldNotBe ""
    }
}
```

The test will not fail at the first error. Instead, it will always run on all the rows, and report multiple errors if they are present.

Defining a table of data in code is convenient... until you start to have too much rows.

## Export a table to a text file

You can export your data to a text file with the `.table` extension.

```kotlin
val tableFile = testResources.resolve("users.table")
table.writeTo(tableFile)
```

The `users.table` file looks like this:

```csv
id | username | fullName
4  | jmfayard | Jean-Michel Fayard
6  | louis    | Louis Caugnault
```

Curious why it's not just a .csv file?

Well CSV is not a well defined format. Everyone has its flavor and we have too. The `.table` has its rules:

- it always uses `|` as separator
- it must have an header
- cells are trimmed and cannot contain new lines
- it can have comments and blank lines

Basically it's optimized for putting table data in a `.table` file.

We hope you don't use Microsoft Excel to edit the CSV-like file. IntelliJ with the [CSV plugin from Martin Sommer](https://plugins.jetbrains.com/plugin/10037-csv) does that better. You can associate the `.table` extension with it and configure  `|` as your CSV separator. It has a table edition mode too!

Now that your table data lives in a file, it's time to read it!

## Read table from files and execute your asserts

Here is how you read your `.table` file:

```kotlin
val tableFromFile = table(
    headers = headers("id", "username", "fullName"),
    source = testResources.resolve("users.table"),
    transform = { a: String, b: String, c: String ->
        row(a.toInt(), b, c)
    }
)
```

The arguments are:
- the file where your table is stored
- the same headers as before: `headers("id", "username", "fullName")`
- a lambda to convert from strings (everything is a string in the text file) to the typed row you had before


The rest works just like before:

```kotlin
test("table-driven testing from the .table file") {
    // asserts like before
    tableFromFile.forAll { id, name, username ->
        id shouldBeGreaterThan 0
        username shouldNotBe ""
    }
}
```
