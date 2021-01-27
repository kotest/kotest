---
title: Project Level Config
slug: project-config.html
---




Kotest is flexible and has many ways to configure tests, such as configuring the order of tests inside a spec, or how
test classes are created. Sometimes you may want to set this at a global level and for that you need to use project-level-config.

Project level configuration can be used by creating an object or class that extends from `AbstractProjectConfig`. At runtime,
Kotest will scan for classes that extend this abstract class and instantiate them, reading any configuration defined there.

You can create more than one config class in different modules, and any on the current classpath will be detected and configs merged.
This is effective for allowing common config to be placed into a root module. In the case of clashes, one value will be arbitrarily picked, so it is not recommended adding competing settings to different configs.

:::note
If your project specifies more than one project config, they will be merged, but the resolution of conflicting values is unspecified. It is advised that separate configs do not specify the same settings
:::

Any configuration set at the Spec level or directly on a test will override the config specified at the project level.

Some configuration options available in `ProjectConfig` include parallelism of tests, failing specs with ignored tests, global `AssertSoftly`, and reusable listeners or extensions.





## Parallelism

You can ask Kotest to run specs in parallel to take advantage of modern cpus with several cores by setting the parallelism level (default is 1). Tests inside a spec are always executed sequentially.

To do this, override `parallelism` inside your config and set it to a value higher than 1.
The number set is the number of concurrently executing specs. For example.


```kotlin
object ProjectConfig : AbstractProjectConfig() {
    override val parallelism = 3
}
```

An alternative way to enable this is the system property `kotest.framework.parallelism` which will always (if defined) take priority over the value here.

Some tests may not play nice in parallel, so you can opt out individual specs and force them to be executed in isolation by using the `@DoNotParallelize` annotation on the spec.


:::note
This is only available on the JVM target.
:::






## Assertion Mode

You can ask Kotest to fail the build, or warn in std err, if a test is executed that does not use a Kotest assertion.

To do this, set `assertionMode` to `AssertionMode.Error` or `AssertionMode.Warn` inside your config. For example.
An alternative way to enable this is the system property `kotest.framework.assertion.mode` which will always (if defined) take priority over the value here.


```kotlin
object ProjectConfig : AbstractProjectConfig {
    override val assertionMode = AssertionMode.Error
}
```


:::caution
Assertion mode only works for Kotest assertions and not other assertion libraries. This is because the assertions need to opt-in
to the assertion mode when enabled.
:::



## Global Assert Softly

Assert softly is very useful to batch up errors into a single failure. If we want to enable this for every test automatically, we can do this in a config.
An alternative way to enable this is by setting system property `kotest.framework.assertion.globalassertsoftly` to `true` which will always (if defined) take priority over the value here.

```kotlin
object ProjectConfig : AbstractProjectConfig {
    override val globalAssertSoftly = true
}
```




## Fail On Ignored Tests

You may wish to consider an ignored test as a failure.
To enable this feature, set `failOnIgnoredTests` to true inside your project config. For example.

```kotlin
object ProjectConfig : AbstractProjectConfig {
    override val failOnIgnoredTests = true
}
```




## Test Ordering

When running multiple tests from a Spec, there's a certain order on how to execute them.

By default, a sequential order is used (the order that tests are defined in the spec), but this can be changed. For available options see [test ordering](test_ordering.md).




## Spec Ordering


By default, the ordering of Spec classes is not defined. This is often sufficient, when we have no preference, but if we need control over the execution order of specs, we can use [spec ordering](spec_ordering.md).




## Test name case

The case of the test names can be controlled by changing the value of `testNameCase`.
By default, the value is `TestNameCase.AsIs` which makes no change.

By setting the value to `TestNameCase.Lowercase` a test's name will be lowercase in output.

If you are using a spec that adds in prefixes to the test names (should as WordSpec or BehaviorSpec) then the values `TestNameCase.Sentence` and `TestNameCase.InitialLowercase` can be useful.





## Test name whitespace

If you define test names over several lines then `removeTestNameWhitespace` can be useful. Take this example:

```kotlin
"""this is
   my test case""" {
  // test here
}
```

Then the test name in output will be `this is   my test case`. By setting `removeTestNameWhitespace` to true,
then this name will be trimmed to `this is my test case`.

An alternative way to enable this is by setting system property `kotest.framework.testname.multiline` to `true` which will always (if defined) take priority over the value here.

```kotlin
object ProjectConfig : AbstractProjectConfig {
    override val testNameRemoveWhitespace = true
}
```

