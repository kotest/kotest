---
id: composed_matchers
title: Composed Matchers
slug: composed-matchers.html
sidebar_label: Composed Matchers
---

Composed matchers can be created for any type by composing one or more matchers. This allows to
build up complex matchers from simpler ones. There are two logical operations, using which we can compose matchers:
logical sum (`Matcher.any`) and logical product (`Matcher.all`).

Let's say we'd like to define a password `Matcher`, which will `containADigit()`, `contain(Regex("[a-z]"))` and
`contain(Regex("[A-Z]"))`. We can compose these matchers this way:
```kotlin
val passwordMatcher = Matcher.all(
   containADigit(), contain(Regex("[a-z]")), contain(Regex("[A-Z]"))
)
```

We can add extension function then:

```kotlin
fun String.shouldBeStrongPassword() = this shouldBe passwordMatcher
```

So it can be invoked like this:

```kotlin
"StrongPassword123".shouldBeStrongPassword()
"WeakPassword".shouldBeStrongPassword() // would fail
```

By analogy, we can build a composed matcher using `Matcher.any`.
In this case, `passwordMatcher` will fail only if all matchers fail, otherwise it will pass.

```kotlin
val passwordMatcher = Matcher.any(
   containADigit(), contain(Regex("[a-z]")), contain(Regex("[A-Z]"))
)
```

Composed matchers can also be created for any `class` or `interface` by composing one or more other matchers along with
the property to extract to test against.

For example, say we had the following structures:

```kotlin
data class Person(
  val name: String,
  val age: Int,
  val address: Address,
)

data class Address(
  val city: String,
  val street: String,
  val buildingNumber: String,
)
```

And our goal is to have a `Person` matcher that checks for people in Warsaw. We can define matchers for each of those
components like this:

```kotlin
fun nameMatcher(name: String) = Matcher<String> {
  MatcherResult(
    value == name,
    { "Name $value should be $name" },
    { "Name $value should not be $name" }
  )
}

fun ageMatcher(age: Int) = Matcher<Int> {
  MatcherResult(
    value == age,
    { "Age $value should be $age" },
    { "Age $value should not be $age" }
  )
}

val addressMatcher = Matcher<Address> {
  MatcherResult(
    value == Address("Warsaw", "Test", "1/1"),
    { "Address $value should be Test 1/1 Warsaw" },
    { "Address $value should not be Test 1/1 Warsaw" }
  )
}
```

Now we can simply combine these together to make a *John in Warsaw matcher*. Notice that we specify the property to
extract to pass to each matcher in turn.

```kotlin
fun personMatcher(name: String, age: Int) = Matcher.all(
  havingProperty(nameMatcher(name) to Person::name),
  havingProperty(ageMatcher(age) to Person::age),
  havingProperty(addressMatcher to Person::address)
)
```

And we can add the extension variant too:

```kotlin
fun Person.shouldBePerson(name: String, age: Int) = this shouldBe personMatcher(name, age)
```

Then we invoke it this way:

```kotlin
Person("John", 21, Address("Warsaw", "Test", "1/1")).shouldBePerson("John", 21)
Person("Sam", 22, Address("Chicago", "Test", "1/1")).shouldBePerson("John", 21) // would fail
```
