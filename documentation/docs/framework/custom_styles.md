---
title: Custom Spec Styles
slug: custom-styles.html
---

Kotest ships with [nine built-in spec styles](styles.md), but if none of them fit your project, you can define your
own. A custom spec style gives you full control over the DSL syntax while still plugging into the Kotest lifecycle,
extensions, and IntelliJ IDEA integration.

## Overview

Creating a custom spec style requires three things:

1. A base class that extends `AbstractSpec` and defines the root-level DSL functions.
2. DSL functions annotated with `@TestRunnable` so the IntelliJ plugin recognises them as test entry points.
3. Optionally, a scope class for each nesting level that exposes the same (or different) DSL inside containers.

:::tip
The [Prepared](https://prepared.opensavvy.dev/api/runner-kotest/index.html) library is a real-world example of a
custom spec style built on top of Kotest.
:::

## Step 1: Define a spec base class

Create a class that extends `io.kotest.core.spec.AbstractSpec`. This class defines the vocabulary of your DSL. In
this example, we will create a `SuiteSpec` with two functions: `suite` for container blocks and `test` for leaf tests.

Both functions call `add`, which registers the test with the engine. `add` takes a `TestDefinition`, built with
`TestDefinitionBuilder`. Notice that the `suite` function wraps its body in a `SuiteScope` receiver; that is the
class we will define in the next step.

```kotlin
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.AbstractSpec
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestRunnable
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

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

The `@TestRunnable` annotation tells the IntelliJ plugin that calls to `suite(...)` and `test(...)` are test entry
points. The first parameter of any `@TestRunnable` function must be a `String`; the plugin uses it as the test name.

:::tip
If you want to support the lambda constructor style used by Kotest's built-in specs (`class MyTests : SuiteSpec({ ... })`),
add a body parameter and call it in `init`:

```kotlin
abstract class SuiteSpec(body: SuiteSpec.() -> Unit = {}) : AbstractSpec() {
   init { body() }
   // ... DSL functions as above
}
```

Without this, tests are registered using an `init` block in the subclass, which works just as well.
:::

## Step 2: Create a scope class for nested tests

When tests can be nested inside containers, you need a scope class that exposes the same DSL inside the container
body. Extend `io.kotest.core.test.AbstractTestScope`, which handles the `TestScope` delegation for you, and add the
same DSL functions. The difference from step 1 is that inside a running test you call `registerTest` instead of `add`.

```kotlin
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestRunnable
import io.kotest.core.test.AbstractTestScope
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

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
      )
   }
}
```

## Step 3: Write tests using your custom style

With both classes in place, writing tests looks exactly like any built-in spec style:

```kotlin
class UserTests : SuiteSpec() {
   init {
      test("homepage loads") { /* ... */ }

      suite("user login") {
         test("accepts valid credentials") { /* ... */ }
         test("rejects invalid password") { /* ... */ }

         suite("edge cases") {
            test("handles expired sessions") { /* ... */ }
         }
      }
   }
}
```

If you added the optional body lambda constructor (see the tip in step 1), the compact lambda style also works:

```kotlin
class UserTests : SuiteSpec({
   test("homepage loads") { /* ... */ }

   suite("user login") {
      test("accepts valid credentials") { /* ... */ }
   }
})
```

## IntelliJ IDEA integration

The Kotest IntelliJ plugin automatically recognises `@TestRunnable`-annotated functions inside classes that extend
`AbstractSpec`. Gutter run icons appear next to each call, you can right-click a test name and select **Run** to
execute just that test, and the test tree in the **Run** tool window reflects the nesting structure.

No additional plugin configuration is needed.
