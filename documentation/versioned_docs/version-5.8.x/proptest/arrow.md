---
id: arrow
title: Arrow Generators
slug: property-test-generators-arrow.html
sidebar_label: Arrow Generators
---

Kotest provides an optional module that provides generators for [Arrow](https://arrow-kt.io).

:::note
To use, add `io.kotest.extensions:kotest-property-arrow:version`and `io.arrow-kt:arrow-core:arrow-version` to your build.
This holds true for the optics module `kotest-property-arrow-optics`, by adding `io.arrow-kt:arrow-optics:arrow-version`, too.
:::

[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-property-arrow?label=latest%20release"/>](https://search.maven.org/search?q=kotest-property-arrow)


| Generator                          | Description                                                                                                                    |
|------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| **Either**                         |
| `Arb.either(arbL, arbR)`           | Generates approx 50/50 of left and right from the underlying generators.                                                       |
| `Arb.right(arb)`                   | Generates instances of [Either.Right] using the given arb.                                                                     |
| `Arb.left(arb)`                    | Generates instances of [Either.Left] using the given arb.                                                                      |
| **NonEmptyList**                   |
| `Arb.nel(arb)`                     | Generates NonEmptyList instances with a size randomly choosen between 1 and 100, with elements populated from the given arb.   |
| `Arb.nel(arb, range)`              | Generates NonEmptyList instances with a size randomly chosen from the given range, with elements populated from the given arb. |
| **Option**                         |
| `Exhaustive.option(a)`             | Returns an Exhaustive that contains a None and a Some with the given value.                                                    |
| `Exhaustive.none(a)`               | Returns an Exhaustive that contains None.                                                                                      |
| `Arb.option(arb)`                  | Generates both None and Some with Some's populated with values from the given arb.                                             |
| `Arb.some(arb)`                    | Generates Some's populated with values from the given arb.                                                                     |
| `Arb.none()`                       | A constant arb that returns None. Equivalent to Exhaustive.None and provided only for use when an Arb is required.             |
| **Endo**                           |                                                                                                                                |
| `Arb.endo(arb)`                    | Wraps values from the underlying arb in `Endo` instances.                                                                      |
| **Eval**                           |                                                                                                                                |
| `Arb<A>.evalNow()`                 | Wraps values from the receiver in `Eval.now`.                                                                                  |
| **Validated**                      |                                                                                                                                |
| `Arb.validated(invalid, valid)`    | Generates approx 50/50 of valid and invalid `Validated` instances using the supplied arbs for values                           |
| `Arb.validatedNel(invalid, valid)` | Generates approx 50/50 of valid and invalid `ValidatedNel` instances using the supplied arbs for values                        |
