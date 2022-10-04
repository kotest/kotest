---
id: robolectric
title: Robolectric
sidebar_label: Robolectric
slug: robolectric.html
---



## Robolectric

[![Latest Release](https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-robolectric)](https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-robolectric)


[Robolectric](http://robolectric.org/) can be used with Kotest through the `RobolectricExtension` which can be found in a separate repository,[kotest-extensions-robolectric](https://github.com/kotest/kotest-extensions-robolectric)

To add this module to project you need specify following in your `build.gradle`:

```kotlin
testImplementation("io.kotest.extensions:kotest-extensions-robolectric:${version}")
```

This dependency brings in `RobolectricExtension`, which is autoregistered to your projects.

Now all you need to do is annotate Robolectric specs with `@RobolectricTest` and you're set!

```kotlin
@RobolectricTest
class MyTest : ShouldSpec({
    should("Access Robolectric normally!") {

    }
})
```



