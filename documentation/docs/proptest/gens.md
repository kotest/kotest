---
id: gens
title: Generators
slug: property-test-generators.html
---

Generated values are provided by instances of the sealed class `Gen`.
You can think of a `Gen` as kind of like an input stream but for property test values.
Each Gen will provide a (usually) infinite stream of these values for one particular type.

Kotest has two types of generators - `Arb` for generating arbitrary (random) values and `Exhaustive` for generating a finite set of values in a closed space.

Both types of gens can be mixed and matched in property tests. For example,
you could test a function with 100 random positive integers (an arb) alongside every
even number from 0 to 200 (exhaustive).

Some generators are only available on the JVM. See the full list [here](genslist.md).



## Arbitrary

`Arb`s generate two types of values - a hard coded set of _edge cases_ and an infinite stream of _randomly chosen values_.

The random values may be repeated, and some values may never be generated at all.
For example generating 1000 random integers between 0 and Int.MAX will clearly not return all possible values,
and some values may happen to be generated more than once. Similarly, generating 1000 random integers between 0 and 500,
will definitely result in some values appearing more than once.

Typical arbs include numbers within a given range, strings in the unicode set,
random lists, random data classes, emails, codepoints, chars and much more.

In addition to the random values, arbs may provide edge cases. One of the design features of Kotest's property testing is
that values for some types will always include "common" edge cases that you probably want to make sure you test.

For example, when testing a function that accepts any integer, you probably want to ensure that at the very least, it is tested with
zero, a positive number and a negative number. So Kotest will always provide some "edge cases" for integers (unless you specify otherwise).

Not all arbs have edge cases, but the arbs for the most common types do. Here are some examples of edge cases used by certain arbs:

* ints: 0, 1, -1, Int.MAX_VALUE, Int.MIN_VALUE
* strings: empty string, string of min length
* lists: empty list
* maps: empty map
* nullable values: null


## Exhaustive

`Exhaustive`s generate all values from a given space. This is useful when you want to ensure every
value in that space is used. For example, for enum values, it is usually more helpful to ensure each
enum is used, rather than picking randomly from the enums values and potentially missing some and duplicating others.

Typical exhaustives include small collections, enums, boolean values, powerset of a list or set, pre-defined small integer ranges, and predefined string ranges.



