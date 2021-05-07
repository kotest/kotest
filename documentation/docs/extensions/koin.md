---
id: koin
title: Koin
sidebar_label: Koin
slug: koin.html
---



## Koin

The [Koin DI Framework](https://insert-koin.io/) can be used with Kotest through the `KoinListener` test listener and its own interface `KoinTest`.

To add the listener to your project, add the dependency to your project:


[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-koin.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotest-extensions-koin)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-koin.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-koin/)



```kotlin
io.kotest.extensions:kotest-extensions-koin:${version}
```

With the dependency added, we can use Koin in our tests!

```kotlin
class KotestAndKoin : FunSpec(), KoinTest {

    override fun listeners() = listOf(KoinListener(myKoinModule))

    val userService by inject<UserService>()

    init {
      test("Use user service") {
        userService.getUser().username shouldBe "LeoColman"
      }
    }

}
```
