---
id: spec_ordering
title: Spec Ordering
slug: spec-ordering.html
---


By default, the ordering of Spec classes is not defined. This means they are essentially random, in whatever order the discovery mechanism finds them.

This is often sufficient, but if we need control over the execution order of specs, we can do this by specifying the order in [project config](project_config.md).

```kotlin
class MyConfig: AbstractProjectConfig() {
    override val specExecutionOrder = ...
}
```

There are several options.

* `Undefined` - This is the default. The order of specs is undefined and will execute in the order they are discovered at runtime. Eg either from JVM classpath discovery, or the order they appear in javascript files.

* `Lexicographic` - Specs are ordered lexicographically.

* `Random` - Specs are explicitly executed in a random order.

* `Annotated` - Specs are ordered using the `@Order` annotation added at the class level, with lowest values executed first. Any specs without such an annotation are considered "last".
This option only works on the JVM. Any ties will be broken arbitrarily.


### Annotated Example

Given the following specs annoated with @Order.

```kotlin
@Order(1)
class FooTest : FunSpec() { }

@Order(0)
class BarTest: FunSpec() {}

@Order(1)
class FarTest : FunSpec() { }

class BooTest : FunSpec() {}
```

`BarTest` will be executed first, as it has the lowest order value. `FooTest` and `FarTest` will be executed next, as they have the next lowest order values, although their values are both 1 so the order between them is undefined. Finally, `BooTest` will execute last, as it has no annotation.


