---
title: Konform Matchers
slug: konform-matchers.html
sidebar_label: Konform
---




Kotest provides various matchers for use with [Konform](https://github.com/konform-kt/konform).
They can be used in your tests to assert that a given object is validated or fails validation.

To use these matchers add `implementation 'io.kotest.extensions:kotest-assertions-konform:<version>'` to your build. This module is available for both JVM and JS targets.

Let's start with a basic data class:

```kotlin
data class UserProfile(
   val fullName: String,
   val age: Int?
)
```

Then given a `UserProfile` validator like this:

```kotlin
val validateUser = Validation<UserProfile> {
  UserProfile::fullName {
     minLength(4)
     maxLength(100)
  }

  UserProfile::age ifPresent {
     minimum(21)
     maximum(99)
  }
}
```

We can test that instances pass validation like this:

```kotlin
val alice = UserProfile("Alice", 25)
validateUser shouldBeValid alice
```

And we can test that instances fail validation with specific error messages like this:

```kotlin
val bob = UserProfile("bob", 18)
validateUser.shouldBeInvalid(bob) {
  it.shouldContainError(UserProfile::fullName, "must have at least 4 characters")
  it.shouldContainError(UserProfile::age, "must be at least '21'")
}
```


| Matcher | Description    |
| ---------- | --- |
| `validation.shouldBeValid(value)` | Asserts that the validation is valid for the given value |
| `validation.shouldBeInvalid(value)` | Asserts that the validation is invalid for the given value |
| `validation.shouldBeInvalid(value) { block }` | Asserts that the validation is invalid for the given value, and then, runs the block with invalid value |

