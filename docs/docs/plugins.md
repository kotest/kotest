## Kotest Plugins

Sometimes there's a need for special integration with some tools. These integrations are available at the `Kotest-plugins` modules and they can be whatever it's necessary to integrate with a specific tool.

Sometimes this is available with less complex integrations, such as using [Listeners](reference.md#listeners) or [Extensions](reference.md#extensions), but in some cases this isn't possible, and thus plugins are necessary.


### Pitest

The Mutation Testing tool [Pitest](https://pitest.org/) is integrated via plugin with Kotest. After [configuring](https://gradle-pitest-plugin.solidsoft.info/) the Pitest extension, add the Kotest Plugin dependency to your dependencies as well:

```kotlin
    testImplementation("io.kotest:kotest-plugins-pitest:<version>")
```

After doing that, tell Pitest that we're going to use the `Kotest` as a `testPlugin`:

```kotlin
// Assuming that you have already configured the Gradle/Maven extension
configure<PitestPluginExtension> {

    testPlugin.set("Kotest")    // <-- Telling Pitest that we're using Kotest


    targetClasses.set(listOf("my.company.package.*"))
}
```

This should set everything up, and running `./gradlew pitest` will generate reports in the way you configured.
