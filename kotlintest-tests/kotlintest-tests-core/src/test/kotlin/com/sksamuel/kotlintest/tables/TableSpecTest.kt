package com.sksamuel.kotlintest.tables

import io.kotlintest.AbstractSpec
import io.kotlintest.shouldBe
import io.kotlintest.tables.Row3
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table

abstract class DataSpec(body: DataSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  operator fun <A, B, C> String.invoke(vararg rows: Row3<A, B, C>, testfn: (A, B, C) -> Unit) {
    table(headers("a", "b", "c"), *rows).forAll { a, b, c -> testfn(a, b, c) }
  }
}

data class Data<A, B, C>(val rows: List<Row3<A, B, C>>)

fun <A, B, C> inputs(vararg rows: Row3<A, B, C>): Data<A, B, C> = Data(rows.toList())

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