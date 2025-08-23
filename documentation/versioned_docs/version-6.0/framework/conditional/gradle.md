---
id: gradle
title: Conditional tests with Gradle
slug: conditional-tests-with-gradle.html
sidebar_label: Gradle
---

Kotest supports two ways to filter tests from the command line using Gradle. The first is the standard --tests flag
that gradle supports, and the second is a kotest specific system property.


### Gradle Test Filtering

When running Kotest via the JUnit Platform runner through gradle, Kotest supports the standard gradle syntax for
test filtering. You can enable filtering either in the build script or via the --tests command-line option.

For example, in the build script:

```groovy
tasks.test {
    filter {
        //include all tests from package
        includeTestsMatching("com.somepackage.*")
    }
}
```

Or via the command line:

```gradle test --tests 'com.sksamuel.somepackage*'```

```gradle test --tests '*IntegrationTest'```

See full Gradle documentation [here](https://docs.gradle.org/6.2.2/userguide/java_testing.html#test_filtering).

:::caution
Because gradle's test support is class.method based, we cannot filter tests down to the individual test level, only the class level.
:::


### Kotest Specific Test Filtering

To avoid the limitations with Gradle's `--tests` support, Kotest offers its own flags which are provided via **system properties** or **environment variables**.
These flags support wildcards via `*` and match either tests or specs:

| System property (JVM)  | Environment variable (JVM or Native) | Scope              |
|------------------------|--------------------------------------|--------------------|
| `kotest.filter.specs`  | `kotest_filter_specs`                | Spec (class) names |
| `kotest.filter.tests`  | `kotest_filter_tests`                | Test names         |

:::caution
System properties are only supported when targeting Kotlin/JVM.
Environment variables are only supported when targeting Kotlin/JVM and Kotlin/Native.
:::

This example would execute all tests in the com.somepackage (and nested) packages by setting the `kotest.filter.specs` system property:

```systemProperty("kotest.filter.specs", "com.somepackage*")```

This example would do the same, but uses the environment variable and so works for both Kotlin/JVM and Kotlin/Native:

```kotest_filter_specs='com.somepackage.*' gradle test```

:::caution
Regardless of whether you use a system property or an environment variable, it's best to enclose the value in single quotes
rather than double quotes to avoid your shell performing globbing on any `*` characters.
:::

This example would execute only tests that contain `Foo` in the com.somepackage (and nested) packages:

```systemProperty("kotest.filter.specs", "com.somepackage.*")```
```systemProperty("kotest.filter.tests", "*Foo*")```

This example would execute only tests that start with `Foo` in any package:

```systemProperty("kotest.filter.tests", "Foo*")```

:::note
Passing system properties to the main Gradle task usually does not work with tests.
Gradle forks another JVM for the test runner and fails to pass system properties.
Therefore, you need to use the `systemProperty(...)` syntax in the test task of the `build.gradle` file.
:::
