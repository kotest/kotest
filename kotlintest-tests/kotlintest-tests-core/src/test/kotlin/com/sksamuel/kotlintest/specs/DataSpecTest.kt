package com.sksamuel.kotlintest.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.DataSpec
import io.kotlintest.tables.row

class DataSpecTest : DataSpec({

  "square roots"(
      row(2, 4),
      row(3, 9),
      row(4, 16),
      row(5, 25)
  ) { a, b ->
    a * a shouldBe b
  }

  "maximum of two numbers"(
      row(1, 5, 5),
      row(5, 1, 5),
      row(1, 1, 1),
      row(0, 1, 1),
      row(1, 0, 1),
      row(0, 0, 0)
  ) { a, b, c ->
    Math.max(a, b) shouldBe c
  }

  "string concat"(
      row("a", "b", "c", "abc"),
      row("hel", "lo wo", "rld", "hello world"),
      row("", "z", "", "z")
  ) { a, b, c, d ->
    a + b + c shouldBe d
  }
})