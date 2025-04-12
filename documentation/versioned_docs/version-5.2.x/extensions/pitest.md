---
id: pitest
title: Pitest
sidebar_label: Pitest
slug: pitest.html
---


The Mutation Testing tool [Pitest](https://pitest.org/) is integrated with Kotest via an extension module.

After [configuring](https://gradle-pitest-plugin.solidsoft.info/) Pitest,
add the `io.kotest.extensions:kotest-extensions-pitest` module to your dependencies as well:

```kotlin
    testImplementation("io.kotest.extensions:kotest-extensions-pitest:<version>")
```

Note: Since pitest is an extension, we use a different maven group name (io.kotest.extensions) from the core modules.

After doing that, we need to inform Pitest that we're going to use `Kotest` as a `testPlugin`:

```kotlin
// Assuming that you have already configured the Gradle/Maven extension
configure<PitestPluginExtension> {
    // testPlugin.set("Kotest")    // needed only with old PIT <1.6.7, otherwise having kotest-extensions-pitest on classpath is enough
    targetClasses.set(listOf("my.company.package.*"))
}
```

This should set everything up, and running `./gradlew pitest` will generate reports in the way you configured.
