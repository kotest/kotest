---
id: ranges
title: Range Matchers
slug: range-matchers.html
sidebar_label: Ranges
---

This page describes the rich assertions (matchers) that are available for [ClosedRange](https://kotlinlang.org/docs/ranges.html) and [OpenEndRange](https://kotlinlang.org/docs/ranges.html) types.


| Ranges                            |                                                                                                                        |
|-----------------------------------|------------------------------------------------------------------------------------------------------------------------|
| `value.shouldBeIn(range)`         | Asserts that an object is contained in range, checking by value and not by reference.                                  |
| `value.shouldNotBeIn(range)`      | Asserts that an object is not contained in range, checking by value and not by reference.                              |
| `range.shouldIntersect(range)`    | Asserts that a range intersects with another range. Both ranges can be either `ClosedRange` or `OpenEndRange`.         |
| `range.shouldNotIntersect(range)` | Asserts that a range does not intersect with another range. Both ranges can be either `ClosedRange` or `OpenEndRange`. |

