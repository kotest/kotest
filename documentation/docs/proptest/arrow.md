---
id: arrow
title: Arrow Generators
slug: property-test-generators-arrow.html
sidebar_label: Arrow Generators
---


An optional module provides generators for [Arrow](https://arrow-kt.io). These generators are JVM only.

| Generator    | Description |
| -------- | ----------- |
| **Either** |
| `Arb.either(arbL, arbR)` |  Generates approx 50/50 of left and right from the underlying generators |
| `Arb.right(arb)` | Generates instances of [Either.Right] using the given arb |
| `Arb.left(arb)` | Generates instances of [Either.Left] using the given arb |
| **NonEmptyList** |
| `Arb.nel(arb, range)` |  Generates NonEmptyList instances with a size randomly chosen from the given range (defaults to 1 to 100), with elements populated from the given arb. |
| **Option** |
| `Exhaustive.option(a)` | Returns an Exhaustive that contains a None and a Some with the given value |
| `Exhaustive.none(a)` | Returns an Exhaustive that contains None |
| `Arb.option(arb)` | Generates both None and Some with Some's populated with values from the given arb |
| `Arb.some(arb)` | Generates Some's populated with values from the given arb |
| `Arb.none()` | A constant arb that returns None. Equivalent to Exhaustive.None and provided only for use when an Arb is required. |

