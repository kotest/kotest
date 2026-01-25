---
id: props
title: Properties
slug: intellij-properties.html
---



The IntelliJ plugin now runs tests via Gradle, which means properties defined in `gradle.properties` or Gradle build files
are automatically available.

Additionally, the Kotest framework will always look for key-value pairs inside
a `kotest.properties` file located on the classpath (eg, in src/main/resources).

Any key value pairs located in this file will be set as a system property before any tests execute.

:::tip
The `kotest.properties` file provides a portable way to configure Kotest consistently across the command line, Gradle, and the IntelliJ plugin.
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
Add the following lines to the `kotest.properties` file:

```
kotest.framework.classpath.scanning.config.disable=true
kotest.framework.classpath.scanning.autoscan.disable=true
```

### Specifying the properties filename

If you don't wish to name the file `kotest.properties`, or perhaps you want to support different files based on an environment,
then you can use the system property `kotest.properties.filename` to set the properties filename.

For example, you could launch tests with `kotest.properties.filename=cluster.prd.properties` then the key-value file named
`cluster.prd.properties` would be loaded before any tests are executed.
