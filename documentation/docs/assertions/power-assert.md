---
id: power-assert
title: Power Assert
sidebar_label: Power Assert
---

# Power Assert

Power Assert support was introduced in Kotest 6.0 that enhances assertion failure messages by providing detailed
information about the values of each part of an expression when an assertion fails. This makes it easier to understand
why an assertion failed without having to add additional debug statements.

## How It Works

When an assertion fails, Power Assert shows the values of each part of the expression in the error message, making it
clear what went wrong. This is particularly useful for complex expressions with method calls or property access chains.

For example, consider this assertion:

```kotlin
val hello = "Hello"
val world = "world!"
hello.substring(1, 3) shouldBe world.substring(1, 4)
```

Without Power Assert, the error message would simply be:

```
expected:<"orl"> but was:<"el">
```

With Power Assert enabled, the error message becomes much more informative:

```
hello.substring(1, 3) shouldBe world.substring(1, 4)
|     |                        |     |
|     |                        |     orl
|     |                        world!
|     el
Hello

expected:<"orl"> but was:<"el">
```

This detailed output shows the values of each part of the expression, making it immediately clear what's happening:

- `hello` is "Hello"
- `hello.substring(1, 3)` is "el"
- `world` is "world!"
- `world.substring(1, 4)` is "orl"

## Setup

Power Assert is implemented as a Kotlin compiler plugin that's part of Kotlin 2.0+. To use it with Kotest 6.0:

1. Add the Power Assert plugin to your build:

```kotlin
plugins {
  kotlin("jvm") version "2.2.0"
  id("org.jetbrains.kotlin.plugin.power-assert") version "2.2.0"
}
```

2. Configure which assertion functions should be enhanced with Power Assert:

```kotlin
powerAssert {
  functions = listOf("io.kotest.matchers.shouldBe")
}
```
