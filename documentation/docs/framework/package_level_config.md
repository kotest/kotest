---
title: Package Level Config
slug: package-level-config.html
---

# Package Level Config

This page describes how to use package-level configuration to share configuration across multiple specs in the same
package.

## Introduction

When writing tests, you often need to apply the same configuration to multiple test files in the same package.
Instead of repeating the same configuration for each spec, or setting it at the global project level, you can use
package-level configuration to define a shared configuration that applies to all specs in a specific package and its
sub-packages.

Package-level configuration works by creating a class named `PackageConfig` that extends `AbstractPackageConfig` in the
package where you want to apply the configuration.

## Basic Usage

To set a default configuration for all specs in a package, create a class named `PackageConfig` that extends
`AbstractPackageConfig` in the target package:

```kotlin
// In package: com.example.mypackage
class PackageConfig : AbstractPackageConfig() {
  override val timeout = 5.seconds
  override val invocations = 2
  override val failfast = true
}
```

With this configuration:

- All tests in specs within the `com.example.mypackage` package will have a timeout of 5 seconds
- All tests will run twice (2 invocations)
- Tests will use failfast mode

This configuration will also apply to all sub-packages (e.g., `com.example.mypackage.subpackage`).

## Configuration Resolution Order

Kotest uses the following order to resolve configuration values:

1. Test-specific configuration (set via `.config()`)
2. Spec-level overrides (set via variables in the spec class)
3. Spec-level default configuration (set via `defaultTestConfig`)
4. Package-level configuration (set via `PackageConfig`)
5. Parent package configuration (if any)
6. Global configuration (set via `ProjectConfig`)
7. System properties or environment variables

This means that more specific configurations will override more general ones. For example, if you set a timeout at both
the test level and in a package-level config, the test-level timeout will be used.

## Available Configuration Options

`AbstractPackageConfig` supports the following configuration options:

- `isolationMode`: Controls how tests are isolated from each other
- `assertionMode`: Controls how assertions behave
- `testCaseOrder`: Controls the order in which tests are executed
- `timeout`: Maximum time a test is allowed to run
- `invocationTimeout`: Maximum time each individual invocation is allowed to run
- `failfast`: Whether to fail fast on the first failure
- `retries`: Number of times to retry a failing test
- `coroutineDebugProbes`: Whether to enable enhanced tracing of coroutines when an error occurs
- `coroutineTestScope`: Whether to use a coroutine test scope
- `duplicateTestNameMode`: Controls what to do when a duplicated test name is discovered
- `assertSoftly`: Whether to use soft assertions for all tests
- `testExecutionMode`: Controls how tests are executed (sequentially or concurrently)
- `extensions`: List of extensions to apply to all tests in the package

## Examples

### Example: Setting Timeouts and Retries

```kotlin
// In package: com.example.api.tests
class PackageConfig : AbstractPackageConfig() {
  // All API tests might need longer timeouts and retries
  override val timeout = 30.seconds
  override val retries = 3
}
```

### Example: Configuring Test Execution Mode

```kotlin
// In package: com.example.unit.tests
class PackageConfig : AbstractPackageConfig() {
  // Run all unit tests concurrently for faster execution
  override val testExecutionMode = TestExecutionMode.Concurrent
}
```

### Example: Adding Extensions for a Package

```kotlin
// In package: com.example.database.tests
class PackageConfig : AbstractPackageConfig() {
  // Add a database container for all database tests
  override val extensions = listOf(
    ContainerExtension(PostgreSQLContainer<Nothing>().withDatabaseName("testdb"))
  )
}
```

## Package Hierarchy

When you have package-level configurations at different levels of your package hierarchy, the configuration closest to
the spec's package takes precedence.

For example, if you have:

```
com.example.PackageConfig
com.example.api.PackageConfig
com.example.api.v1.PackageConfig
```

And your test is in `com.example.api.v1.UserTest`, then:

1. `com.example.api.v1.PackageConfig` will be applied first
2. Any values not set there will fall back to `com.example.api.PackageConfig`
3. Any values not set in either will fall back to `com.example.PackageConfig`
4. Finally, any values not set in any package config will fall back to the project config

## Implementation Details

Kotest automatically detects classes named `PackageConfig` that extend `AbstractPackageConfig` at runtime. The detection
happens when a test is executed, and Kotest looks for package configs in the package of the test and all parent
packages.

For performance reasons, package configs are cached after they are first loaded, so changes to a package config class
will only take effect after restarting the test run.
