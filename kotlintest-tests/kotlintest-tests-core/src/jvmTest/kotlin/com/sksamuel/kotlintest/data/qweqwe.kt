package com.sksamuel.kotlintest.data

import arrow.core.Tuple3
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import io.kotlintest.tables.Row3
import io.kotlintest.tables.row
import kotlin.math.max

fun <A, B, C> expect(f: (Tuple3<A, B, C>) -> Unit, where: ExpectingWhere3<A, B, C>): Nothing = TODO()

fun <A, B, C> expectb(f: (Tuple3<A, B, C>) -> Boolean, vararg f2: () -> Row3<A, B, C>): Nothing = TODO()

fun <A, B, C> expect(f: (Tuple3<A, B, C>) -> Unit, vararg rows: Row3<A, B, C>): Nothing = TODO()

fun <A, B, C> where(vararg rows: Row3<A, B, C>): ExpectingWhere3<A, B, C> = TODO()

interface ExpectingWhere3<A, B, C> {
  fun where(vararg rows: Row3<A, B, C>) {}
}

fun <A, B, C> expect(f: (Tuple3<A, B, C>) -> Unit): ExpectingWhere3<A, B, C> = TODO()

class MyTest : FunSpec({

  test("qq") {
    expect<Int, Int, Int> {
      max(it.a, it.c) shouldBe it.c
    }.where(
      row(1, 2, 3),
      row(4, 5, 9),
      row(7, 0, 7)
    )
  }

  test("qq") {
    expect(
      { max(it.a, it.c) shouldBe it.c },
      row(1, 2, 3),
      row(4, 5, 9),
      row(7, 0, 7)
    )
  }

  test("qq") {
    expectb(
      { max(it.a, it.c) == it.c },
      { row(1, 2, 3) },
      { row(1, 2, 3) },
      { row(1, 2, 3) },
      { row(1, 2, 3) }
    )
  }
})
