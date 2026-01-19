---
id: annotations
title: Conditional tests with annotations
slug: spec-annotations-conditional-evaluation.html
sidebar_label: Spec Annotations
---


If we wish to completely disable a Spec and all tests included in the spec, we can do this using annotations.

An advantage to this approach, instead of disabling each test one by one, is that the spec will not be instantiated.
If a spec has expensive resource setup/teardown, then that time can be avoided by this approach.


:::note
These annotations are only available for the JVM target.
:::


### @Ignored

If we wish to simply disable a spec completely, then we can use the `@Ignored` annotation.

```kotlin
@Ignored
class IgnoredSpec : FunSpec() {
  init {
    error("boom") // spec will not be created so this error will not happen
  }
}
```

### @EnabledIf

If we want to disable a spec dependent on the execution of a function, then we can use `@EnabledIf`.

This annotation accepts a class that implements `EnabledCondition`, and that class is instantiated and invoked
to determine if a spec is enabled. Note that implementations must have a zero args constructor.

For example, we may wish to only execute tests containing the name "Linux" when run on a Linux machine.

```kotlin
class LinuxOnlyCondition : Condition {
  override fun evaluate(kclass: KClass<out Spec>): Boolean = when {
    kclass.simpleName?.contains("Linux") == true -> IS_OS_LINUX
    else -> true // non Linux tests always run
  }
}
```

Then we can apply this condition to one or more specs:

```kotlin
@EnabledIf(LinuxOnlyCondition::class)
class MyLinuxTest1 : FunSpec() {
  // tests here
}

@EnabledIf(LinuxOnlyCondition::class)
class MyLinuxTest2 : DescribeSpec() {
  // tests here
}

@EnabledIf(LinuxOnlyCondition::class)
class MyWindowsTests : DescribeSpec() {
  // tests here
}
```
