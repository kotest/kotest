---
title: Arrow
slug: arrow.html
sidebar_label: Arrow
---


[![Latest Release](https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-assertions-arrow)](https://search.maven.org/artifact/io.kotest.extensions/kotest-assertions-arrow)

This page lists all current matchers in the Kotest arrow matchers extension library.

:::note
The following module is needed: `io.kotest.extensions:kotest-assertions-arrow` which is versioned independently of the main Kotest project.
Search maven central for latest version [here](https://central.sonatype.com/search?q=io.kotest.extensions:kotest-assertions-arrow).
:::

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

| Validated                      |                                                                             |
|--------------------------------|-----------------------------------------------------------------------------|
| `validated.shouldBeValid()`    | Asserts that the validated is of type Valid and returns the Valid value     |
| `validated.shouldBeValid(v)`   | Asserts that the validated is of type Valid with specific value v           |
| `validated.shouldBeInvalid()`  | Asserts that the validated is of type Invalid and returns the Invalid value |
| `validated.shouldBeInvalid(v)` | Asserts that the validated is of type Invalid with specific value v         |
