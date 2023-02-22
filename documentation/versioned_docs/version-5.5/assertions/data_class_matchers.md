---
id: composed_matchers
title: Composed Matchers
slug: composed-matchers.html
sidebar_label: Composed Matchers
---



Composed matchers can be created for any `class` or `interface` by composing one or more other matchers along with the property to extract to
test against. This allows us to build up complicated matchers from simpler ones.

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

Now we can simply combine these together to make a John in Warsaw matcher. Notice that we specify the property to
extract to pass to each matcher in turn.

```kotlin
fun personMatcher(name: String, age: Int) = Matcher.compose(
  nameMatcher(name) to Person::name,
  ageMatcher(age) to Person::age,
  addressMatcher to Person::address
)
```

And we could add the extension variant too:

```kotlin
fun Person.shouldBePerson(name: String, age: Int) = this shouldBe personMatcher(name, age)
```

Then we invoke like this:

```kotlin
Person("John", 21, Address("Warsaw", "Test", "1/1")).shouldBePerson("John", 21)
Person("Sam", 22, Address("Chicago", "Test", "1/1")).shouldBePerson("John", 21) // would fail
```
