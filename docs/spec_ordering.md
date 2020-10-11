Spec Ordering
=====


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
