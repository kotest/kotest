---
id: gradle
title: Conditional tests with Gradle
slug: conditional-tests-with-gradle.html
sidebar_label: Gradle
---

Kotest supports multiple ways to filter tests from the command line using Gradle.

### Gradle Test Filtering

When running Kotest via the JUnit Platform runner through Gradle, Kotest supports the standard Gradle syntax for
test filtering. You can enable filtering either in the build script or via the `--tests` command-line option.

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

```gradle test --tests 'com.sksamuel.some.package.*'```

```gradle test --tests '*IntegrationTest'```

```gradle test --tests 'com.sksamuel.some.package.MyTestClass.some test name'```

See full Gradle documentation [here](https://docs.gradle.org/6.2.2/userguide/java_testing.html#test_filtering).

:::note
Because Gradle's test support is `class.method` based, when filtering to individual tests, we can specify nested tests
by using the ` -- ` delimiter between test names. For example, `com.mypackage.MySpec.test -- nested test`. Note the
delimiter has a space around the double dashes.
:::

### Kotest Specific Test Filtering

For multiplatform testing, Kotest offers its own flag which is provided via an **environment variable**. This flag
support wildcards via `*` and matches either tests or specs using the same syntax as the Gradle format.

This example would execute all tests in the `com.somepackage` (and nested) packages by setting the
`KOTEST_INCLUDE_PATTERN` environment variable:

```KOTEST_INCLUDE_PATTERN='com.somepackage.*' gradle test```

:::caution
It's best to enclose the value in single quotes rather than double quotes to avoid your shell performing globbing on any
`*` characters.
:::
