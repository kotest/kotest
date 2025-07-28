---
title: Isolation Modes
slug: isolation-mode.html
---

:::warning
The isolation mode `InstancePerRoot` is only available in Kotest 6.0 and later, and `InstancePerTest` and
`InstancePerLeaf` are now deprecated due to undefined behavior in edge cases.
:::

All specs allow you to control how the test engine creates instances of Specs for test cases. This behavior is called
the _isolation mode_ and is controlled by an enum `IsolationMode`. There are four values: `SingleInstance`,
`InstancePerRoot`, `InstancePerLeaf`, and
`InstancePerTest`. Note that `InstancePerLeaf` and `InstancePerTest` are deprecated in favor of `InstancePerRoot`.

If you want tests to be executed inside fresh instances of the spec - to allow for state shared between tests to be
reset - you can change the isolation mode.

This can be done by using the DSL such as:

```kotlin
class MyTestClass : WordSpec({
  isolationMode = IsolationMode.SingleInstance
  // tests here
})
```

Or if you prefer function overrides, you can override `fun isolationMode(): IsolationMode`:

```kotlin
class MyTestClass : WordSpec() {
  override fun isolationMode() = IsolationMode.SingleInstance

  init {
    // tests here
  }
}
```

:::tip
The default in Kotest is Single Instance which is the same as ScalaTest (the inspiration for this framework), Jest,
Jasmine, and other Javascript frameworks, but different to JUnit.
:::

## Single Instance

The default isolation mode is `SingleInstance` whereby one instance of the Spec class is created and then each test case
is executed in turn until all tests have completed.

For example, in the following spec, the same id would be printed four times as the same instance is used for all tests.

```kotlin
class SingleInstanceExample : WordSpec({
  val id = UUID.randomUUID()
  "a" should {
    println(id)
    "b" {
      println(id)
    }
    "c" {
      println(id)
    }
  }
  "d" should {
    println(id)
  }
})
```

## InstancePerRoot

The `InstancePerRoot` isolation mode creates a new instance of the Spec class for every top level (root) test case. Each
root test is executed in its own associated instance.

This mode is recommended when you want to isolate your tests but still maintain a clean structure.

```kotlin
class InstancePerRootExample : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerRoot

  val id = UUID.randomUUID()

  init {
    "a" should {
      println(id)
      "b" {
        println(id)
      }
      "c" {
        println(id)
      }
    }

    "d" should {
      println(id)
    }
  }
}
```

In this example, the tests a, b and c will all print the same UUID, but test d will print a different UUID because it is
executed in a new instance as it is a top level (aka root) test case.

## InstancePerTest

::::warning
This mode is deprecated due to undefined behavior on edge cases. It is recommended to use `InstancePerRoot` instead.
::::

The next mode is `IsolationMode.InstancePerTest` where a new spec will be created for every test case, including inner
contexts.
In other words, outer contexts will execute as a "stand alone" test in their own instance of the spec.
An example should make this clear.

```kotlin
class InstancePerTestExample : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  init {
    "a" should {
      println("Hello")
      "b" {
        println("From")
      }
      "c" {
        println("Sam")
      }
    }
  }
}
```

Do you see how we've overridden the `isolationMode` function here.

When this is executed, the following will be printed:

```
Hello
Hello
From
Hello
Sam
```

This is because the outer context (test "a") will be executed first. Then it will be executed again for test "b", and
then again for test "c".
Each time in a clean instance of the Spec class. This is very useful when we want to re-use variables.

Another example will show how the variables are reset.

```kotlin
class InstancePerTestExample : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  val counter = AtomicInteger(0)

  init {
    "a" should {
      println("a=" + counter.getAndIncrement())
      "b" {
        println("b=" + counter.getAndIncrement())
      }
      "c" {
        println("c=" + counter.getAndIncrement())
      }
    }
  }
}
```

This time, the output will be:

a=0
a=0
b=1
a=0
c=1

## InstancePerLeaf

::::warning
This mode is deprecated due to undefined behavior on edge cases. It is recommended to use `InstancePerRoot` instead.
::::

The next mode is `IsolationMode.InstancePerLeaf` where a new spec will be created for every leaf test case - so
excluding inner contexts.
In other words, inner contexts are only executed as part of the "path" to an outer test.
An example should make this clear.

```kotlin
class InstancePerLeafExample : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

  init {
    "a" should {
      println("Hello")
      "b" {
        println("From")
      }
      "c" {
        println("Sam")
      }
    }
  }
}
```

When this is executed, the following will be printed:

```
Hello
From
Hello
Sam
```

This is because the outer context - test "a" - will be executed first, followed by test "b" in the same instance.
Then a new spec will be created, and test "a" again executed, followed by test "c".

Another example will show how the variables are reset.

```kotlin
class InstancePerLeafExample : WordSpec() {

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

  val counter = AtomicInteger(0)

  init {
    "a" should {
      println("a=" + counter.getAndIncrement())
      "b" {
        println("b=" + counter.getAndIncrement())
      }
      "c" {
        println("c=" + counter.getAndIncrement())
      }
    }
  }
}
```

This time, the output will be:

a=0
b=1
a=0
c=1

## Global Isolation Mode

Rather than setting the isolation mode in every spec, we can set it globally in project config or via a system property.


### Config

See the docs on setting up [project wide config](project_config.md), and then add the isolation mode you want to be the
default. For example:

```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override val isolationMode = IsolationMode.InstancePerRoot
}
```

:::note
Setting an isolation mode in a Spec will always override the project wide setting.
:::


### System Property

To set the global isolation mode at the command line, use the system property `kotest.framework.isolation.mode` with one
of the values:

* SingleInstance
* InstancePerRoot
* InstancePerTest (deprecated)
* InstancePerLeaf (deprecated)

:::note
The values are case sensitive.
:::
