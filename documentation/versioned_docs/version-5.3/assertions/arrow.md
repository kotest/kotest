---
title: Arrow
slug: arrow.html
sidebar_label: Arrow
---


This page lists all current matchers in the Kotest arrow matchers extension library.

To use this library you need to add `io.kotest.extensions:kotest-assertions-arrow` to your build.

:::note
In the case `io.arrow-kt:arrow-core:arrow-version` is not in your classpath, please add it. To prevent Unresolved Reference errors.
:::

| Option                   |                                                           |
|--------------------------|-----------------------------------------------------------|
| `option.shouldBeSome()`  | Asserts that the option is of type Some and returns value |
| `option.shouldBeSome(v)` | Asserts that the option is of type Some with value v      |
| `option.shouldBeNone()`  | Asserts that the option is of type None                   |

| Either                    |                                                                      |
|---------------------------|----------------------------------------------------------------------|
| `either.shouldBeRight()`  | Asserts that the either is of type Right and returns the Right value |
| `either.shouldBeRight(v)` | Asserts that the either is of type Right with specified value v      |
| `either.shouldBeLeft()`   | Asserts that the either is of type Left and returns the Left value   |
| `either.shouldBeLeft(v)`  | Asserts that the either is of type Left with specific value v        |

| NonEmptyList                         |                                                                            |
|--------------------------------------|----------------------------------------------------------------------------|
| `nel.shouldContain(e)`               | Asserts that the NonEmptyList contains the given element e                 |
| `nel.shouldContainAll(e1,e2,...,en)` | Asserts that the NonEmptyList contains all the given elements e1,e2,...,en |
| `nel.shouldContainNull()`            | Asserts that the NonEmptyList contains at least one null                   |
| `nel.shouldContainNoNulls()`         | Asserts that the NonEmptyList contains no nulls                            |
| `nel.shouldContainOnlyNulls()`       | Asserts that the NonEmptyList contains only nulls or is empty              |
| `nel.shouldHaveDuplicates()`         | Asserts that the NonEmptyList has at least one duplicate                   |
| `nel.shouldBeSingleElement(e)`       | Asserts that the NonEmptyList has a single element which is e              |
| `nel.shouldBeSorted()`               | Asserts that the NonEmptyList is sorted                                    |

| Validated                     |                                                                             |
|-------------------------------|-----------------------------------------------------------------------------|
| `validated.shouldBeValid()`   | Asserts that the validated is of type Valid and returns the Valid value     |
| `validated.shouldBeValid(v)`  | Asserts that the validated is of type Valid with specific value v           |
| `validated.shouldBeInvalid()` | Asserts that the validated is of type Invalid and returns the Invalid value |
