---
id: system_extensions
title: System Extensions
sidebar_label: System Extensions
slug: system_extensions.html
---




## System Extensions

If you need to test code that uses `java.lang.System`, Kotest provides extensions that can alter the system and restore it after each test. This extension is only available on the JVM.

To use this extension, add the dependency to your project:

[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-jvm.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest/kotest-extensions-jvm)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-extensions-jvm.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest-extensions-jvm/)

```kotlin
io.kotest:kotest-extensions-jvm:${version}
```

:::caution
This extension does not support concurrent test execution. Due to the JVM specification there can only be one instance of these extensions running (For example: Only one Environment map must exist). If you try to run more than one instance at a time, the result is undefined.
:::

### System Environment

With *System Environment Extension* you can simulate how the System Environment is behaving. That is, what you're obtaining from `System.getenv()`.

Kotest provides some extension functions that provides a System Environment in a specific scope:

```kotlin
withEnvironment("FooKey", "BarValue") {
    System.getenv("FooKey") shouldBe "BarValue" // System environment overridden!
}
```

:::info
To use `withEnvironment` with JDK17 you need to add `--add-opens=java.base/java.util=ALL-UNNAMED` to the arguments for the JVM that runs the tests.

If you run tests with gradle, you can add the following to your `build.gradle.kts`:

```kotlin
tasks.withType<Test>().configureEach {
    jvmArgs("--add-opens=java.base/java.util=ALL-UNNAMED")
}
```
:::

You can also use multiple values in this extension, through a map or list of pairs.

```kotlin
withEnvironment(mapOf("FooKey" to "BarValue", "BarKey" to "FooValue")) {
  // Use FooKey and BarKey
}

```

These functions will add the keys and values if they're not currently present in the environment, and will override them if they are. Any keys untouched by the function will remain in the environment, and won't be messed with.

Instead of extensions functions, you can also use the provided Listeners to apply these functionalities in a bigger scope. There's an alternative for the Spec/Per test level, and an alternative for the Project Level.

```kotlin

class MyTest : FreeSpec() {

      override fun listeners() = listOf(SystemEnvironmentTestListener("foo", "bar"))

    init {
      "MyTest" {
        System.getenv("foo") shouldBe "bar"
      }
    }

}

```

```kotlin

class ProjectConfig : AbstractProjectConfig() {

    override fun listeners(): List<TestListener> = listOf(SystemEnvironmentProjectListener("foo", "bar"))

}

```



### System Property Extension

In the same fashion as the Environment Extensions, you can override the System Properties (`System.getProperties()`):

```kotlin
withSystemProperty("foo", "bar") {
  System.getProperty("foo") shouldBe "bar"
}
```

And with similar Listeners:

```kotlin
    class MyTest : FreeSpec() {

          override fun listeners() = listOf(SystemPropertyListener("foo", "bar"))

        init {
          "MyTest" {
            System.getProperty("foo") shouldBe "bar"
          }
        }

    }
```



### System Security Manager

Similarly, with System Security Manager you can override the System Security Manager (`System.getSecurityManager()`)

```kotlin

    withSecurityManager(myManager) {
      // Usage of security manager
    }

```

And the Listeners:

```kotlin
    class MyTest : FreeSpec() {

              override fun listeners() = listOf(SecurityManagerListener(myManager))

            init {
              // Use my security manager
            }

        }
```

### System Exit Extensions

Sometimes you want to test that your code calls `System.exit`. For that you can use the `System Exit Listeners`. The Listener will throw an exception when the `System.exit` is called, allowing you to catch it and verify:

```kotlin

class MyTest : FreeSpec() {

  override fun listeners() = listOf(SpecSystemExitListener)

  init {
    "Catch exception" {
      val thrown: SystemExitException = shouldThrow<SystemExitException> {
        System.exit(22)
      }

      thrown.exitCode shouldBe 22
    }
  }
}

```

### No-stdout / no-stderr listeners

Maybe you want to guarantee that you didn't leave any debug messages around, or that you're always using a Logger in your logging.

For that, Kotest provides you with `NoSystemOutListener` and `NoSystemErrListener`. These listeners won't allow any messages to be printed straight to `System.out` or `System.err`, respectively:

```kotlin
    // In Project or in Spec
    override fun listeners() = listOf(NoSystemOutListener, NoSystemErrListener)
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

And with the listeners

```kotlin
  // In Project or in Spec
  override fun listeners() = listOf(
    LocaleTestListener(Locale.FRANCE),
    TimeZoneTestListener(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo")))
  )

```
