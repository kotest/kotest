---
id: robolectric
title: Robolectric
sidebar_label: Robolectric
slug: robolectric.html
---



## Robolectric

[Robolectric](http://robolectric.org/) can be used with Kotest through the `RobolectricExtension` which can be found in `kotest-extensions-robolectric` module.

To add this module to project you need specify following in your `build.gradle`:

```groovy
testImplementation 'io.kotest:kotest-extensions-robolectric:<version>'
```
With this dependency added you should add extensions to your project config. For example if you have no such config yet it would look like

```kotlin
class MyProjectLevelConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = super.extensions() + RobolectricExtension()
}
```

Of course you can just add this extension to another extensions you're already using.

After that done any class which should be ran with Robolectric should be annotated with `@RobolectricTest` annotation.


