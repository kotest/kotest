---
title: Should Be
slug: shouldbe.html
---

The _main_ matcher or assertion in Kotest is the `shouldBe` matcher. This matcher is used to assert _equality_ between
an an actual and an expected value. The syntax is in the format `actual shouldBe expected` For example:

```kotlin
val a = "samuel"
val b = a.take(3)
b shouldBe "sam"
```

When two values do not compare equal, Kotest will print out a nice error message including an intellij <click to see
difference> between the two values. For example:

```
Expected :world
Actual   :hello
<Click to see difference>
```

Note, you can check two values are _not_ equal using `shouldNotBe`.

```kotlin
val a = "samuel"
val b = a.take(3)
b shouldNotBe "bob"
```

:::tip
The `shouldBe` matcher can be combined with [power assert](power-assert.md) for greater effect.
:::

Behind the scenes, Kotest uses the `equals` method but also adds extra logic to determine equality for some types where
simple object equality isn't quite appropriate. For example, on the JVM, it is well known that `Arrays` with the same
contents will not be considered equal when using the equals method. Another example is primitives of different types
even with the same value.

This logic is encapsulated in the `Eq` typeclass which Kotest uses internally. It is also possible to define your own
equality logic for types by implementing `Eq` and registering it with Kotest.

## Custom Eq Instances

Let's show an example of creating a custom `Eq` instance for comparing `Foo` objects. Firstly, the definition of `Foo`.

```kotlin
data class Foo(val value: String)
```

Then we implement the `Eq` typeclass for whatever equality logic we want, returning an `EqResult` which is either
`Success` or `Failure`.

Here are are saying that if one `Foo` contains the string `hello` and the other contains the string `world` then they
are equal. To return a failure message we can use the `AssertionErrorBuilder` which is a helper to build the
appropriate concrete `AssertionError` for whichever platform we are running on.

```kotlin
object FooEq : Eq<Foo> {
  override fun equals(actual: Foo, expected: Foo, context: EqContext): EqResult {
    return if (actual.value == "hello" && expected.value == "world")
      EqResult.Success
    else EqResult.Failure {
      AssertionErrorBuilder.create().withMessage("I don't like foo").build()
    }
  }
}
```

:::tip
If we specify the _expected_ and _actual_ values to the error builder the <click to see difference> link will be
automatically generated too.
:::

Then we register it with Kotest, specifying the type that we want to use it for. Here we are
using [project config](../framework/project_config.md) to set it up before any tests are run. We could do this at the
spec level too, but bear in mind if you are running tests in parallel then the registration will be non-deterministic.

```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override suspend fun beforeProject() {
    DefaultEqResolver.register(Foo::class, FooEq)
  }
}
```

Finally, we can use our custom `Eq` instance in our tests by simply using `shouldBe` or `shouldNotBe` as normal.

```kotlin
test("custom eq should be selected if both sides are the same type") {
  Foo("hello") shouldBe Foo("world")
}
```

:::note
Custom `Eq` instances are only selected if both sides of the call are the type specified when registered. Also the type
must be exact, subclasses are not selected automatically and must also be registered
:::


