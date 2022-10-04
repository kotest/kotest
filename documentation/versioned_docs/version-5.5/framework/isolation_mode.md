---
title: Isolation Modes
slug: isolation-mode.html
---



All specs allow you to control how the test engine creates instances of Specs for test cases. This behavior is called the _isolation mode_ and is controlled
by an enum `IsolationMode`. There are three values: `SingleInstance`, `InstancePerLeaf`, and `InstancePerTest`.

If you want tests to be executed inside fresh instances of the spec - to allow for state shared between tests to be reset -
you can change the isolation mode.

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


:::warning
The default in Kotest is Single Instance which is the same as ScalaTest (the inspiration for this framework), Jest, Jasmine, and other Javascript frameworks, but different to JUnit.
:::



## Single Instance

The default isolation mode is `SingleInstance` whereby one instance of the Spec class is created and then each test case
is executed in turn until all tests have completed.

For example, in the following spec, the same id would be printed three times as the same instance is used for all tests.

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
})
```



## InstancePerTest

The next mode is `IsolationMode.InstancePerTest` where a new spec will be created for every test case, including inner contexts.
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

This is because the outer context (test "a") will be executed first. Then it will be executed again for test "b", and then again for test "c".
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

The next mode is `IsolationMode.InstancePerLeaf` where a new spec will be created for every leaf test case - so excluding inner contexts.
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

### System Property

To set the global isolation mode at the command line, use the system property `kotest.framework.isolation.mode` with one of the values:

* InstancePerTest
* InstancePerLeaf
* SingleInstance

:::note
The values are case sensitive.
:::

### Config

See the docs on setting up [project wide config](project_config.md), and then add the isolation mode you want to be the default. For example:

```kotlin
class ProjectConfig: AbstractProjectConfig() {
   override val isolationMode = IsolationMode.InstancePerLeaf
}
```

:::note
Setting an isolation mode in a Spec will always override the project wide setting.
:::

