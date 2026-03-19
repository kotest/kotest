---
id: logging
title: Logging while Testing
sidebar_label: Logging
slug: logging.html
---

Sometimes we need to write logging statements to give a little more context when things go wrong.
In Kotest we provide the standard logging functions in the scope of each test that one would expect: `error`, `warn`, `info`, `debug`, and `trace`.
Each function accepts an expression that is only executed when the configured logging level or greater is set by config, e.g.: `warn { "foo" }` would be executed if `kotest.framework.loglevel=info`.

## Setup

In order to get logging working you'll need two things:
1. at least one implementation of `LogExtension` added to extensions in your `AbstractProjectConfig`
2. a configured value for logLevel added to your `AbstractProjectConfig`, or set using the sysprop or environment variable `kotest.framework.loglevel`

**Note:** the sysprop and environment variable will override the setting in your `AbstractProjectConfig`

```kotlin
object ProjectConfig : AbstractProjectConfig() {
    override val logLevel = LogLevel.Error
    override fun extensions() = listOf(
        object : LogExtension {
            override suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>) {
                logs.forEach { println(it.level.name + " - " + it.message) }
            }
        }
    )
}
```

## Usage

Now you can just log using the aforementioned extension methods on the Kotest `TestScope`

```kotlin
test("test something") {
  warn { "something weird happened" }
}
```

Additionally, we provide a logger object to pass around your tests, in the case that you write a test function

```kotlin
test("test something else") {
  logger.assertSomething("something else")
}

fun TestLogger.assertSomething(actual: String) {
   info { "asserting something about $actual" }
}
```
