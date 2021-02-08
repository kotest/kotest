---
id: test_containers
title: Test Containers
sidebar_label: Test Containers
slug: test_containers.html
---



## Test Containers

[testcontainers-java](https://github.com/testcontainers/testcontainers-java) library that provide lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

```kotest-extensions-testcontainers``` provides integration for using testcontainers-java with kotest.

For using ```kotest-extensions-testcontainers``` add the below dependency in your build file.

```groovy
testImplementation("io.kotest:kotest-extensions-testcontainers:${version}")
```

Having this dependency in test classpath brings in extension method's in scope which let you convert any Startable such as a DockerContainer into a kotest TestListener, which you can register with Kotest and then Kotest will manage lifecycle of container for you.

For example:

```kotlin

class DatabaseRepositoryTest : FunSpec({
   val redisContainer = GenericContainer<Nothing>("redis:5.0.3-alpine")
   listener(redisContainer.perTest()) //converts container to listener and registering it with Kotest.

   test("some test which assume to have redis container running") {
      //
   }
})
```

In above example ```perTest()``` extension method converts the container into a ```TestListener``` which start's the
redis container before each test and stop's that after test. Similarly if you want to reuse the container for all tests
in a single spec class you can use ```perSpec()``` extension method which convert's container into a ```TestListener```
which start's the container before running any test in spec and stop's that after all tests, thus a single container is
used by all tests in spec class.




