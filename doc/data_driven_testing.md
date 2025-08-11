Data-driven Testing
--------------------

## ⛔️ [This page has moved](https://kotest.io/docs/framework/datatesting/data-driven-testing.html) ⛔ ️

To test your code with different parameter combinations, you can use a table of values as input for your test
cases. This is called _data driven testing_ also known as _table driven testing_.

Invoke the `forAll` or `forNone` function, passing in one or more `row` objects, where each row object contains
the values to be used be a single invocation of the test. After the `forAll` or `forNone` function, setup your
actual test function to accept the values of each row as parameters.

The row object accepts any set of types, and the type checker will ensure your types are consistent with the parameter
types in the test function.

```kotlin
"square roots" {
  forAll(
      row(2, 4),
      row(3, 9),
      row(4, 16),
      row(5, 25)
  ) { root, square ->
    root * root shouldBe square
  }
}
```

In the above example, the `root` and `square` parameters are automatically inferred to be integers.

If there is an error for any particular input row, then the test will fail and KotlinTest will automatically
match up each input to the corresponding parameter names. For example, if we change the previous example to include the row `row(5,55)`
then the test will be marked as a failure with the following error message.

```
Test failed for (root, 5), (square, 55) with error expected: 55 but was: 25
```

Table testing can be used within any spec. Here is an example using `StringSpec`.

```kotlin
class StringSpecExample : StringSpec({
  "string concat" {
    forAll(
      row("a", "b", "c", "abc"),
      row("hel", "lo wo", "rld", "hello world"),
      row("", "z", "", "z")
    ) { a, b, c, d ->
      a + b + c shouldBe d
    }
  }
})
```

It may be desirable to have each row of data parameters as an individual test. To generating such individual tests follow a similar pattern for each spec style. An example in the `FreeSpec` is below.

```kotlin
class IntegerMathSpec : FreeSpec({
    "Addition" - {
        listOf(
            row("1 + 0", 1) { 1 + 0 },
            row("1 + 1", 2) { 1 + 1 }
        ).map { (description: String, expected: Int, math: () -> Int) ->
            description {
                math() shouldBe expected
            }
        }
    }
    // ...
    "Complex Math" - {
        listOf(
            row("8/2(2+2)", 16) { 8 / 2 * (2 + 2) },
            row("5/5 + 1*1 + 3-2", 3) { 5 / 5 + 1 * 1 + 3 - 2 }
        ).map { (description: String, expected: Int, math: () -> Int) ->
            description {
                math() shouldBe expected
            }
        }
    }
})
```

Produces 4 tests and 2 parent descriptions:

```txt
IntegerMathSpec
  ✓ Addition
    ✓ 1 + 0
    ✓ 1 + 1
  ✓ Complex Math
    ✓ 8/2(2+2)
    ✓ 5/5 + 1*1 + 3-2
```
