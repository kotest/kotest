package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.tables.Row3
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.table

abstract class AbstractDataSpec(body: AbstractDataSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  operator fun <A, B, C> String.invoke(vararg rows: Row3<A, B, C>, testfn: (A, B, C) -> Unit) {
    table(headers("a", "b", "c"), *rows).forAll { a, b, c -> testfn(a, b, c) }
  }
}