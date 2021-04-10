---
id: robolectric
title: Robolectric
sidebar_label: Robolectric
slug: robolectric.html
---



## Robolectric

[Robolectric](http://robolectric.org/) can be used with Kotest through the `RobolectricExtension` which can be found in a separate repository,[kotest-extensions-robolectric](https://github.com/kotest/kotest-extensions-robolectric)

To add this module to project you need specify following in your `build.gradle`:

[![Latest Release](https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-robolectric)](https://search.maven.org/search?q=g:io.kotest.extensions%20a:kotest-extensions-robolectric)

```kotlin
testImplementation("io.kotest.extensions:kotest-extensions-robolectric:${version}")
```
With this dependency added you should add extensions to your project config. For example if you have no such config yet it would look like

```kotlin
class MyProjectLevelConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = super.extensions() + RobolectricExtension()
}
```

Of course you can just add this extension to another extensions you're already using.

After that done any class which should be ran with Robolectric should be annotated with `@RobolectricTest` annotation.


