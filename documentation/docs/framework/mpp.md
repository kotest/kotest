---
id: multiplatform
title: Multiplatform Testing
slug: multiplatform.html
---


_Experimental Feature_

Kotest 5.0 supports full multiplatform testing - JVM, JS and native.
This page describes how to use Kotest with Kotlin multiplatform.

This is a beta feature first released in 5.0.0.M1

Note: Only IR mode is supported for Javascript - Legacy is discontinued.

### Gradle Plugin

Add the [Kotest multiplatform gradle plugin](https://plugins.gradle.org/plugin/io.kotest.multiplatform) to your build.

For example:

```kotlin
plugins {
   kotlin("io.kotest.multiplatform").version("5.0.0.5")
}
```

### Engine Dependency

Add the Kotest engine module to your build - either as a common dependency or as a platform specific dependency.

For example, either:

```kotlin
val commonTest by getting {
   dependencies {
      implementation("io.kotest:kotest-framework-engine:5.0.0.M1")
   }
}
```

Or

```kotlin
val jsTest by getting {
   dependencies {
      implementation("io.kotest:kotest-framework-engine-js:5.0.0.M1")
   }
}
```

Or

```kotlin
val linuxX64Test by getting {
   dependencies {
      implementation("io.kotest:kotest-framework-engine-linuxx64:5.0.0.M1")
   }
}
```

Note: The JUnit runner dependency (`io.kotest:kotest-runner-junit5-jvm`) is still required for JVM tests as on the JVM
the tests run via JUnit platform.

### Executing tests

When using multiplatform you must use the `check` task and not the `test` task.

`./gradlew check`


### Caveats

* The Javascript and Native test engines are more feature limited than the JVM test engine. For example, concurrent tests and annotation based configuration. Kotlin doesn't expose annotations to non-reflection targets.


* Legacy support for JS (present in 4.x) has been dropped and only IR is supported moving forward. This is because in
  5.0 the MPP support works via compiler plugins, which are different between IR and legacy, and we will not support a
  deprecated backend (legacy) in new code.


* Tests for Javascript can only use the `FunSpec`, `ShouldSpec` and `StringSpec` spec styles. This is
  somewhat ironic since `DescribeSpec` is in fact inspired by Javascript testing frameworks.

  The reason is that JS testing libraries (which are used by Kotlin/JS under the hood) do not support promises in "parent" tests, but Kotest does (suspend). So
  either the DSL would need to have a breaking change so that parent tests are not suspendable (a breaking change and
  undesirable for writing JVM tests) or we just disallow those spec styles in JS. You will see a warning in output if
  you use them for JS tests.


* The IntelliJ plugin will not allow you to run common, native or JS tests from the IDE. You will need to use
  the `gradle check` task.
