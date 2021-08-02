---
title: Multiplatform
slug: multiplatform.html
---

** work in progress **

This page describes how to use Kotest with Kotlin multiplatform.

Note: Only IR mode is supported for Javascript - Legacy is discontinued.

### Gradle Plugin

Add the [Kotest multiplatform gradle plugin](https://plugins.gradle.org/plugin/io.kotest.multiplatform) to your build.

For example:

```kotlin
plugins {
   kotlin("io.kotest.multiplatform").version("5.0.0.3")
}
```

### Engine Dependency

Add the Kotest engine module to your build - either as a common dependency or as a platform specific dependency.

For example, either:

```kotlin
val commonTest by getting {
   dependencies {
      implementation("io.kotest:kotest-framework-engine:<version>")
   }
}
```

Or

```kotlin
val jsTest by getting {
   dependencies {
      implementation("io.kotest:kotest-framework-engine-js:<version>")
   }
}
```

### Write Tests and run from gradle

Tests for multiplatform can only use the `FunSpec`, `ShouldSpec` and `StringSpec` spec styles.

Note: The Javascript and Native test engines are more feature limited than the JVM test engine.

When using multiplatform you must use the `check` task and not the `test` task.

`./gradlew check`
