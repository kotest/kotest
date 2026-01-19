---
id: fail_fast
title: Fail Fast
slug: fail-fast.html
sidebar_label: Fail Fast
---

Kotest can eagerly fail a list of tests if one of those tests fails. This is called _fail fast_.

Fail fast can take affect at the spec level, or at a parent test level.

In the following example, we enable failfast for a parent test, and the first failure inside that context,
will cause the rest to be skipped.

```kotlin
class FailFastTests() : FunSpec() {
   init {
      context("context with fail fast enabled").config(failfast = true) {
         test("a") {} // pass
         test("b") { error("boom") } // fail
         test("c") {} // skipped
         context("d") {  // skipped
            test("e") {} // skipped
         }
      }
   }
}
```

This can be enabled for all scopes in a Spec by setting failfast at the spec level.

```kotlin
class FailFastTests() : FunSpec() {
   init {

      failfast = true

      context("context with fail fast enabled at the spec level") {
         test("a") {} // pass
         test("b") { error("boom") } // fail
         test("c") {} // skipped
         context("d") {  // skipped
            test("e") {} // skipped
         }
      }
   }
}
```
