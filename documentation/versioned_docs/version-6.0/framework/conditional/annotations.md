---
id: annotations
title: Conditional tests with annotations
slug: spec-annotations-conditional-evaluation.html
sidebar_label: Spec Annotations
---


If we wish to completely disable a Spec and all tests included in the spec, we can do this using annotations.

An advantage to this approach, instead of disabling each test one by one, is that the spec will not be instantiated.
If a spec has expensive resource setup/teardown, then that time can be avoided by this approach.


:::note[JVM Only]
These annotations are only available for specs executing on the JVM.
:::


## @Ignored

If we wish to simply disable a spec completely, then we can use the `@Ignored` annotation.

```kotlin
@Ignored
class IgnoredSpec : FunSpec() {
  init {
    error("boom") // spec will not be created, so this error will not happen
  }
}
```

## @EnabledIf

If we want to enable a spec dependent on the execution of a function, then we can use `@EnabledIf`.

This annotation accepts a class that implements `Condition`, and that class is instantiated and invoked
to determine if a spec is enabled. Note that implementations must have a zero args constructor.

For example, we may wish to only execute a spec when running on a Linux machine.
We will use the system property `os.name` to determine if we are running on Linux.

```kotlin
class LinuxOnlyCondition : Condition {
  override fun evaluate(kclass: KClass<out Spec>): Boolean {
    val os = System.getProperty("os.name") ?: return false
    return os.lowercase().contains("linux")
  }
}
```

Then we can apply this condition to one or more specs:

```kotlin
@EnabledIf(LinuxOnlyCondition::class)
class MyLinuxTests : FunSpec() {
  // tests here
}

@EnabledIf(LinuxOnlyCondition::class)
class MyWindowsTests : DescribeSpec() {
  // tests here
}
```

## @DisabledIf

The opposite of `@EnabledIf`. Any spec annotated with `@DisabledIf` will be disabled if the condition
applied to that annotation evaluates to `true`.

For example, we may wish to disable a particular spec when running on a CI server.
We will use the presence of an env variable `CI` to determine if we are running on a CI server.

```kotlin
class CIServerCondition : Condition {
  override fun evaluate(kclass: KClass<out Spec>): Boolean = System.getenv("CI") != null
}
```

Then we can apply this condition to one or more specs:

```kotlin
@DisabledIf(CIServerCondition::class)
class SkipOnCiSpec : FunSpec() {
  // tests here
}
```
