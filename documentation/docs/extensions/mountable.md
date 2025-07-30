---
id: mountable
title: Mountable Extensions
sidebar_label: Mountable Extensions
slug: mountable.html
---

## Mountable Extensions

Kotest provides a special type of extension called a `MountableExtension` that can return a materialized value to the user and allows for a configuration block. This allows extensions to return control objects which differ from the extension itself.

### Basic Usage

A `MountableExtension` is installed in a spec using the `install` function, which takes the extension and an optional configuration block:

```kotlin
class MyTest : FunSpec() {
  init {
    val kafka = install(EmbeddedKafka) {
      port = 9092
    }

    // Now you can use the materialized 'kafka' object in your tests
    test("should connect to kafka") {
      kafka.createTopic("test-topic")
      // ...
    }
  }
}
```

In this example, `kafka` is a materialized value that contains details of the host/port of the started Kafka instance, and `EmbeddedKafka` is the extension itself.

### Important Behaviors

#### Installation Always Occurs

The `install` method will always be called even if a spec has no active tests. This means that the extension's `mount` method will be executed regardless of whether any tests in the spec are active or not. This can be useful for setting up resources that might be needed by other specs or for performing initialization that should happen regardless of test execution.

#### Extending Other Extensions

Mountable extensions can extend other extension interfaces such as `BeforeSpecListener`, `AfterSpecListener`, etc. This allows them to hook into the test lifecycle at various points.

For example, a mountable extension can implement `BeforeSpecListener` to perform additional setup before the spec runs:

```kotlin
class MyMountable : MountableExtension<Config, Config>, BeforeSpecListener {

  override suspend fun beforeSpec(spec: Spec) {
    // Perform setup before the spec runs
  }

  override fun mount(configure: Config.() -> Unit): Config {
    val config = Config()
    configure(config)
    return config
  }
}
```

### Lazy Mountable Extensions

Kotest also provides a `LazyMountableExtension` interface for cases where the materialized value needs to be created asynchronously:

```kotlin
class MyLazyMountable : LazyMountableExtension<Config, Resource> {
  override fun mount(configure: Config.() -> Unit): LazyMaterialized<Resource> {
    val config = Config()
    configure(config)
    return object : LazyMaterialized<Resource> {
      override suspend fun get(): Resource {
        // Create and return the resource asynchronously
        return createResource(config)
      }
    }
  }
}
```

When using a `LazyMountableExtension`, the materialized value is wrapped in a `LazyMaterialized` interface, which provides a suspending `get()` function to retrieve the actual value.

### Implementation Details

Both `MountableExtension` and `LazyMountableExtension` are generic interfaces with two type parameters:

- `CONFIG`: The type of the configuration object
- `MATERIALIZED`: The type of the materialized value returned by the extension

The `mount` method is called by the `install` function and is responsible for applying the configuration and returning the materialized value.

Note that the `mount` method cannot be suspending as it is invoked by `install`, which is often used in constructors where suspending functions are not allowed.
