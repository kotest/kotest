---
id: extensions_introduction
title: Introduction to Extensions
slug: extensions-introduction.html
sidebar_label: Introduction
---

Extensions are reusable [lifecycle hooks](../lifecycle_hooks.md). In fact, lifecycle hooks are themselves represented internally as instances
of extensions. In the past, Kotest used the term _listener_ for simple interfaces and _extension_ for more advanced interfaces, however
there is no distinction between the two and the terms can be used interchangeably.

## Creating an Extension

The basic usage is to create an implementation of the required extension interface and register it with a test,
a spec, or project wide in [ProjectConfig](../project_config.md).

For example, here we define a `BeforeSpecListener` and `AfterSpecListener`, and register it with a spec.

```kotlin
class MyTestListener : BeforeSpecListener, AfterSpecListener {
   override suspend fun beforeSpec(spec:Spec) {
      // power up kafka
   }
   override suspend fun afterSpec(spec: Spec) {
      // shutdown kafka
   }
}


class TestSpec : FreeSpec({
   extension(MyTestListener())
   // tests here
})
```

Any extensions registered inside a `Spec` will be used for all tests in that spec (including [test factories](../test_factories.md) and nested tests).

To run an extension for every spec in the entire project, you can register the listener via [project config](../project_config.md).
Here is an example of the previous `MyTestListener` registered at the project level so it wuld apply to all specs.

```kotlin
object ProjectConfig : AbstractProjectConfig {
   override val extensions = listOf(MyTestListener())
}
```

:::caution
Some extensions can only be registered at the project level. For example, registering a `BeforeProjectListener` inside a
spec will have no effect, since the project has already started by the time that extension would be encountered.
:::



