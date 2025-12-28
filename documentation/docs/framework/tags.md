---
id: tags
title: Grouping Tests with Tags
slug: tags.html
sidebar_label: Grouping Tests
---


Sometimes you don't want to run all tests and Kotest provides `tags` to be able to determine which
tests are executed at runtime. Tags are objects inheriting from `io.kotest.core.Tag`.

For example, to group tests by operating system you could define the following tags:

```kotlin
object Linux : Tag()
object Windows : Tag()
```

Alternatively, tags can be defined using the `NamedTag` class. When using this class, observe the following rules:

- A tag must not be null or blank.
- A tag must not contain whitespace.
- A tag must not contain ISO control characters.
- A tag must not contain any of the following characters:
  - !: exclamation mark
  - (: left paren
  - ): right paren
  - &: ampersand
  - |: pipe

For example:

```kotlin
val tag = NamedTag("Linux")
```

:::note
If two tag objects have the same simple name (even in different packages) they are treated as the same tag.
:::

## Adding tags to tests

### Tagging Tests

Tests can be tagged at various levels. Firstly, test cases themselves can have tags added via test `config`.
Note that any nested tags inherit tags from their parents.

```kotlin
class MyTest : FunSpec() {
  init {
    test("should run on Windows").config(tags = setOf(Windows)) {
      // ...
    }

    test("should run on Linux").config(tags = setOf(Linux)) {
      // ...
    }

    context("should run on Windows and Linux").config(tags = setOf(Windows, Linux)) {
      test("and nested tests") { // implicity has windows and linux tags added

      }
    }
  }
}
```

### Tagging Specs

Secondly, you can add tags at the spec level, either through the tags function in the spec itself, or through the @Tags
annotation. Any tags
added this way are applied to all tests implicitly.

:::caution
When tagging tests in this way, the spec class will still need to be instantiated in order to examine the tags on each
test, because the test itself may define further tags. Therefore, do not rely on this if you want to avoid instantiating
classes completely, and instead see @RequiresTag.
:::

```kotlin
@Tags("Foo") // applied to all tests in this spec
private class TaggedSpec : ExpectSpec() {
  init {

    tags(Windows) // applied to all tests in this spec

    expect("should run on Windows") {
      // ...
    }
  }
}
```

:::caution
Any tags added via @Tags do not stop the spec from being instantiated, as the engine
needs to check for any tags added via code. If you want to avoid a spec from being instantiated completely, use
@RequiresTag.
:::

### @RequiresTag

Finally, you can use the @RequiresTag annotation. This only checks that all the referenced tags are present and
if not, will skip the spec entirely. This is an important distinction, because with the other annotation - @Tags - the
spec will still need to be instantiated in order to check for any tags added via the DSL. This can be counter-intuitive.

For example, the following spec would be skipped and not instantiated unless the Linux and Mysql tags were
specified at runtime.

```kotlin
@RequiresTag("Linux", "Mysql")
class MyTestClass : FunSpec()
```

:::note
Note that when you use annotations you pass the string name of the tag, not the tag instance itself.
:::


## Executing with Tags

By invoking the test runner with a system property of `kotest.tags` or an environment variable of `KOTEST_TAGS` you can
control which tests are run. The expression to be passed in is a simple boolean expression using boolean operators: `&`,
`|`, `!`, with parenthesis for association.

For example, `Tag1 & (Tag2 | Tag3)`

Provide the simple names of tag object (without package) when you run the tests. Eg, a tag created as
`val mytag = NamedTag("A")` would use the tag name `A`.

:::caution
Please pay attention to the use of upper case and lower case! Tags are case-sensitive.
:::

Example: To run only test tagged with `Linux`, but not tagged with `Database`, you would invoke Gradle
using the environment variable (since Kotest 6.1.0).

```
KOTEST_TAGS="Linux & !Database" gradle check
```

Or using the system property from the command line.

```
gradle test -Dkotest.tags="Linux & !Database"
```

Or by specifying the system property inside your build script.

```kotlin
tasks.withType<Test>().configureEach {
  systemProperty("kotest.tags", "Linux & !Database")
}
```

:::tip
Prefer the environment variable as it works on all platforms, unless you are on an earlier version of Kotest
(prior to 6.1), or need to specify the tags in your Gradle build script rather than at the command line.
:::

:::note
If no root tests are active at runtime, the [beforeSpec](lifecycle_hooks.md) and [afterSpec](lifecycle_hooks.md)
callbacks will _not_ be invoked.
:::


### Tag Expression Operators

Operators (in descending order of precedence)

| Operator | Description | Example                     |
|----------|-------------|-----------------------------|
| !        | not         | !macos                      |
| &        | and         | linux & integration         |
| &#124;   | or          | windows &#124; microservice |


### Inheriting tags

By default, the `@Tags` annotation will only be considered on the immediate Spec which it was applied to. However, a
Spec can also inherit tags from superclasses and superinterfaces. To enable this, toggle `tagInheritance = true` in
your [project config](./project_config.md)

## Examples

Consider the following example:

```kotlin
@Tags("Linux")
class MyTestClass : FunSpec({

  tags(UnitTest)

  beforeSpec { println("Before") }

  test("A").config(tags = setOf(Mysql)) {}
  test("B").config(tags = setOf(Postgres)) {}
  test("C") {}
})
```

| Runtime Tags               | Spec Created | Callbacks | Outcome                                                                                                                                                                                                                                       |
|----------------------------|--------------|-----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| kotest.tags=Linux          | yes          | yes       | A, B, C are executed because all tests inherit the `Linux` tag from the annotation                                                                                                                                                            |
| kotest.tags=Linux & Mysql  | yes          | yes       | A is executed only because all tests have the `Linux` tag, but only A has the `Mysql` tag                                                                                                                                                     |
| kotest.tags=!Linux         | no           | no        | No tests are executed, and the MyTestClass is not instantiated because we can exclude it based on the tags annotation                                                                                                                         |
| kotest.tags=!UnitTest      | yes          | no        | No tests are executed because all tests inherit `UnitTest` from the tags function. MyTestClass is instantiated in order to retrieve the tags defined in the class. The beforeSpec callback is not executed because there are no active tests. |
| kotest.tags=Mysql          | yes          | yes       | A is executed only, because that is the only test marked with `Mysql`                                                                                                                                                                         |
| kotest.tags=!Mysql         | yes          | yes       | B, C are executed only, because A is excluded by being marked with `Mysql`                                                                                                                                                                    |
| kotest.tags=Linux & !Mysql | yes          | yes       | B, C are executed only, because all tests inherit `Linux` from the annotation, but A is excluded by the `Mysql` tag                                                                                                                           |



## Gradle

**Special attention is needed in your Gradle configuration when using system properties**

To use System Properties (-Dx=y), Gradle must be configured to propagate them to the test executors, and an extra
configuration must be added to your tests:

Groovy:

```groovy
test {
  //... Other configurations ...
  systemProperties = System.properties
}
```

Kotlin Gradle DSL:

```kotlin
val test by tasks.getting(Test::class) {
  // ... Other configurations ...
  systemProperties = System.getProperties().asIterable().associate { it.key.toString() to it.value }
}
```

This will guarantee that the system property is correctly read by the JVM.
