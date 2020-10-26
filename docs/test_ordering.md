Test Ordering
=====

When running multiple tests from a Spec, there's a certain order on how to execute them.

 By default, a **sequential** order is used (order that tests are defined in the spec), but it's also possible to configure them
 to be executed in a **random** order or **lexicographic** order.

This setting can be configured in either a `Spec` or in [ProjectConfig](project_config.md) by overriding the `testCaseOrder` function.
If both exist, the `Spec`'s configuration will have priority.


!!! note
    Nested tests will always run in discovery order (sequential).



```kotlin
class SequentialSpec : StringSpec() {

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential

    init {
      "foo" {
        // I run first as I'm defined first
      }

      "bar" {
        // I run second as I'm defined second
      }
    }
}
```

```kotlin
class RandomSpec : StringSpec() {

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Random

    init {
      "foo" {
        // This test may run first or second
      }

      "bar" {
        // This test may run first or second
      }
    }
}
```

```kotlin
class LexicographicSpec : StringSpec() {

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Lexicographic

    init {
      "foo" {
        // I run second as bar < foo
      }

      "bar" {
        // I run first as bar < foo
      }
    }
}
```

