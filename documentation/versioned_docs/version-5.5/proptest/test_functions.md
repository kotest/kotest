---
id: testfunctions
title: Property Test Functions
slug: property-test-functions.html
sidebar_label: Test Functions
---


There are two variants of functions that are used to execute a property test in Kotest: `forAll` and `checkAll`.

### For All

The first, `forAll`, accepts an n-arity function `(a, ..., n) -> Boolean` that tests the property.
The test will pass if, for all input values, the function returns true.

```kotlin
class PropertyExample: StringSpec({
   "String size" {
      forAll<String, String> { a, b ->
         (a + b).length == a.length + b.length
      }
   }
})
```


Notice that this functions accepts type parameters for the argument types, with arity up to 14.
Kotest uses these type parameters to locate a _generator_ which provides (generates) random values of a suitable type.

For example, `forAll<String, Int, Boolean> { a, b, c -> }` is a 3-arity property test where
argument `a` is a random String, argument `b` is a random int, and argument `c` is a random boolean.



### Check All

The second, `checkAll`, accepts an n-arity function `(a, ..., n) -> Unit` in which you can simply execute assertions against the inputs.
This approach will consider a test valid if no exceptions are thrown.
Here is the same example again written in the equivalent way using checkAll.

```kotlin
class PropertyExample: StringSpec({
   "String size" {
      checkAll<String, String> { a, b ->
         a + b shouldHaveLength a.length + b.length
      }
   }
})
```

The second approach is more general purpose than returning a boolean, but the first approach is from the original
haskell libraries that inspired this library.



### Iterations

By default, Kotest will run the property test 1000 times. We can easily customize this by specifying the iteration count
when invoking the test method.

Let's say we want to run a test 10,000 times.

```kotlin
class PropertyExample: StringSpec({
   "a many iterations test" {
      checkAll<Double, Double>(10_000) { a, b ->
         // test here
      }
   }
})
```




### Specifying Generators

You saw in the previous examples that Kotest would provide values automatically based on the type parameter(s).
It does this by locating a _generator_ that generates values for the required type.

For example, the automatically provided _Integer_ generator generates random ints from all possible values -
negative, positive, infinities, zero and so on.

This is fine for basic tests but often we want more control over the sample space.
For example, we may want to test a function for numbers in a certain range only.

Then you would need to specify the generator(s) manually.

```kotlin
class PropertyExample: StringSpec({
   "is allowed to drink in Chicago" {
      forAll(Arb.int(21..150)) { a ->
         isDrinkingAge(a) // assuming some function that calculates if we're old enough to drink
      }
   }
   "is allowed to drink in London" {
      forAll(Arb.int(18..150)) { a ->
         isDrinkingAge(a) // assuming some function that calculates if we're old enough to drink
      }
   }
})
```

You can see we created two tests and in each test passed a generator into the `forAll` function with a suitable int range.

See [here](gens.md) for a list of the built in generators.
