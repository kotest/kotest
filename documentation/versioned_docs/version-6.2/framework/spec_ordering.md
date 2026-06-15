---
id: spec_ordering
title: Spec Ordering
slug: spec-ordering.html
---


By default, the ordering of Spec classes is not defined. This means they are essentially random, in whatever order the
discovery mechanism finds them.

This is usually fine as the order is perhaps not important to most test suites, but if you require control over the
execution order of specs, we can do this by specifying the order in [project config](project_config.md).

```kotlin
class MyConfig : AbstractProjectConfig() {
  override val specExecutionOrder = ...
}
```

There are several options.

* `Undefined` - This is the default. The order of specs is undefined and will execute in the order they are discovered
  at runtime. Eg either from the JVM classpath or the order they appear in JavaScript files.

* `Lexicographic` - Specs are ordered lexicographically.

* `Random` - Specs are executed in a random order.

* `Annotated` - Specs are ordered using the `@Order` annotation added at the class level, with the lowest values
  executed first. Any specs without such an annotation are considered "last" (Max integer).
  This option only works on the JVM. Specs with the same order value are executed in the order they are discovered.

### Annotated Example

Given the following specs annotated with @Order.

```kotlin
@Order(1)
class FooTest : FunSpec() {}

@Order(0)
class BarTest : FunSpec() {}

@Order(1)
class BazTest : FunSpec() {}

class WazTest : FunSpec() {}
```

`BarTest` will be executed first, as it has the lowest order value. `FooTest` and `BazTest` will be executed next, as
they have the next lowest order values, although their values are both 1 so the order between them is undefined.
Finally, `WazTest` will execute last, as it has no annotation.

### Random Seed

When using the `Random` spec execution order, you can set a seed to ensure that the same order is always used if required.

```kotlin
class MyConfig : AbstractProjectConfig() {
  override val randomOrderSeed = ...
}
```

### Custom Ordering

You can also order specs yourself by implementing the `SpecExecutionOrderExtension` interface and registering it with
the project config. If such an extension is registered, the `specExecutionOrder` property will be ignored and the
extension will be used instead.

```kotlin
class MyConfig : AbstractProjectConfig() {
  override val extensions = listOf(MySpecExecutionOrderExtension())
}
```
