---
title: Custom Spec Styles
slug: custom-styles.html
---

Kotest ships with [nine built-in spec styles](styles.md) to give you extensive power right out of the box, but if
you want to go further and define your own styles, Kotest is fully customizable. A custom spec style lets you derive
whatever DSL syntax you want while plugging into the full Kotest lifecycle, extensions, and IntelliJ IDEA integration.

## Overview

Creating a custom spec style involves these pieces:

1. **Extend `AbstractSpec`** — the public base class for all spec styles.
2. **Define test functions annotated with `@TestRunnable`** — these become the vocabulary of your DSL.
3. **Optionally create Scope interfaces that extend `TestScope`** — to expose nested DSL methods inside a container.

:::tip
If you want to see a real-world example of a custom spec style, then check out
the [Prepared](https://prepared.opensavvy.dev/api/runner-kotest/index.html) library, which offers an advanced
fixture-based DSL on top of Kotest.
:::

## Step 1 — Define a spec base class

Create a subclass of `io.kotest.core.spec.AbstractSpec` with the name of your style. In this example, we'll make a
style we will call `SuiteSpec`, which has `suite` and `test` functions. These functions are the keywords
of your test style – the DSL you are creating. In this example, `suite` will act as parent test containers, and `test`
as leaf level tests.

These methods will delegate to the `add` method provided by the `AbstractSpec` base class which is used to register
tests. This `add` method expects instances of `TestDefinition` which represent the test in source code. Create these
`TestDefinition`s by using the `TestDefinitionBuilder` builder.

Tests in Kotest are of the type `suspend TestScope.() -> Unit`. These are the test bodies that are executed
when a test is run and will be passed into the builders that create the test definition. Notice in the example below
that the `test` lambda for the `suite` fnction is actually of type `suspend SuiteScope.() -> Unit`. This is because
suites can contain nested tests, and so we need to provide helper methods for registering nested tests using the same
syntax as this `SuiteSpec` class. More on this in the next step.

```kotlin
import io.kotest.core.spec.AbstractSpec

abstract class SuiteSpec : AbstractSpec() {

  @TestRunnable
  fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
    add(
      TestDefinitionBuilder
        .builder(TestNameBuilder.builder(name).build(), TestType.Container)
        .build { SuiteScope(this).test() }
    )
  }

  @TestRunnable
  fun test(name: String, test: suspend TestScope.() -> Unit) {
    add(
      TestDefinitionBuilder
        .builder(TestNameBuilder.builder(name).build(), TestType.Test)
        .build(test)
    )
  }
}
```

Notice that the DSL methods - `suite` and `test` - are annotated with `@TestRunnable`. This instructs the IntelliJ
plugin to recognize them as test entry points.

:::tip
When creating a runnable test method, the function must have a String parameter as the first parameter.
This parameter represents the test name and is used to display the test name in the test runner UI.
:::

## Step 2 — Create Scope interfaces for nested tests

When tests can be nested, it is recommended to extend `TestScope` to add scope-specific DSL functions. While
`TestScope` allows nested tests via the `registerTest` method, you typically want to expose nested DSL
delegates that align with your test style.

Extend `io.kotest.core.test.AbstractTestScope` and add your DSL methods. In this example, we are repeating
the same syntax we had at the root level. The `registerTest` method here is analagous to the `add` method
at the root level.

```kotlin
class SuiteScope(delegate: TestScope) : AbstractTestScope(delegate) {

  @TestRunnable
  suspend fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
    registerTest(
      TestDefinitionBuilder
        .builder(TestNameBuilder.builder(name).build(), TestType.Container)
        .build { SuiteScope(this).test() }
    )
  }

  @TestRunnable
  suspend fun test(name: String, test: suspend TestScope.() -> Unit) {
    registerTest(
      TestDefinitionBuilder
        .builder(TestNameBuilder.builder(name).build(), TestType.Test)
        .build(test)
        .build { SuiteScope(this).test() }
    )
  }
}
```

## IntelliJ IDEA integration

The Kotest IntelliJ plugin automatically recognises `@TestRunnable`-annotated functions inside
classes that extend `AbstractSpec`. This means:

* Gutter run icons appear next to each `test(...)` and `suite(...)` call.
* Right-clicking a test name in the editor and selecting **Run** executes just that test.
* The test tree in the **Run** tool window reflects the nesting produced by container calls.

No additional plugin configuration is needed.
