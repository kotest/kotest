## KotlinTest Plugins

Sometimes there's a need for special integration with some tools. These integrations are available at the `kotlintest-plugins` modules and they can be whatever it's necessary to integrate with a specific tool.

Sometimes this is available with less complex integrations, such as using [Listeners](reference.md#listeners) or [Extensions](reference.md#extensions), but in some cases this isn't possible, and thus plugins are necessary.


### Pitest

The Mutation Testing tool [Pitest](https://pitest.org/) is integrated via plugin with KotlinTest. After [configuring](https://gradle-pitest-plugin.solidsoft.info/) the Pitest extension, add the KotlinTest Plugin dependency to your dependencies as well:

```kotlin
    testImplementation("io.kotlintest:kotlintest-plugins-pitest:{kotlintestVersion}")
```

After doing that, tell Pitest that we're going to use the `KotlinTest` as a `testPlugin`:

```kotlin
// Assuming that you have already configured the Gradle/Maven extension
configure<PitestPluginExtension> {

    testPlugin.set("KotlinTest")    // <-- Telling Pitest that we're using KotlinTest
    
    
    targetClasses.set(listOf("my.company.package.*"))
}
```

This should set everything up, and running `./gradlew pitest` will generate reports in the way you configured.
