package com.sksamuel.kotest.data

import io.kotest.data.forall
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.FreeSpec
import io.kotest.specs.StringSpec
import io.kotest.tables.row
import kotlin.math.max
import io.kotest.data.suspend.forall as forallSuspendOverload

class DataDrivenTestingTest : StringSpec() {

  init {

    "square roots" {
      forall(
          row(2, 4),
          row(3, 9),
          row(4, 16),
          row(5, 25)
      ) { root, square ->
        root * root shouldBe square
      }

      forallSuspendOverload(
              row(2, 4),
              row(3, 9),
              row(4, 16),
              row(5, 25)
      ) { root, square ->
        root * root shouldBe square
      }
    }

    "maximum of two numbers" {
      forall(
          row(1, 5, 5),
          row(5, 1, 5),
          row(1, 1, 1),
          row(0, 1, 1),
          row(1, 0, 1),
          row(0, 0, 0)
      ) { a, b, max ->
        max(a, b) shouldBe max
      }

      forallSuspendOverload(
              row(1, 5, 5),
              row(5, 1, 5),
              row(1, 1, 1),
              row(0, 1, 1),
              row(1, 0, 1),
              row(0, 0, 0)
      ) { a, b, max ->
        max(a, b) shouldBe max
      }
    }

    "string concat" {
      forall(
          row("a", "b", "c", "abc"),
          row("hel", "lo wo", "rld", "hello world"),
          row("", "z", "", "z")
      ) { a, b, c, d ->
        a + b + c shouldBe d
      }

      forallSuspendOverload(
              row("a", "b", "c", "abc"),
              row("hel", "lo wo", "rld", "hello world"),
              row("", "z", "", "z")
      ) { a, b, c, d ->
        a + b + c shouldBe d
      }
    }

    "row3 should detect header names from params" {
      shouldThrow<AssertionError> {
        forall(
            row(1, 2, 3)
        ) { foo, bar, woo ->
          foo * bar * woo shouldBe 0
        }
      }.message shouldBe "Test failed for (foo, 1), (bar, 2), (woo, 3) with error expected: 0 but was: 6"

      shouldThrow<AssertionError> {
        forallSuspendOverload(
                row(1, 2, 3)
        ) { foo, bar, woo ->
          foo * bar * woo shouldBe 0
        }
      }.message shouldBe "Test failed for (foo, 1), (bar, 2), (woo, 3) with error expected: 0 but was: 6"
    }

    "row2 should detect header names from params" {
      shouldThrow<AssertionError> {
        forall(
            row(2, 4)
        ) { foo, bar ->
          foo * bar shouldBe 0
        }
      }.message shouldBe "Test failed for (foo, 2), (bar, 4) with error expected: 0 but was: 8"

      shouldThrow<AssertionError> {
        forallSuspendOverload(
                row(2, 4)
        ) { foo, bar ->
          foo * bar shouldBe 0
        }
      }.message shouldBe "Test failed for (foo, 2), (bar, 4) with error expected: 0 but was: 8"
    }



    "row1 should detect header names from params" {
      shouldThrow<AssertionError> {
        forall(
            row(2)
        ) { foo ->
          foo shouldBe 0
        }
      }.message shouldBe "Test failed for (foo, 2) with error expected: 0 but was: 2"

      shouldThrow<AssertionError> {
        forallSuspendOverload(
                row(2)
        ) { foo ->
          foo shouldBe 0
        }
      }.message shouldBe "Test failed for (foo, 2) with error expected: 0 but was: 2"
    }

    "row9 should detect header names from params" {
      shouldThrow<AssertionError> {
        forall(
            row(1, 2, 3, 4, 5, 6, 7, 8, 9)
        ) { foo1, foo2, foo3, foo4, foo5, foo6, foo7, foo8, foo9 ->
          foo1 shouldBe 0
        }
      }.message shouldBe "Test failed for (foo1, 1), (foo2, 2), (foo3, 3), (foo4, 4), (foo5, 5), (foo6, 6), (foo7, 7), (foo8, 8), (foo9, 9) with error expected: 0 but was: 1"


      shouldThrow<AssertionError> {
        forallSuspendOverload(
                row(1, 2, 3, 4, 5, 6, 7, 8, 9)
        ) { foo1, foo2, foo3, foo4, foo5, foo6, foo7, foo8, foo9 ->
          foo1 shouldBe 0
        }
      }.message shouldBe "Test failed for (foo1, 1), (foo2, 2), (foo3, 3), (foo4, 4), (foo5, 5), (foo6, 6), (foo7, 7), (foo8, 8), (foo9, 9) with error expected: 0 but was: 1"
    }

    "row10 should detect header names from params" {
      shouldThrow<AssertionError> {
        forall(
            row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        ) { foo1, foo2, foo3, foo4, foo5, foo6, foo7, foo8, foo9, foo10 ->
          foo1 shouldBe 0
        }
      }.message shouldBe "Test failed for (foo1, 1), (foo2, 2), (foo3, 3), (foo4, 4), (foo5, 5), (foo6, 6), (foo7, 7), (foo8, 8), (foo9, 9), (foo10, 10) with error expected: 0 but was: 1"

      shouldThrow<AssertionError> {
        forallSuspendOverload(
                row(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        ) { foo1, foo2, foo3, foo4, foo5, foo6, foo7, foo8, foo9, foo10 ->
          foo1 shouldBe 0
        }
      }.message shouldBe "Test failed for (foo1, 1), (foo2, 2), (foo3, 3), (foo4, 4), (foo5, 5), (foo6, 6), (foo7, 7), (foo8, 8), (foo9, 9), (foo10, 10) with error expected: 0 but was: 1"
    }
  }
}

class DataDrivenTestingCoroutinesTest : FreeSpec() {
  init {

    // Issue 707 test, that guarantees the lambda works with the suspended string invocation
    "operations test" - {
      forallSuspendOverload(
              row(1, 2, 3, -1),
              row(2, 3, 5, -1)
      ) { a, b, c, d ->
        "addition" {
          a + b shouldBe c
        }
        "subtraction" {
          a - b shouldBe d
        }
      }
    }
  }
}
