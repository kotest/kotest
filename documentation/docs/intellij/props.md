---
id: props
title: Properties
slug: intellij-properties.html
---



When running tests via the intellij runner, properties set using `gradle.properties` or in a gradle build file won't be
picked up because the runner is not set to use Gradle.

To support runtime system properties, the Kotest framework will always look for key value pairs inside
a `kotest.properties` file located on the classpath (eg, in src/main/resources).

Any key value pairs located in this file will be set as a system property before any tests execute.

:::tip
Any properties specified in the `kotest.properties` file work for both command line via Gradle, and tests executed via the Intellij plugin.
:::

For example, after adding this file to your classpath as `kotest.properties`:

```
foo=bar
```

The following test would pass:

```kotlin
class FooTest : DescribeSpec() {
  init {
    describe("after adding kotest.properties") {
      it("foo should be set") {
         System.getProperty("foo") shouldBe "bar"
      }
    }
  }
}
```


### Common use case

It is common to disable the classpath scanning capabilities of Kotest to save some startup time, if those features are not used.
To do this place, the following lines into the `kotest.properties` file:

```
kotest.framework.classpath.scanning.config.disable=true
kotest.framework.classpath.scanning.autoscan.disable=true
```

### Specifying the properties filename

If you don't wish to name the file `kotest.properties`, or perhaps you want to support different files based on an environment,
then you can use the system property `kotest.properties.filename` to set the properties filename.

For example, you could launch tests with `kotest.properties.filename=cluster.prd.properties` then the key value file named
`cluster.prd.properties` would be loaded before any tests are executed.
