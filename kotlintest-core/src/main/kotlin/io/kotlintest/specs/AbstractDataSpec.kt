package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.tables.Row1
import io.kotlintest.tables.Row2
import io.kotlintest.tables.Row3
import io.kotlintest.tables.Row4
import io.kotlintest.tables.Row5
import io.kotlintest.tables.Row6
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.table

abstract class AbstractDataSpec(body: AbstractDataSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  operator fun <A> String.invoke(vararg rows: Row1<A>, testfn: (A) -> Unit) {
    table(headers("a"), *rows).forAll { a -> testfn(a) }
  }

  operator fun <A, B> String.invoke(vararg rows: Row2<A, B>, testfn: (A, B) -> Unit) {
    table(headers("a", "b"), *rows).forAll { a, b -> testfn(a, b) }
  }

  operator fun <A, B, C> String.invoke(vararg rows: Row3<A, B, C>, testfn: (A, B, C) -> Unit) {
    table(headers("a", "b", "c"), *rows).forAll { a, b, c -> testfn(a, b, c) }
  }

  operator fun <A, B, C, D> String.invoke(vararg rows: Row4<A, B, C, D>, testfn: (A, B, C, D) -> Unit) {
    table(headers("a", "b", "c", "d"), *rows).forAll { a, b, c, d -> testfn(a, b, c, d) }
  }

  operator fun <A, B, C, D, E> String.invoke(vararg rows: Row5<A, B, C, D, E>, testfn: (A, B, C, D, E) -> Unit) {
    table(headers("a", "b", "c", "d", "e"), *rows).forAll { a, b, c, d, e -> testfn(a, b, c, d, e) }
  }

  operator fun <A, B, C, D, E, F> String.invoke(vararg rows: Row6<A, B, C, D, E, F>, testfn: (A, B, C, D, E, F) -> Unit) {
    table(headers("a", "b", "c", "d", "e", "f"), *rows).forAll { a, b, c, d, e, f -> testfn(a, b, c, d, e, f) }
  }
}