package com.sksamuel.kotlintest.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.DataSpec
import io.kotlintest.tables.row

class DataSpecTest : DataSpec({

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

})