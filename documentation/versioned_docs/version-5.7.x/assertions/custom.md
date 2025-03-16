---
id: custom_matchers
title: Custom Matchers
slug: custom-matchers.html
sidebar_label: Custom Matchers
---


It is easy to define your own matchers in Kotest.

Simply extend the `Matcher<T>` interface, where T is the type you wish to match against. The `Matcher` interface
specifies one method, `test` which returns an instance of `MatcherResult`.

```kotlin
interface Matcher<in T> {
  fun test(value: T): MatcherResult
}
```

This `MatcherResult` type defines three methods - a boolean to indicate if the test passed or failed, and two failure
messages.

```kotlin
interface MatcherResult {
  fun passed(): Boolean
  fun failureMessage(): String
  fun negatedFailureMessage(): String
}
```

The first failure message is the message to the user if the matcher predicate failed. Usually you can include some
details of the expected value and the actual value and how they differed. The second failure message is the message to
the user if the matcher predicate evaluated true in _negated_ mode. Here you usually indicate that you expected the
predicate to fail.

The difference in those two messages will be clearer with an example. Let's consider writing a length matcher for
strings, to assert that a string has a required length. We will want our syntax to be something
like `str.shouldHaveLength(8)`.

Then the first message should be something like `"string had length 15 but we expected length 8"`. The second message
would need to be something like `"string should not have length 8"`

First we build out our matcher type:

```kotlin
fun haveLength(length: Int) = Matcher<String> { value ->
  MatcherResult(
    value.length == length,
    { "string had length ${value.length} but we expected length $length" },
    { "string should not have length $length" },
  )
}
```

Notice that we wrap the error messages in a function call so we don't evaluate if not needed. This is important for
error messages that take some time to generate.

This matcher can then be passed to the `should` and `shouldNot` infix functions as follows:

```kotlin
"hello foo" should haveLength(9)
"hello bar" shouldNot haveLength(3)
```

## Extension Variants

Usually, we want to define extension functions which invoke the matcher function for you and return the original value
for chaining. This is how Kotest structures the built in matchers, and Kotest adopts a shouldXYZ naming strategy. For
example:

```kotlin
fun String.shouldHaveLength(length: Int): String {
  this should haveLength(length)
  return this
}

fun String.shouldNotHaveLength(length: Int): String {
  this shouldNot haveLength(length)
  return this
}
```

Then we can invoke these like:

```kotlin
"hello foo".shouldHaveLength(9)
"hello bar".shouldNotHaveLength(3)
```
