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

Note that this syntax does **not** automatically compose shrinkers from the inner arbitraries.
When a property test using this generator fails, no shrinking will occur unless you provide a custom `Shrinker` explicitly.
See [Shrinking](shrinking.md) for details on providing a custom shrinker, and the section below for approaches that provide automatic shrinking.

### Arb.bind — recommended for data classes

When building a custom `Arb` for a data class or record type, `Arb.bind` is the **recommended approach**.
It combines the shrinkers of each component arbitrary automatically, meaning that on failure the framework
will attempt to shrink each field independently toward a minimal failing case.

```kotlin
data class Person(val name: String, val age: Int)

val personArb: Arb<Person> = Arb.bind(
   Arb.string(10..12),
   Arb.int(21, 150),
   ::Person
)
```

### Comparing the three approaches

There are three common ways to build a custom `Arb` for a composite type, each with different shrinking behaviour:

| Approach | Shrinking |
|---|---|
| `Arb.bind(arbA, arbB, ...) { ... }` | **Full** — each component is shrunk independently. Recommended for data classes. |
| `arbA.flatMap { ... }` | **Partial** — only the outermost arbitrary's shrinker applies. Inner values are held fixed during shrinking. |
| `arbitrary { arbA.bind(); arbB.bind() }` | **None** — no automatic shrinking unless a custom `Shrinker` is provided. |

Because `Arb.bind` produces the best shrinking results with no extra effort, prefer it over `flatMap` or the `arbitrary`
builder DSL whenever you are composing independent arbitraries into a record type.
Use the `arbitrary` builder when you need imperative logic or dependencies between fields, and pair it with a custom
`Shrinker` if shrinking is important for your tests.

### Exhaustive

When writing a custom exhaustive we can use the `exhaustive()` extension function on a List. Nothing more to it than that really!

```kotlin
val singleDigitPrimes = listOf(2,3,5,7).exhaustive()
```

```kotlin
class PropertyExample: FreeSpec({
    "testing single digit primes" {
        checkAll(singleDigitPrimes) { prime ->
           isPrime(prime) shouldBe true
           isPrime(prime * prime) shouldBe false
        }
    }
})
```
