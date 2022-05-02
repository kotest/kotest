---
id: shrinking
title: Shrinking
slug: property-test-shrinking.html
sidebar_label: Shrinking
---

In property-based testing, the initially found failing case may contain a lot of complexity that does actually cause the test to fail.
Shrinking is the mechanism through which a property-based testing framework can simplify failing cases in order to find out the minimal reproducible case is.
In Kotest, the way in which failing cases from [generators](gens.md) are shrunk is defined by implementations of the `Shrinker` interface.
Built-in generators generally have a default Shrinker defined by the framework, while custom generators can be given a custom Shrinker implementation.

## Shrinking for built-in generators
Built-in generators (see [Generators List](genslist.md)) have a default Shrinker defined by the framework.
A shrink function takes as input the value that failed the test and returns a list of new values on which Kotest can appy the test.
The exact behaviour depends on the data type.
For instance, a string could be shrunk by dropping the first or last character while for integers we could decrement or halve the value.
In addition, shrink behaviour is defined for edge cases such as an empty string or the integer 0.
The shrinking is performed when a test that uses such generator fails.

```kotlin
Arb.positiveInt().checkAll { i ->
    calculateProperty(i) shouldBe true
}
```

If the test fails for one of the generated inputs then the shrinking result is shown:

```
Property test failed for inputs

0) 1792716902

Caused by io.kotest.assertions.AssertionFailedError: expected:<1792716902> but was:<0> at
	PropertyBasedTest$1$1$3$1.invokeSuspend(PropertyBasedTest.kt:54)
	PropertyBasedTest$1$1$3$1.invoke(PropertyBasedTest.kt)
	PropertyBasedTest$1$1$3$1.invoke(PropertyBasedTest.kt)
	io.kotest.property.internal.ProptestKt$proptest$3$2.invokeSuspend(proptest.kt:45)

Attempting to shrink arg 1792716902
Shrink #1: 1 pass
Shrink #2: 597572300 fail
Shrink #3: 199190766 fail
Shrink #4: 66396922 fail
Shrink #5: 22132307 fail
Shrink #6: 7377435 fail
Shrink #7: 2459145 fail

[...]

Shrink #999: 29948 pass
Shrink #1000: 44922 pass
Shrink #1001: 59896 pass
Shrink #1002: 89839 fail
Shrink result (after 1002 shrinks) => 89839

Caused by io.kotest.assertions.AssertionFailedError: expected:<89839> but was:<0> at
	PropertyBasedTest$1$1$3$1.invokeSuspend(PropertyBasedTest.kt:54)
	PropertyBasedTest$1$1$3$1.invoke(PropertyBasedTest.kt)
	PropertyBasedTest$1$1$3$1.invoke(PropertyBasedTest.kt)
	io.kotest.property.internal.ShrinkfnsKt$shrinkfn$1$1$smallestA$1.invokeSuspend(shrinkfns.kt:19)
```

By default, Kotest will shrink 1000 times. This behaviour is configurable.
For example, if you want to continue shrinking without bounds:

```kotlin
Arb.positiveInt().checkAll(PropTestConfig(shrinkingMode = ShrinkingMode.Unbounded)) { i ->
    calculateProperty(i) shouldBe true
}
```

## Shrinking for custom generators
[Custom generators](customgens.md) do not have a Shrinker defined by Kotest.
Instead, custom Shrinkers can be implemented.
Below is an example where the Shrinker returns coordinates that are next to the value itself.

```kotlin
data class Coordinate(val x: Int, val y: Int)

class CoordinateTest : FunSpec({
    context("Coordinate Transformations") {
        // Shrinker takes the four neighbouring coordinates
        val coordinateShrinker = Shrinker<Coordinate> { c ->
            listOf(
                Coordinate(c.x - 1, c.y),
                Coordinate(c.x, c.y - 1),
                Coordinate(c.x + 1, c.y),
                Coordinate(c.x, c.y + 1),
            )
        }
        val coordinateArb = arbitrary(coordinateShrinker) {
            Coordinate(Arb.nonNegativeInt().bind(), Arb.nonNegativeInt().bind())
        }

        test("Coordinates are always positive after transformation") {
            coordinateArb.checkAll {
                transform(it).x shouldBeGreaterThanOrEqualTo 0
                transform(it).y shouldBeGreaterThanOrEqualTo 0
            }
        }
    }
})
```
