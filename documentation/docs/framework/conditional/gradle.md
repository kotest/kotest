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

To avoid the limitations with Gradle's `--tests` support, Kotest offers its own flags, `kotest.filter.tests` and `kotest.filter.specs`
which are provided via **system properties** or **env vars**. These flags support wildcards via `*` and match either tests or specs.

This example would execute all tests in the com.somepackage (and nested) packages:

```gradle test -Dkotest.filter.specs='com.somepackage.*'```

This example would execute only tests that contain `Foo` in the com.somepackage (and nested) packages:

```gradle test -Dkotest.filter.specs='com.somepackage.*' -Dkotest.filter.tests='*Foo*'```

This example would execute only tests that start with `Foo` in any package:

```gradle test -Dkotest.filter.tests='Foo*'```
