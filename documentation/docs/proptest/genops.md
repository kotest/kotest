---
id: genops
title: Generator Operations
slug: generator-operations.html
---




## Next

If you want to use an Arb to just return a value (even outside of a property test), then you can call next on it.

```kotlin
val arbA: Arb<A> = ...
val a = arbA.next() // use Random.Default
val a2 = arbA.next(rs) // pass in Random
```



## Filter

If you have an arb and you want to create a new arb that provides a subset of values, you can call filter on the source arb.
For example, one way of generating even numbers is to take the integer arb, and filter out odd values. Viz:

```kotlin
val evens = Arb.int().filter { it.value % 2 == 0 }
val odds = Arb.int().filter { it.value % 2 == 1 }
```



## Map

If you have an arb and you want to transform the value generated, you can use map.
```kotlin
val integerStrings: Arb<String> = Arb.int().map { it.toString() }
```


## FlatMap

If you have an arb whose emission or edgecases depends on the emission of the previous arbitraries, you can use flatMap.
```kotlin
val dependentArbs: Arb<String> = Arb.of("foo", "bar").flatMap { prefix ->
   Arb.int(1..10).map { integer ->
      "${prefix}-${integer}"
   }
}
```



## Merging

Two generators can be merged together, so that elements 0, 2, 4, ... are taken from the first generator, and elements 1, 3, 5, ... are taken from the second generator.

```kotlin
val merged = arbA.merge(arbB)
```

So with the following example:

```kotlin
val a = arbitrary { "a" }
val b = arbitrary { "b" }
val ab = a.merge(b)
ab.take(10).forEach { println(it) }
```

Would ouput `ababababab`


## Bind

Bind is useful if you want to apply multiple arbitraries. We can take a look at how we might construct values for a data class using bind.

```kotlin
data class Person(val name: String, val age: Int)

val personArb: Arb<Person> = Arb.bind(
   Arb.string(),
   Arb.int()
) { name, age -> Person(name, age) }
```
