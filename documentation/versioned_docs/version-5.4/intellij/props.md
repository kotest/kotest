---
id: props
title: Properties
slug: intellij-properties.html
---



When running tests via the intellij runner, properties set using `gradle.properties` or in a gradle build file won't be picked up of course.

To support runtime System properties, the Kotest framework will always look for key value pairs inside a `kotest.properties` file located on the classpath.
Any key value pairs located in this file will be set as a system property before any tests execute.

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


### Specifying the properties filename

If you don't wish to name the file `kotest.properties`, or perhaps you want to support different files based on an environment,
then you can use the system property `kotest.properties.filename` to set the properties filename.

For example, you could launch tests with `kotest.properties.filename=cluster.prd.properties` then the key value file named
`cluster.prd.properties` would be loaded before any tests are executed.
