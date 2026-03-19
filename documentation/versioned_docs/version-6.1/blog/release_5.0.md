# Release 5.0

_**This document is a work in progress and will be finalized within a few days of the 5.0 release.**_

- [Breaking Changes](#breaking-changes)
- [Deprecations](#deprecations)


## Breaking Changes

#### Kotlin 1.6 is now the minimum supported version

From version 5 onwards, Kotest requires Kotlin 1.6. The decision to do this is twofold.

Firstly, the main feature in the 5.0
release train is support for multiplatform tests and there are incompatibilities in the compiler between Kotlin 1.5 and 1.6. To support both
would add needless complexity to the build.

Secondly, `kotlin.time.Duration`'s have finally gone stable as of 1.6 and Kotest
builds on these internally. We wanted to be able to depend on the multiplatform functionality provided by Kotlin durations without
any issues arising from changes in these classes from previous versions.

#### Legacy Javascript compiler support dropped

Javascript support for the _legacy_ compiler is no longer supported. If you are running tests on JS legacy then you will need to continue using Kotest 4.6.x or set your Javascript test build to use only use the IR compiler.

Test support for the legacy Javascript compiler relied on functionality that has been removed in the IR compiler (Namely, that the
[framework adapter](https://kotlinlang.org/api/latest/kotlin.test/kotlin.test/-framework-adapter/) no longer works with third
party modules). For the 5.0 release, Kotest provides a compiler plugin which integrates tests directly into the compiled output exactly
how the kotlin.test support works.

#### Global configuration object dropped

In previous versions Kotest supported configuration updates via a global configuration object called `configuration` (and called `project` in even earlier versions).
From this release onwards, this top level val has been removed.

The reason for the removal is that having global state complicated using multiple instances of the Test Engine in the same JVM and
also because there was not precise semanatics around the orders of updates to a top level val.

The former was mainly an issue when testing Kotest itself, since users don't typically create instances of `TestEngine`
directly but instead run tests via gradle or intellij.

This top level val was not included in documentation so users should have been largely unaware it existed anyway. The
recommended approach to defining Kotest configuration remains
either [ProjectConfig](https://kotest.io/docs/framework/project-config.html)
or [system properties](https://kotest.io/docs/framework/framework-config-props.html).

#### Experimental data testing classes moved

The experimental data-test `withData` functions added in 4.5 under the package name `io.kotest.datatest` have moved to a new module `kotest-framework-datatest`.

Note: These are separate from the `forAll` and `forNone` data test functions which have been part of Kotest since version 2.0.

#### Deprecated property test Arb.value removed

The `Arb.values()` method has been removed from the `Arb` interface. This method was deprecated in 4.3 in favour of using `Arb.sample` which was introduced to allow for Arb flat-mapping. This will only affect anyone who has written a custom arb that extends `Arb` directly and is still using the deprecated method. Any existing uses of the `arbitrary` builders is unaffected and those builders are always the preferred way to create custom arbitraries.

#### Startup configuration dump off by default

The Engine no longer logs config to the console during start **by default**. To enable output, set the system property or env var `kotest.framework.dump.config` to true.

#### Deprecated method removals

* `shouldReceiveWithin` and `shouldReceiveNoElementsWithin` channel matchers have been removed.
* The deprecated `RuntimeTagExtension` has been undeprecated but moved to a new package.
* `RuntimeTagExpressionExtension` has moved to a new package.

#### Inspector changes.

When using inspectors, the deprecated `kotlintest.assertions.output.max` system property has been removed.
This was replaced with `kotest.assertions.output.max` in release 4.0 when the project was renamed
from KotlinTest to Kotest.

## Deprecations

#### Test Status

The `TestStatus` enum has been deprecated in favour of the `TestResult` ADT. Instead of matching on`result.status` in `AfterTestListener` you should now match directly on the result. Eg

```kotlin
when (result) {
  is TestResult.Success -> ...
  is TestResult.Error -> ...
}
```

#### SpecExtension.intercept(KClass) method deprecation

The `intercept(KClass)` method in SpecExtension has been deprecated and `SpecRefExtension` has been added. The deprecated method had ambigious behavior when used with an `IsolationMode` that created multiple instances of a spec. The new methods have precise guarantees of when they will execute.

* `SpecRefExtension`s will execute once per class.
* `SpecExtension`s will execute once per instance.

### Default test case config

The `defaultTestCaseConfig` containers in Spec's and project configuration have been deprecated. This is because it was not possible to specify at both the spec level and the project-configuration level and allow settings to fall through.

Instead, you should set per-setting defaults, and these will fall through from test -> spec -> configuration.

For example, instead of this:

```kotlin
class MySpec: FunSpec() {
  init {
    override fun defaultTestCaseConfig() = TestCaseConfig(tags = setOf(Foo, Bar), timeout = 100.seconds)
    test("foo") {
      // will time out after 100 seconds and has tags Foo and Bar applied
    }
  }
}
```

You should do:

```kotlin
class MySpec: FunSpec() {
  init {

    tags(Foo, Bar)
    timeout = 100.seconds

    test("foo") {
       // will time out after 100 seconds and has tags Foo and Bar applied
    }
  }
}
```

Note that the second variation has always been possible, but the first variation is no longer recommended.


#### Listener names

The `val name` inside `Listener` has been deprecated. This was used so that errors from multiple before/after spec callbacks could appear with customized unique names. The framework now takes ensures that names are unique so this val is no longer needed and is now ignored.

#### Other deprecations

* `SpecInstantiationListener` has been deprecated in favour of `InstantiationListener` and `InstantiationErrorListener`,
  both of which support suspension functions. `SpecInstantiationListener` is a hold-over from before coroutines existed
  in Kotlin and so had no support for coroutines.
* `SpecIgnoredListner` (note the typo) has been renamed to `IgnoredSpecListener`.
* The `listeners` method to add listeners to a Spec has been deprecated. When adding listeners to specs directly, you
  should now prefer `extensions()` rather than `listeners()`.
* `CompareMode` /`CompareOrder` for `shouldEqualJson` has been deprecated in favor of `compareJsonOptions { }`
