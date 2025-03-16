---
id: index
title: Assertions
slug: assertions.html
---







Kotest is split into several subprojects which can be used independently. One of these subprojects is
the comprehensive assertion / matchers support. These can be used with the [Kotest test framework](../framework/index.md),
or with another test framework like JUnit or Spock.


[![version badge](https://img.shields.io/maven-central/v/io.kotest/kotest-assertions-core-jvm.svg?label=release)](https://search.maven.org/search?q=g:io.kotest)
[![version badge](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.kotest/kotest-assertions-core-jvm.svg?label=snapshot)](https://s01.oss.sonatype.org/content/repositories/snapshots/io/kotest/)

The core functionality of the assertion modules are functions that test state. Kotest calls these types of state
assertion functions _matchers_. There are [core](matchers.md) matchers and matchers for third party libraries.

There are also many other utilities for writing tests, such as [testing for exceptions](exceptions.md), functions to
help test [non-determistic code](nondeterministic_testing.md), [inspectors](inspectors.md) for collections, and
[soft assertions](soft_assertions.md) to group assertions.

## Multitude of Matchers

For example, to assert that a variable has an expected value, we can use the `shouldBe` function.

```kotlin
name shouldBe "sam"
```

There are general purpose matchers, such as `shouldBe` as well as matchers for many other specific scenarios,
such as `str.shouldHaveLength(10)` for testing the length of a string, and `file.shouldBeDirectory()` which test
that a particular file points to a directory. They come in both infix and regular variants.

Assertions can generally be chained, for example:

```kotlin
"substring".shouldContain("str")
           .shouldBeLowerCase()

myImageFile.shouldHaveExtension(".jpg")
           .shouldStartWith("https")
```



There are over 350 matchers spread across multiple modules. Read about all the [matchers here](matchers.md).






## Clues

Sometimes a failed assertion does not contain enough information to know exactly what went wrong.

For example,

```kotlin
user.name shouldNotBe null
```

If this failed, you would simply get:

```
<null> should not equal <null>
```

Which isn't particularly helpful. We can add extra context to failure messages through the use of [clues](clues.md).






## Inspectors

Inspectors allow us to test elements in a collection, and assert the quantity of elements that should be
expected to pass (all, none, exactly k and so on). For example

```kotlin
mylist.forExactly(3) {
    it.city shouldBe "Chicago"
}
```

Read about [inspectors here](inspectors.md)




## Custom Matchers

It is easy to add your own matchers by extending the `Matcher<T>` interface, where T is the type you wish to match against. Custom matchers can compose existing matchers or be completely standalone.

See a [full worked example](custom.md).

