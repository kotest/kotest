Assertions
==========

Kotest is split into several subprojects which can be used independently. One of these subprojects is
the comprehensive assertion / matchers support. These can be used with Kotest test framework, or with
another test framework like JUnit or Spock.

## Matchers

The core functionality of the assertion modules is without doubt the statements that
confirm that your test is in the state you expect. For example, asserting that a variable has an expected value:

`name shouldBe "sam"`

Kotest calls these functions _matchers_.

There are general purpose matchers, such as `shouldBe` as well as matchers for many other specific scenarios,
such as `str.shouldHaveLength(10)` and `file.shouldBeDirectory()`

There are over 350 matchers spread across multiple modules. Read about all the [matchers here](matchers.md).


## Inspectors

Inspectors allow us to test elements in a collection, and assert the quantity of elements that should be
expected to pass (all, none, exactly k and so on)

Read about [inspectors here](inspectors.md)
