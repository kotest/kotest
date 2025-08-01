---
id: decoroutinator
title: Decoroutinator
sidebar_label: Decoroutinator
slug: decoroutinator.html
---

The Kotest Decoroutinator extension integrates [Stacktrace Decoroutinator](https://github.com/Anamorphosee/stacktrace-decoroutinator) with Kotest. Decoroutinator improves stack traces in Kotlin coroutines by removing the internal coroutine implementation details, making stack traces cleaner and easier to understand.

:::note
To use this extension add the `io.kotest:kotest-extensions-decoroutinator` module to your test compile path.
:::

[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-decoroutinator.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-decoroutinator)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-decoroutinator%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-decoroutinator/maven-metadata.xml)

### Getting Started

Register the `DecoroutinatorExtension` in your test class:

```kotlin
class MyTest : FunSpec({
   extension(DecoroutinatorExtension())

   test("with clean stack traces") {
      // Your test code with coroutines
   }
})
```

The `DecoroutinatorExtension` can also be registered at the [project level](../framework/project-config.html) for all tests:

```kotlin
class ProjectConfig : AbstractProjectConfig() {
   override fun extensions() = listOf(DecoroutinatorExtension())
}
```

### How It Works

When a test fails due to an exception in a coroutine, the stack trace typically contains many internal coroutine implementation details that make it difficult to understand the actual cause of the failure. The Decoroutinator extension automatically installs the Decoroutinator JVM API, which:

1. Removes internal coroutine implementation details from stack traces
2. Makes the stack traces more readable and focused on your code
3. Helps you quickly identify the source of errors in coroutine-based tests

### Example

Without Decoroutinator, a stack trace might look like:

```
java.lang.IllegalStateException: Test exception
    at com.example.MyTest$1$1.invokeSuspend(MyTest.kt:15)
    at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
    at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
    at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:570)
    at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:750)
    at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:677)
    at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:664)
    ... many more internal details
```

With Decoroutinator, the same stack trace becomes:

```
java.lang.IllegalStateException: Test exception
    at com.example.MyTest$1$1.invokeSuspend(MyTest.kt:15)
    ... coroutine implementation details removed for clarity
```

This makes it much easier to identify and fix issues in your coroutine-based tests.
