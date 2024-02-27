---
id: tags
title: Grouping Tests with Tags
slug: tags.html
sidebar_label: Grouping Tests
---


Sometimes you don't want to run all tests and Kotest provides tags to be able to determine which
tests are executed at runtime. Tags are objects inheriting from `io.kotest.core.Tag`.

For example, to group tests by operating system you could define the following tags:

```kotlin
object Linux : Tag()
object Windows: Tag()
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

## Marking Tests

Test cases can then be marked with tags using the `config` function:

```kotlin
import io.kotest.specs.StringSpec

class MyTest : StringSpec() {
  init {
    "should run on Windows".config(tags = setOf(Windows)) {
      // ...
    }

    "should run on Linux".config(tags = setOf(Linux)) {
      // ...
    }

    "should run on Windows and Linux".config(tags = setOf(Windows, Linux)) {
      // ...
    }
  }
}
```


## Running with Tags

Then by invoking the test runner with a system property of `kotest.tags` you can control which tests are run. The expression to be
passed in is a simple boolean expression using boolean operators: `&`, `|`, `!`, with parenthesis for association.

For example, `Tag1 & (Tag2 | Tag3)`

Provide the simple names of tag object (without package) when you run the tests.
Please pay attention to the use of upper case and lower case! If two tag objects have the same simple name (in different name spaces) they are treated as the same tag.


Example: To run only test tagged with `Linux`, but not tagged with `Database`, you would invoke
Gradle like this:

```
gradle test -Dkotest.tags="Linux & !Database"
```

Tags can also be included/excluded in runtime (for example, if you're running a project configuration instead of properties) through the `RuntimeTagExtension`:

```kotlin
RuntimeTagExpressionExtension.expression = "Linux & !Database"
```

## Tag Expression Operators

Operators (in descending order of precedence)

| Operator | Description | Example                     |
|----------|-------------|-----------------------------|
| !        | not         | !macos                      |
| &        | and         | linux & integration         |
| &#124;   | or          | windows &#124; microservice |





## Tagging All Tests

You can add a tag to all tests in a spec using the tags function in the spec itself. For example:

```kotlin
class MyTestClass : FunSpec({

  tags(Linux, Mysql)

  test("my test") { } // automatically marked with the above tags
})
```

:::caution
When tagging tests in this way, the spec class will still need to be instantiated in order to examine the tags on each test, because the test itself may define further tags.
:::

:::note
If no root tests are active at runtime, the [beforeSpec](lifecycle_hooks.md) and [afterSpec](lifecycle_hooks.md) callbacks will _not_ be invoked.
:::

## Tagging a Spec

There are two annotations you can add to a spec class itself - @Tags and @RequiresTag - which accept one or more tag names as their arguments.

The first tag - @Tags - will be applied to all tests in the class, however this will only stop a spec from being instantiated if we can guarantee
that no tests would be executed (because a tag is being explicitly excluded).

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


The second tag - @RequiresTag - only checks that all the referenced tags are present and if not, will skip the spec.

For example, the following spec would be skipped and not instantiated unless the Linux and Mysql tags were
specified at runtime.

```kotlin
@RequiresTag("Linux", "Mysql")
class MyTestClass : FunSpec()
```


:::note
Note that when you use these annotations you pass the tag string name, not the tag itself. This is due to Kotlin annotations only allow "primitive" arguments
:::

### Inheriting tags

By default, the `@Tags` annotation will only be considered on the immediate Spec which it was applied to. However, a Spec can also inherit tags from superclasses and superinterfaces. To enable this, toggle `tagInheritance = true` in your [project config](./project-config.html)


## Gradle

**Special attention is needed in your gradle configuration**

To use System Properties (-Dx=y), your gradle must be configured to propagate them to the test executors, and an extra configuration must be added to your tests:

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
    systemProperties = System.getProperties().associate { it.key.toString() to it.value }
}
```

This will guarantee that the system property is correctly read by the JVM.
