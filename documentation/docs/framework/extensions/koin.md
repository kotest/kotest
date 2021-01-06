---
id: koin
title: Koin
sidebar_label: Koin
slug: koin.html
---



## Koin

The [Koin DI Framework](https://insert-koin.io/) can be used with Kotest through the `KoinListener` test listener and its own interface `KoinTest`.

To add the listener to your project, add the depency to your project:
```groovy
testImplementation("io.kotest:kotest-extensions-koin:${version}")
```

With the dependency added, we can use Koin in our tests!

```kotlin
class KotestAndKoin : FunSpec(), KoinTest {

    override fun listeners() = listOf(KoinListener(myKoinModule))

    val userService by inject<UserService>()

    init {
      test("Use user service") {
        userService.getUser().username shouldBe "Kerooker"
      }
    }

}
```
