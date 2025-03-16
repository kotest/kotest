---
id: customgens
title: Custom Generators
slug: custom-generators.html
---


To write your own generator for a type T, you just create an instance of `Arb<T>` or `Exhaustive<T>`.


### Arbitrary

When writing a custom arbitrary we can use the `arbitrary` builder which accepts a lambda that must return the type we are generating for.
The parameter to this lambda is a `RandomSource` parameter which contains the seed and the `Random` instance. We should typically
use the provided `RandomSource` if we need access to a `kotlin.Random` instance, as this instance will have been seeded by the framework to allow for repeatable tests.

For example, here is a custom arb that generates a random int between 3 and 6 using the `arbitrary` builder.

```kotlin
val sillyArb = arbitrary { rs: RandomSource ->
   rs.random.nextInt(3..6)
}

```

In addition to the `RandomSource` parameter, the arbitrary builder lambda also provides the `ArbitraryBuilderSyntax` context which we can leverage
to compose other arbitraries when building ours.

For example, here is an `Arbitrary` that supports a custom class called `Person`, delegating to a String arbitrary and an Int arbitrary.

```kotlin
data class Person(val name: String, val age: Int)

val personArb = arbitrary {
   val name = Arb.string(10..12).bind()
   val age = Arb.int(21, 150).bind()
   Person(name, age)
}
```

The resulting arbitrary produced using this syntax is equivalent to using [map](genops.md#map),
[flatMap](genops.md#flatmap) and [bind](genops.md#bind).

### Exhaustive

When writing a custom exhaustive we can use the `exhaustive()` extension function on a List. Nothing more to it than that really!

```kotlin
val singleDigitPrimes = listOf(2,3,5,7).exhaustive()
```

```kotlin
class PropertyExample: StringSpec({
    "testing single digit primes" {
        checkAll(singleDigitPrimes) { prime ->
           isPrime(prime) shouldBe true
           isPrime(prime * prime) shouldBe false
        }
    }
})
```
