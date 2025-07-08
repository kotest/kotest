---
title: Kotest 6.0 migration guide
sidebar_label: Migration guide
slug: migration-guide.html
---

Table of contents
- [Multiplatform testing in v6](#multiplatform-testing-in-v6)
- [Kotest extensions](#kotest-extensions)
- [Deprecations](#deprecations)
  - [Listeners](#listeners)
  - [JdbcDatabaseContainerExtension](#jdbcdatabasecontainerextension)

## Multiplatform testing in v6
We have ditched writing a full compiler plugin, as it's prone to breaking with every Kotlin release.
We are now just using the `io.kotest` gradle plugin (following the main Kotest release cycle), and we
are utilizing KSP (Kotlin Symbol Processing) to generate the necessary code for multiplatform testing.

Replace
```kotlin
plugins {
    id("io.kotlin.multiplatform")
}
```

With
```kotlin
plugins {
  id("io.kotest") version "6.0.0"
  id("com.google.devtools.ksp") version "2.2.0-2.0.2"
}

// Also add the kotest symbol processor as a dependency
dependencies {
  testImplementation("io.kotest:kotest-framework-symbol-processor:6.0.0")
}
```

## Kotest extensions
- All extensions have had their groupId's changed from `io.kotest.extensions`, to just `io.kotest`.
- Extensions will follow the main Kotest release cycle and versioning.

### kotest-extensions-clock
- `TestClock` has been moved to the `io.kotest:kotest-extensions` artifact.

## Deprecations

### Listeners
Listeners, having been deprecated for a long while, have now been completely removed. Use extensions instead.

### JdbcDatabaseContainerExtension
