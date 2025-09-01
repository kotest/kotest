---
id: system_extensions
title: System Extensions
sidebar_label: System Extensions
slug: system_extensions.html
---




## System Extensions

If you need to test code that uses `java.lang.System`, Kotest provides extensions that can alter the system and restore it after each test.
This extension is only available on the JVM.

To use this extension, add the dependency to your project:

[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-jvm.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest/kotest-extensions-jvm)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-jvm%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-jvm/maven-metadata.xml)


```kotlin
io.kotest:kotest-extensions:${version}
```

:::caution
This extension does not support concurrent test execution. Due to the JVM specification there can only be one instance of these extensions running (For example: Only one Environment map must exist). If you try to run more than one instance at a time, the result is undefined.
:::


### System Property Extension

You can override the System Properties (`System.getProperties()`) by either using a listener at the spec level,
or by using the `withSystemProperty` function to wrap any arbitrary code.

With the function:

```kotlin
withSystemProperty("foo", "bar") {
  System.getProperty("foo") shouldBe "bar"
}
```

And as an extension:

```kotlin
class MyTest : FreeSpec() {
  override val extensions = listOf(SystemPropertyTestListener("foo", "bar"))

  init {
    "MyTest" {
      System.getProperty("foo") shouldBe "bar"
    }
  }
}
```

### No-stdout / no-stderr listeners

Maybe you want to guarantee that you didn't leave any debug messages around, or that you're always using a Logger in your logging.

For that, Kotest provides you with `NoSystemOutListener` and `NoSystemErrListener`. These listeners won't allow any messages to be printed straight to `System.out` or `System.err`, respectively:

```kotlin
// In Project or in Spec
override val extensions = listOf(NoSystemOutListener, NoSystemErrListener)
```

### Locale/Timezone listeners

Some codes use and/or are sensitive to the default Locale and default Timezone. Instead of manipulating the system defaults no your own,
let Kotest do it for you!

```kotlin
withDefaultLocale(Locale.FRANCE) {
  println("My locale is now France! Très bien!")
}

withDefaultTimeZone(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo"))) {
  println("My timezone is now America/Sao_Paulo! Muito bem!")
}
```

And as an extension:

```kotlin
// In Project or in Spec
override val extensions = listOf(
  LocaleTestListener(Locale.FRANCE),
  TimeZoneTestListener(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo")))
)
```
