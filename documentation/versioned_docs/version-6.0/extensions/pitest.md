---
id: pitest
title: Pitest
sidebar_label: Pitest
slug: pitest.html
---


The Mutation Testing tool [Pitest](https://pitest.org/) is integrated with Kotest via an extension module.

## Gradle configuration
[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-pitest.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotest-extensions-pitest)

After [configuring](https://gradle-pitest-plugin.solidsoft.info/) Pitest,
add the `io.kotest.extensions:kotest-extensions-pitest` module to your dependencies as well:

```kotlin
    testImplementation("io.kotest:kotest-extensions-pitest:<version>")
```

:::note
Since Kotest 6.0, all extensions are published under the `io.kotest` group once again, with version cadence tied to
main Kotest releases.
:::


After doing that, we need to inform Pitest that we're going to use `Kotest` as a `testPlugin`:

```kotlin
// Assuming that you have already configured the Gradle/Maven extension
configure<PitestPluginExtension> {
    // testPlugin.set("Kotest")    // needed only with old PIT <1.6.7, otherwise having kotest-extensions-pitest on classpath is enough
    targetClasses.set(listOf("my.company.package.*"))
}
```

This should set everything up, and running `./gradlew pitest` will generate reports in the way you configured.

## Maven configuration
[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-pitest.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotest-extensions-pitest)

First of all, you need to configure the [Maven Pitest plugin](https://pitest.org/quickstart/maven/):

```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>${pitest-maven.version}</version>
    <configuration>
        <targetClasses>...</targetClasses>
        <coverageThreshold>...</coverageThreshold>
        ... other configurations as needed
    </configuration>
</plugin>
```

Then add the dependency on Pitest Kotest extension:

```xml
<dependencies>
  ... the other Kotest dependencies like kotest-runner-junit5
  <dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-extensions-pitest</artifactId>
    <version>${kotest-extensions-pitest.version}</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

This should be enough to be able to run Pitest and get the reports as described in the [Maven Pitest plugin](https://pitest.org/quickstart/maven/).
