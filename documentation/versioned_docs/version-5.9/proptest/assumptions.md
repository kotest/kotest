---
id: assumptions
title: Assumptions
slug: property-test-assumptions.html
sidebar_label: Assumptions
---


To restrict the set the generated values from a generator, we can use filter to return a constrained arb.

```kotlin
val evens = Arb.int().filter { it.value % 2 == 0 }
```

However if we need to restrict inputs based on the relationship between values, then filtering on a single arb won't
work. Consider an example where we want to test that two non-equal strings have a non-zero Levenshtein distance.

```kotlin
checkAll<String, String> { a, b ->
  levenshtein(a, b) shouldBeGreaterThan 0
}
```

This will periodically fail - whenever two equal strings are generated. One approach would be to just wrap the tests in
an if/else block and avoid those undesired inputs.

```kotlin
checkAll<String, String> { a, b ->
  if (a != b)
    levenshtein(a, b) shouldBeGreaterThan 0
}
```

But in more complicated scenarios we could easily introduce a bug and filter _all_ our inputs.

Kotest provides a feature called _assumptions_ that will filter out unwanted combinations, while tracking that we
are not filtering too many.

An assumption accepts a boolean value passed to the `withAssumptions` function, that if true, will allow the property
test to continue, but if false, that particular iteration is skipped. For example, the previous example will now pass:

```kotlin
checkAll<String, String> { a, b ->
  withAssumptions(a != b) {
    levenshtein(a, b) shouldBeGreaterThan 0
  }
}
```

Alternatively, you can use inline syntax:

```kotlin
checkAll<String, String> { a, b ->
  assume(a != b)
  levenshtein(a, b) shouldBeGreaterThan 0
}
```

### Assertions

Kotest expands on basic boolean assumptions by allowing you to specify assertions in an assumption function.

For example, building on the previous example:

```kotlin
checkAll(Arb.string(3..4, Codepoint.az()), Arb.string(3..4, Codepoint.az())) { a, b ->
  withAssumptions({
    a shouldNotBe b
    a shouldHaveLength (b.length)
  }) {
     a.compareTo(b) shouldNotBe 0
  }
}
```

Here we are ensuring that all inputs are not equal, and that the inputs have the same length. Any assertion that
throws `AssertionError` can be used here, including all the assertions provided by Kotest.

This also supports inline syntax:

```kotlin
checkAll<String, String> { a, b ->
  assume {
    a shouldNotBe b
    a shouldHaveLength (b.length)
  }
  levenshtein(a, b) shouldBeGreaterThan 0
}
```

### Max Discard Percentage

By default, the maximum discard percentage is 10%. If more combinations are discarded than that, the property test will
fail. This helps avoid a scenario where we erroneously discard too many, or even all, our inputs.

For example, the following would fail by default because we would be filtering ~50% of values.

```kotlin
checkAll<Int, Int> { a, b ->
  withAssumptions(a % 2 == 0) {
    ..
  }
}
```

But if we wanted to allow this regardless, we can use the `maxDiscardPercentage` to increase the allowed discard rate.

```kotlin
checkAll<Int, Int>(PropTestConfig(maxDiscardPercentage = 55)) { a, b ->
  withAssumptions(a % 2 == 0) {
    ..
  }
}
```

It is generally better to adjust your arbs to produce values closer to what you need, so that you only need to filter
out unwanted edge cases.
