---
id: release6
title: Features and Changes in Kotest 6.0
sidebar_label: Release 6.0
---

This page lists the features and changes in Kotest 6.0.

## New Features

### Enhanced Concurrency Support

Kotest 6.0 introduces a comprehensive set of concurrency features to improve test execution:

- **Spec Concurrency Mode**: Controls how specs (test classes) are executed in relation to each other
  - Sequential (default): All specs are executed sequentially
  - Concurrent: All specs are executed concurrently
  - LimitedConcurrency(max: Int): Specs are executed concurrently up to a given maximum number

- **Test Concurrency Mode**: Controls how root tests within a spec are executed in relation to each other
  - Sequential (default): All tests are executed sequentially
  - Concurrent: All tests are executed concurrently
  - LimitedConcurrency(max: Int): Tests are executed concurrently up to a given maximum number

- **Coroutine Dispatcher Factory**: Customize the coroutine dispatcher used for executing specs and tests
  - Built-in implementation: ThreadPerSpecCoroutineContextFactory
  - Can be configured at project-wide or spec-level

- **Blocking Test Mode**: Addresses issues with timeouts when working with blocking code
  - Switches execution to a dedicated thread for the test case
  - Allows the test engine to safely interrupt tests when they time out

### Package-Level Configuration

Package-level configuration allows you to define shared configuration that applies to all specs in a specific package and its sub-packages:

- Create a `PackageConfig` class that extends `AbstractPackageConfig` in the target package
- Configuration resolution follows a clear hierarchy (test-specific → spec-level → package-level → parent package → global)
- Supports various configuration options (isolation mode, assertion mode, timeouts, etc.)

### Shared Test Configuration

The new `DefaultTestConfig` feature allows you to define shared test configuration that applies to all tests in a spec:

- Set default configuration values like timeout, invocations, tags, etc.
- Individual tests can override any part of the default configuration
- Simplifies configuration management for tests with similar requirements

### New Isolation Mode: InstancePerRoot

A new isolation mode `InstancePerRoot` has been introduced:

- Creates a new instance of the Spec class for every top-level (root) test case
- Each root test is executed in its own associated instance
- Provides better isolation while maintaining a clean structure

### TestClock Implementation

A new `TestClock` implementation has been added for controlling time in tests:

- Mutable Clock that supports millisecond precision
- Allows setting specific instants and manipulating time with plus and minus operations
- Useful for testing time-dependent code in a deterministic way

### Enhanced Coroutine Debugging

Improved support for debugging coroutines in tests:

- CoroutineDebugProbeInterceptor for installing the kotlinx debug probe for coroutines
- Helps with debugging by providing stack traces and dumping coroutine information
- Can be enabled on a per-test basis

### Decoroutinator Extension

A new extension for improving coroutine stack traces:

- Integrates with [Stacktrace Decoroutinator](https://github.com/Anamorphosee/stacktrace-decoroutinator)
- Removes internal coroutine implementation details from stack traces
- Makes stack traces cleaner and easier to understand
- Helps quickly identify the source of errors in coroutine-based tests

### Power Assert Support

Kotest 6.0 integrates with Kotlin 2.2's Power Assert feature to provide enhanced assertion failure messages:

- Displays values of each part of an expression when an assertion fails
- Makes debugging test failures easier by showing the actual values in the expression
- Works with `shouldBe` and other configurable assertion functions
- See the [Power Assert documentation](assertions/power-assert.md) for details and setup instructions

## Breaking Changes

### Minimum Versions

Kotest 6.0 requires a minimum of JDK 11 and Kotlin 2.2.

### Kotlin Multiplatform Support

The KMP support in Kotest 6.0 has changed from previous versions:

- No longer requires a compiler plugin
- Simplified setup process for multiplatform projects
- See the [setup documentation](https://kotest.io/docs/framework/project-setup.html) for details

### Extensions Publishing

All extensions are now published under the `io.kotest` group:

- Version cadence tied to main Kotest releases
- Simplifies dependency management
- Affects all extension modules (Allure, Koin, Ktor, MockServer, WireMock, Spring, etc.)

### Project Configuration Location

The location of the project config instance is now required to be at a specific path:

- By default will be at `io.kotest.provided.ProjectConfig`
- Can be overridden by setting the `kotest.framework.config.fqn` system property
- Will not be picked up by the framework if located elsewhere
- Different from Kotest 5.x behavior

### Removed Classpath Scanning

Classpath scanning for extensions has been removed in Kotest 6.0:

- The `@AutoScan` annotation is no longer supported
- Extensions must now be explicitly registered

To register extensions, use one of these approaches:
- Add extensions to your project config:
  ```kotlin
  object ProjectConfig : AbstractProjectConfig() {
    override val extensions = listOf(
      MyExtension(),
      AnotherExtension()
    )
  }
  ```
- Use the `@ApplyExtension` annotation on your spec classes:
  ```kotlin
  @ApplyExtension(MyExtension::class)
  class MySpec : FunSpec() {
    // tests here
  }
  ```

### Data Driven Testing

If you are using the Kotest 5.0+ `withData` support, you no longer need to add the `kotest-framework-data` dependency
to your project as this has been merged into the core framework.

### Table Driven Testing

If you are using the Kotest 4.x era table driven testing, you will need to add the `kotest-assertions-table` dependency
to your project as this has been moved out of the core framework.

### Extension overrides

Inside the project config, extensions are now a val not a function.
So if you had before:

```kotlin
override fun listeners() = ...
```

or

```kotlin
override fun extensions() = ...
```

Change this to:

```kotlin
override val extensions = ...
```

### Removed listeners

The System.exit and System.env override extensions have been removed due to the deprecation of the SecurityManager in Java.

### Deprecated Isolation Modes

The following isolation modes are now deprecated due to undefined behavior in edge cases:

- `InstancePerTest`
- `InstancePerLeaf`

It is recommended to use `InstancePerRoot` instead.

### API changes

* Removed `io.kotest.assertions.print.Print<A>.print(A, level)` in favor of the now-undeprecated `print(A)`.
* Renamed `io.kotest.matchers.maps.contain` to `io.kotest.matchers.maps.mapcontain`.

## Improvements

### Coroutine Debug Probes

Enhanced support for coroutine debugging:

- Option to enable debug probes for better visibility into coroutine execution
- Helps identify issues with coroutines in tests
- Provides detailed stack traces and coroutine dumps when errors occur
