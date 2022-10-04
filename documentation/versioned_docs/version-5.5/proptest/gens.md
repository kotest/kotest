---
id: gens
title: Generators
slug: property-test-generators.html
sidebar_label: Generators
---


Generated values are provided by instances of the sealed class `Gen`. You can think of a `Gen` as kind of like an input
stream but for property test values. Each Gen will provide a (usually) infinite stream of these values for one
particular type.

Kotest has two types of generators - `Arb` for generating arbitrary (random) values and `Exhaustive` for generating a
finite set of values in a closed space.

Both types of gens can be mixed and matched in property tests. For example, you could test a function with 100 random
positive integers (an arb) alongside every even number from 0 to 200 (exhaustive).

Some generators are only available on the JVM. See the full list [here](genslist.md).

## Arbitrary

`Arb`s generate two types of values - a hard coded set of _edge cases_ and an infinite stream of _randomly chosen
samples_.

The samples may be repeated, and some values may never be generated at all. For example generating 1000
integers between 0 and Int.MAX cannot return all possible values, and some values may happen to be generated
more than once. Similarly, generating 1000 random integers between 0 and 500 will definitely result in some values
appearing more than once.

Some common arbitraries include numbers with or without a range, strings in the unicode set, random lists,
data classes with random parameters, emails, codepoints, chars and so on.

In addition to the random values, arbs may provide edge cases. One of the design features of Kotest's property testing
is that values for some types will always include "common" edge cases that you probably want to be included in your
tests.

For example, when testing a function that accepts an integer, you probably want to ensure that, at the very least, it is
tested with zero, a positive number and a negative number. If only random values were provided, the chances of zero
appearing would be fairly low, so Kotest will always provide some "edge cases" for integers (unless you specify
otherwise).

When executing tests, the framework will alternate randomly between samples and edge cases. The split is determined
by a configuration value which defaults to 2% edge cases.

Not all arbs have edge cases, but the arbs for the most common types do.
Here are some examples of edge cases used by some arbs:

* ints: 0, 1, -1, Int.MAX_VALUE, Int.MIN_VALUE
* doubles: 0, 1, -1, Double.MAX_VALUE, Double.MIN_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN
* strings: empty string, string of min length, lowest codepoint
* lists: empty list, list of a single element, list with duplicate elements
* maps: empty map
* nullable values: null

## Exhaustive

`Exhaustive`s generate all values from a given space. This is useful when you want to ensure every value in that space
is used. For example, for enum values, it is usually more helpful to ensure each enum is used, rather than picking
randomly from the enums values and potentially missing some and duplicating others.

Typical exhaustives include small collections, enums, boolean values, powerset of a list or set, pre-defined integer
ranges, and predefined string ranges.

Once an exhaustive has provided all it's values, it will loop and start again, so an exhaustive can be used in a test
that requires any number of inputs.

For example:

```
enum class Season { Winter, Fall, Spring, Summer }

forAll<Int, Season>(100) { a, season -> ... }
```

Here we asked for 100 iterations, so each value of Season would be provided 25 times.



