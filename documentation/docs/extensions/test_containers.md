---
id: test_containers
title: Test Containers
sidebar_label: Test Containers
slug: test_containers.html
---



## Test Containers

[testcontainers-java](https://github.com/testcontainers/testcontainers-java) library that provide lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

```kotest-extensions-testcontainers``` provides integration for using testcontainers-java with kotest.

To use ```kotest-extensions-testcontainers```, add the below dependency to your Gradle build file.

```groovy
testImplementation("io.kotest:kotest-extensions-testcontainers:${kotest.version}")
```

For Maven, you will need these dependencies:

```xml
            <dependency>
                <groupId>io.kotest</groupId>
                <artifactId>kotest-extensions-testcontainers</artifactId>
                <version>${kotest.version}</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>io.kotest</groupId>
                <artifactId>kotest-extensions-testcontainers-jvm</artifactId>
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
