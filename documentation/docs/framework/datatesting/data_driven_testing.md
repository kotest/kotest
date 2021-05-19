---
id: introduction
title: Introduction
slug: data-driven-testing.html
---


:::note
This section covers the new and improved data test support that was released with Kotest 4.6.0. To view the
documentation for the previous data test support, [click here](data_driven_testing_4.2.0.md)
:::

When writing tests that are logic based, one or two specific code paths that work through particular scenarios make
sense. Other times we have tests that are more example based, and it would be helpful to test many combinations of
parameters.

In these situations, **data driven testing** (also called table driven testing) is an easy technique to avoid tedious
boilerplate.

Kotest has first class support for data driven testing built into the framework.
This means Kotest will automatically generate test case entries, based on input values provided by you.

## Getting Started

Let's consider writing tests for a [pythagorean triple](https://en.wikipedia.org/wiki/Pythagorean_triple) function that
returns true if the input values are valid triples (_a squared + b squared = c squared_).

```kotlin
fun isPythagTriple(a: Int, b: Int, c: Int): Boolean = a * a + b * b == c * c
```

Since we need more than one element per row (we need 3), we start by definining a data class that will hold a single _
row_ of values (in our case, the two inputs, and the expected result).

```kotlin
data class PythagTriple(val a: Int, val b: Int, val c: Int)
```

We will create tests by using instances of this data class, passing them into the `withData` function, which also
accepts a lambda that performs the test logic for that given _row_.

For example:

```kotlin
class MyTests : FunSpec({
  context("Pythag triples tests") {
    withData(
      PythagTriple(3, 4, 5),
      PythagTriple(6, 8, 10),
      PythagTriple(8, 15, 17),
      PythagTriple(7, 24, 25)
    ) { (a, b, c) ->
      isPythagTriple(a, b, c) shouldBe true
    }
  }
})
```

Notice that because we are using data classes, the input row can be destructured into the member properties.
When this is executed, we will have 4 test cases in our input, one for each input row.

Kotest will automatically generate a test case for each input row, as if you had manually written a seperate test case
for each.

![data test example output](datatest1.png)

The test names are generated from the data classes themselves but can be [customized](#custom-test-names).

If there is an error for any particular input row, then the test will fail and Kotest will output the values that
failed. For example, if we change the previous example to include the row `PythagTriple(5, 4, 3)`
then that test will be marked as a failure.

![data test example output](datatest2.png)

The error message will contain the error and the input row details:

`Test failed for (a, 5), (b, 4), (c, 3) expected:<9> but was:<41>`

In that previous example, we wrapped the `withData` call in a parent test, so we have more context when the test results appear.
The syntax varies depending on the [spec style](docs/framework/styles) used - here we used _fun spec_ which uses context blocks for containers.
In fact, data tests can be nested inside any number of containers.

But this is optional, you can define data tests at the root level as well.

For example:

```kotlin
class MyTests : FunSpec({
  withData(
    PythagTriple(3, 4, 5),
    PythagTriple(6, 8, 10),
    PythagTriple(8, 15, 17),
    PythagTriple(7, 24, 25)
  ) { (a, b, c) ->
    isPythagTriple(a, b, c) shouldBe true
  }
})
```


## Stable Names

When generating test names, Kotest needs a _stable_ test name. Otherwise, test reports can be messed up if the name used
changes over the course of the test suite execution.

Kotest will only use the `toString()` of the input class if it thinks the input class has a stable `toString()` value
otherwise it will use the class name.

You can force Kotest to use the `toString()` for test names by annotating your type with `@IsStableType`. Then
the `toString()` will be used regardless.

Alternatively, you can completely customize the display name of the test.
See [customing test names](custom_test_names.md).

