---
id: test_containers_46
title: Testcontainers
sidebar_label: Testcontainers
slug: test_containers_46.html
---



## Testcontainers

The [Testcontainers](https://github.com/testcontainers/testcontainers-java) project provides lightweight, ephemeral instances of common databases, elasticsearch, kafka, Selenium web browsers, or anything else that can run in a Docker container, ideal for use inside tests.

This module provides integration for using Testcontainers with kotest.
To use add the below dependency to your Gradle build file.

```groovy
io.kotest.extensions:kotest-extensions-testcontainers:${kotest.version}
```

[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-testcontainers.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-testcontainers)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-testcontainers.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-testcontainers/)

Note: The group id is different (io.kotest.extensions) from the main kotest dependencies (io.kotest).

For Maven, you will need these dependencies:

```xml
<dependency>
    <groupId>io.kotest.extensions</groupId>
    <artifactId>kotest-extensions-testcontainers</artifactId>
    <version>${kotest.version}</version>
    <scope>test</scope>
</dependency>
```


Having these dependencies in test classpath will bring extension methods into scope which let you convert any `Startable` such as a `DockerContainer` into a kotest `TestListener`, which you can register with Kotest and then Kotest will manage lifecycle of container for you.

For example:

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.perTest
import org.testcontainers.containers.GenericContainer

class DatabaseRepositoryTest : FunSpec({
   val redisContainer = GenericContainer<Nothing>("redis:5.0.3-alpine")
   listener(redisContainer.perTest()) //converts container to listener and registering it with Kotest.

   test("some test which assume to have redis container running") {
      //
   }
})
```

In above example, the ```perTest()``` extension method converts the container into a ```TestListener```, which starts the
redis container before each test and stops it after test. Similarly if you want to reuse the container for all tests
in a single spec class you can use ```perSpec()``` extension method, which converts the container into a ```TestListener```
which starts the container before running any test in the spec, and stops it after all tests, thus a single container is
used by all tests in spec class.
