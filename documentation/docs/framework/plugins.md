---
id: plugins
title: Plugins
slug: plugins.html
---

Sometimes there's a need for special integration with some tools that are more complex
In this situation plugins are necessary.


## Pitest

The Mutation Testing tool [Pitest](https://pitest.org/) is integrated via a kotest plugin. After [configuring](https://gradle-pitest-plugin.solidsoft.info/) the Pitest extension, add the `kotest-plugins-pitest` module to your dependencies as well:

```kotlin
    testImplementation("io.kotest:kotest-plugins-pitest:<version>")
```

After doing that, tell Pitest that we're going to use `Kotest` as a `testPlugin`:

```kotlin
// Assuming that you have already configured the Gradle/Maven extension
configure<PitestPluginExtension> {
    testPlugin.set("Kotest")    // <-- Telling Pitest that we're using Kotest
    targetClasses.set(listOf("my.company.package.*"))
}
```

This should set everything up, and running `./gradlew pitest` will generate reports in the way you configured.
