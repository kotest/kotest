---
id: shared_test_config
title: Shared Test Config
slug: sharedtestconfig.html
---

# Shared Test Config

This page describes how to use `DefaultTestConfig` to share test configuration across multiple test cases in your specs.

:::note
This feature is available in Kotest 6.0 and later.
:::

## Introduction

When writing tests, you often need to apply the same configuration to multiple test files.
Instead of repeating the same configuration for each test, you can use `DefaultTestConfig` to define a
shared configuration that applies to all tests in a spec.

`DefaultTestConfig` is a data class that allows for the configuration of test cases to be easily shared.

Each of the configuration values can be overridden on a per-test basis, but if you wish to have a common set of
defaults that are shared across several tests, then you can create an instance of this class and declare it in each of
the specs that you wish to share the configuration.


## Basic Usage

To set a default configuration for all tests in a spec, assign a `DefaultTestConfig` instance to the `defaultTestConfig`
property in your spec:

```kotlin
class MySpec : FunSpec() {
  init {

    defaultTestConfig = DefaultTestConfig(
      timeout = 2.seconds,
      invocations = 3,
      threads = 2
    )

    // All tests in this spec will use the above configuration by default
    test("test with default config") {
      // This test will run 3 times with a timeout of 2 seconds
    }

    // You can still override the default config for specific tests
    test("test with custom config").config(timeout = 5.seconds) {
      // This test will run 3 times (from default config) with a timeout of 5 seconds
    }
  }
}
```

## Available Configuration Options

`DefaultTestConfig` supports the following configuration options:

- `timeout`: Maximum time a test is allowed to run before it is considered a failure
- `invocationTimeout`: Maximum time each individual invocation is allowed to run
- `invocations`: Number of times to run each test
- `assertSoftly`: Whether to use soft assertions for all tests
- `tags`: Set of tags to apply to all tests
- `severity`: Severity level for all tests
- `enabledIf`: Function that determines if tests should be enabled
- `enabledOrReasonIf`: Function that determines if tests should be enabled and provides a reason if not
- `assertionMode`: Assertion mode to use for all tests
- `testOrder`: Order in which to run tests
- `blockingTest`: Whether tests should be run in a blocking manner
- `coroutineTestScope`: Whether to use a coroutine test scope
- `coroutineDebugProbes`: Whether to enable coroutine debug probes
- `duplicateTestNameMode`: How to handle duplicate test names
- `failfast`: Whether to fail fast on the first failure
- `retries`: Number of times to retry a failing test
- `retryDelay`: Delay between retries

## Examples

### Example: Setting Retry Configuration

```kotlin
class RetrySpec : DescribeSpec() {
  init {

    defaultTestConfig = DefaultTestConfig(retries = 5, retryDelay = 20.milliseconds)

    describe("a flaky test") {
      // This test will be retried up to 5 times with a 20ms delay between retries
      it("should eventually pass") {
        // Test logic here
      }
    }
  }
}
```

### Example: Setting Timeouts and Invocations

```kotlin
class PerformanceSpec : StringSpec() {
  init {

    defaultTestConfig = DefaultTestConfig(
      timeout = 1.minutes,
      invocations = 10,
      invocationTimeout = 5.seconds
    )

    "performance test" {
      // This test will run 10 times, with each invocation having a 5 second timeout
      // The entire test has a 1 minute timeout
    }
  }
}
```

### Example: Using Tags and Assertion Mode

```kotlin
class IntegrationSpec : FunSpec() {
  init {

    defaultTestConfig = DefaultTestConfig(
      tags = setOf(Tags.Integration, Tags.Slow),
      assertionMode = AssertionMode.Error
    )

    test("database connection") {
      // This test will be tagged as Integration and Slow
      // Assertions will throw errors instead of exceptions
    }
  }
}
```

## Overriding Default Configuration

Individual tests can override any part of the default configuration using the `.config()` method:

```kotlin
class MixedSpec : FunSpec() {
  init {
    defaultTestConfig = DefaultTestConfig(
      timeout = 10.seconds,
      invocations = 3
    )

    test("uses default config") {
      // Uses timeout = 10.seconds and invocations = 3
    }

    test("overrides timeout only").config(timeout = 30.seconds) {
      // Uses timeout = 30.seconds and invocations = 3 (from default)
    }

    test("overrides everything").config(timeout = 5.seconds, invocations = 1) {
      // Uses timeout = 5.seconds and invocations = 1
    }
  }
}
```

The order of lookups is as follows:
1. Test-specific configuration (set via `.config()`)
2. Spec-level overrides (set via variables in the spec class)
3. Spec-level default configuration (set via `defaultTestConfig`)
4. Package-level default configuration (set via `PackageConfig`)
4. Global configuration (set via `ProjectConfig`)
5. System properties or environment variables
