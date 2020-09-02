Project Level Config
=============

Kotest is flexible and has many ways to configure tests, such as configuring the order of tests inside a spec, or how
test classes are created. Sometimes you may want to set this at a global level and for that you need to use project-level-config.

Project level configuration is used by creating an object or class that extends from `AbstractProjectConfig`. At runtime,
Kotest will scan for classes that extend this and instantiate them and read any configuration defined there.

Any configuration set at the Spec level (or directly on a TestCase) will override the config specified at the project level.

Some of the configuration options available in `ProjectConfig` include parallelism of tests, failing specs with ignored tests, global `AssertSoftly`, and reusable listeners or extensions.

#### Parallelism

You can ask Kotest to run specs in parallel to take advantage of modern cpus with several cores by setting the parallelism level (default is 1).. Tests inside a spec are always executed sequentially.

To do this, override `parallelism` inside your config and set it to a value higher than 1.
The number set is the number of concurrently executing specs. For example.


```kotlin
object ProjectConfig : AbstractProjectConfig() {
    override val parallelism = 3
}
```

An alternative way to enable this is the system property kotest.parallelism which will always (if defined) take priority over the value here.

Some tests may not play nice in parallel, so you can opt out individual specs and force them to be executed in isolation by using the `@DoNotParallelize` annotation on the spec.

_Note: Parallelism is only supported on the JVM._

#### Assertion Mode

You can ask Kotest to fail the build, or warn in std err, if a test is executed that does not use a Kotest assertion (other assertion libraries are not detected).

To do this, set `assertionMode` to `AssertionMode.Error` or `AssertionMode.Warn` inside your config. For example.

```kotlin
object ProjectConfig : AbstractProjectConfig {
    override val assertionMode = AssertionMode.Error
}
```

#### Fail On Ignored Tests

You may wish to consider an ignored test as a failure. To enable this feature, set `failOnIgnoredTests` to true inside your project config. For example.

```kotlin
object ProjectConfig : AbstractProjectConfig {
    override val failOnIgnoredTests = true
}
```

#### Test Ordering

When running multiple tests from a Spec, there's a certain order on how to execute them.

By default, a sequential order is used (the order that tests are defined in the spec), but it's also possible to configure them to be executed in a random or lexicographic order.

##### Sequential

Runs top level tests in the order they are defined in code.

```kotlin
object ProjectConfig : AbstractProjectConfig() {
    override val testCaseOrder = TestCaseOrder.Sequential
}
```

##### Random

Runs top level tests in a random order every time.

```kotlin
object ProjectConfig : AbstractProjectConfig() {
    override val testCaseOrder = TestCaseOrder.Random
}
```

##### Lexicographic

Runs top level tests in in lexicographic (~alphabetical) order.

```kotlin
object ProjectConfig : AbstractProjectConfig() {
    override val testCaseOrder = TestCaseOrder.Lexicographic
}
```

_Note: Nested tests will always run in discovery order (sequential)_
