---
id: assertion_mode
title: Assertion Mode
slug: assertion-mode.html
---



If you are using Kotest framework alongside Kotest assertions, you can ask Kotest to fail the build, or output a warning to stderr, if a test is executed that does not execute an assertion.

To do this, set `assertionMode` to `AssertionMode.Error` or `AssertionMode.Warn` inside a spec. For example.

```kotlin
class MySpec : FunSpec() {
   init {
      assertions = AssertionMode.Error
      test("this test has no assertions") {
         val name = "sam"
         name.length == 3 // this isn't actually testing anything
      }
   }
}
```

Running this test will output something like:

```
Test 'this test has no assertions' did not invoke any assertions
```

If we want to set this globally, we can do so in [project config](../framework/project_config.md) or via the system property `kotest.framework.assertion.mode`.


:::note
Assertion mode only works for Kotest assertions and not other assertion libraries.
:::

