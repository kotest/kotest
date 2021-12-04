---
id: koin
title: Koin
sidebar_label: Koin
slug: koin.html
---



## Koin

The [Koin DI Framework](https://insert-koin.io/) can be used with Kotest through the `KoinExtension` extension.

To use the extension in your project, add the dependency to your project:


[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-koin.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-koin)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-koin.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-koin/)



```kotlin
io.kotest.extensions:kotest-extensions-koin:${version}
```

With the dependency added, we can use Koin in our tests!

```kotlin
class KotestAndKoin : FunSpec(), KoinTest {

    override fun extensions() = listOf(KoinExtension(myKoinModule))

    val userService by inject<UserService>()

    init {
      test("Use user service") {
        userService.getUser().username shouldBe "LeoColman"
      }
    }

}
```

In the default mode, the extension will start/stop the Koin context between root tests. If you are used a nested style, and want koin to start/stop between leaf tests, then you can specify the `KoinLifecycleMode`.

```kotlin
class KotestAndKoin : DescribeSpec(), KoinTest {

    override fun extensions() = listOf(KoinExtension(myKoinModule, KoinLifecycleMode.Test))

    val userService by inject<UserService>()

    init {
      describe("Use user service") {
        it("inside a leaf test") {
          userService.getUser().username shouldBe "LeoColman"
        }
        it("and again with a fresh application here") {
          userService.getUser().username shouldBe "LeoColman"
        }
      }
    }

}
```
