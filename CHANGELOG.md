Changelog
=========

This project follows [semantic versioning](http://semver.org/).

Version 1.3.0, released YYYY-MM-DD TODO
----------------------------------

[Closed Issues](https://github.com/kotlintest/kotlintest/issues?utf8=%E2%9C%93&q=is%3Aclosed+milestone%3A2.0)

### Added

* Table Tests. Tables allow you to manually specific combinations of values that should be used, and are useful for edge cases and other specific values you want to test. An example of using a table consisting of two-value tuples:

```kotlin
class TableExample : TableTesting() {
  init {
    val table = table(
      headers("a", "b"),
      row(5, 5),
      row(4, 6),
      row(3, 7)
    )
    "numbers should be prime".forAll(table) { a, b ->
      a + b == 10
    }
  }
}
```

The headers are used for when values fail, the output can show you what inputs were used for what labels.

* `containInAnyOrder` matcher. You can now write

```kotlin
val col = listOf(1,2,3,4,5)
col should containInAnyOrder(4,2,3)
```

* `haveKey` Map<K,V> matcher. You can now write

```kotlin
val map = mapOf(Pair(1, "a"), Pair(2, "b"))
map should haveKey(1)
```

* `haveValue` Map<K,V> matcher. You can now write

```kotlin
val map = mapOf(Pair(1, "a"), Pair(2, "b"))
map should haveValue("a")
```

* `beTheSameInstanceAs` reference matcher. This is an alias for `x should be theSameInstanceAs(y)`, allowing `x should beTheSameInstanceAs(y)` which fits in with new matcher style.

### Changed

#### Replaced `timeout` + `timeUnit` with `Duration` ([#29](https://github.com/kotlintest/kotlintest/issues/29))

You can now write `config(timeout = 2.seconds)` instead of 
`config(timeout = 2, timeoutUnit = TimeUnit.SECONDS)`.

### Deprecated

nothing

### Removed

nothing

### Fixed

nothing