---
title: Project Level Config
slug: project-config.html
---

:::warning
This document describes project-level configuration in Kotest 6.0.
If you were using project-level configuration in Kotest 5.x, note that the location of the project config instance must
now be specified, otherwise it will not be picked up by the framework.
:::

Kotest is flexible and has many ways to configure tests, such as configuring the order of tests inside a spec, or how
test classes are created. Sometimes you may want to set this at a global level and for that you need to use
project-level-config.

Project wide configuration can be used by creating a class that extends from `AbstractProjectConfig`.
On the JVM and JS platforms, an object is also supported if you prefer using an object to a class.

Any configuration set at the spec level or directly on a test will override config specified at the project level. Some
configuration options are only available at the project level because they change how the test engine runs the entire
test suite (eg spec concurrency settings).

Some configuration options available in `AbstractProjectConfig` include assertions modes, timeouts, failing specs with
ignored tests, global `AssertSoftly`, and reusable listeners or extensions and so on.

## Setup

On the JVM, Kotest will inspect the classpath for a class with a specified name and package that extends `AbstractProjectConfig`.
By default, this class should be named `io.kotest.provided.ProjectConfig`. If you don't want to place your class in that
particular package, you can specify a different name using the system property `kotest.framework.config.fqn`.

For example, in gradle, you would configure something like this:

```kotlin
tests.task {
  useJunitPlatform()
  systemProperty("kotest.framework.config.fqn", "com.sksamuel.mypackage.WibbleConfig")
}
```

On native and JS platforms, the config class can be located anywhere but must still extend `AbstractProjectConfig`.

:::caution
You should only create a single project config class, otherwise the behavior is undefined.
If you want to have different configurations per package, see [package level config](./package_level_config.md).
:::

## Examples

### Assertion Mode

You can ask Kotest to fail the build, or warn in std err, if a test is executed that does not use a Kotest assertion.

To do this, set `assertionMode` to `AssertionMode.Error` or `AssertionMode.Warn` inside your config. For example.
An alternative way to enable this is the system property `kotest.framework.assertion.mode` which will always (if
defined) take priority over the value here.

```kotlin
object KotestProjectConfig : AbstractProjectConfig() {
  override val assertionMode = AssertionMode.Error
}
```

:::caution
Assertion mode only works for Kotest assertions and not other assertion libraries. This is because the assertions need
to be aware of the assertion detection framework that Kotest provides.
:::

### Global Assert Softly

Assert softly is very useful to batch up errors into a single failure. If we want to enable this for every test
automatically, we can do this in a config.
An alternative way to enable this is by setting system property `kotest.framework.assertion.globalassertsoftly` to
`true` which will always (if defined) take priority over the value here.

```kotlin
object KotestProjectConfig : AbstractProjectConfig() {
  override val globalAssertSoftly = true
}
```

### Timeouts

You can set a default timeout for all tests in your project by setting the `timeout` property in your project config.

```kotlin
object KotestProjectConfig : AbstractProjectConfig() {
  override val timeout = 5.seconds
}
```


### Duplicate Test Name Handling

By default, Kotest will rename a test if it has the same name as another test in the same scope. It will append _1, _2
and so on to the test name. This is useful for automatically generated tests.

You can change this behavior globally by setting `duplicateTestNameMode` to either `DuplicateTestNameMode.Error` or
`DuplicateTestNameMode.Warn`.

`Error` will fail the test suite on a repeated name, and warn will rename but output a warning.

### Fail On Ignored Tests

You may wish to consider an ignored test as a failure.
To enable this feature, set `failOnIgnoredTests` to true inside your project config. For example.

```kotlin
object KotestProjectConfig : AbstractProjectConfig() {
  override val failOnIgnoredTests = true
}
```

### Ordering

Kotest supports ordering both specs and tests independently.

#### Test Ordering

When running multiple tests from a Spec, there's a certain order on how to execute them.

By default, a sequential order is used (the order that tests are defined in the spec), but this can be changed. For
available options see [test ordering](test_ordering.md).

#### Spec Ordering

By default, the ordering of Spec classes is not defined. This is often sufficient, when we have no preference, but if we
need control over the execution order of specs, we can use [spec ordering](spec_ordering.md).

### Test Naming

Test names can be adjusted in several ways.

#### Test Case

Test names case can be controlled by changing the value of `testNameCase`.

By default, the value is `TestNameCase.AsIs` which makes no change.

By setting the value to `TestNameCase.Lowercase` a test's name will be lowercase in output.

If you are using a spec that adds in prefixes to the test names (should as WordSpec or BehaviorSpec) then the
values `TestNameCase.Sentence` and `TestNameCase.InitialLowercase` can be useful.

#### Test Name Tags

Another using test name option is `testNameAppendTags` which, when set to true, will include any applicable tags in the
test name.
For example, if a test `foo` was defined in a spec with the tags `linux` and `spark` then the test name would be
adjusted
to be `foo [linux, spark]`

This setting can also be set using a system property or environment variable `kotest.framework.testname.append.tags` to
`true`.

#### Test name whitespace

If you define test names over several lines then `removeTestNameWhitespace` can be useful. Take this example:

```kotlin
"""this is
   my test case""" {
  // test here
}
```

Then the test name in output will be `this is _ _ _  my test case` (note: the underscores are added for emphasis). By setting `removeTestNameWhitespace` to true,
then this name will be trimmed to `this is my test case`.

An alternative way to enable this is by setting system property `kotest.framework.testname.multiline` to `true` which
will always (if defined) take priority over the value here.

```kotlin
object KotestProjectConfig : AbstractProjectConfig() {
  override val testNameRemoveWhitespace = true
}
```

### Coroutine Dispatcher Factory

You can specify a custom coroutine dispatcher factory to control how coroutines are executed in your tests.

```kotlin
object KotestProjectConfig : AbstractProjectConfig() {
  override val coroutineDispatcherFactory = ThreadPerSpecCoroutineContextFactory
}
```

For more details on this feature, see the [concurrency documentation](concurrency6.html#coroutine-dispatcher-factory).
