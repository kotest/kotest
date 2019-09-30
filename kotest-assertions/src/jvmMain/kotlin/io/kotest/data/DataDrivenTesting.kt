package io.kotest.data

import io.kotest.tables.Row1
import io.kotest.tables.Row10
import io.kotest.tables.Row2
import io.kotest.tables.Row3
import io.kotest.tables.Row4
import io.kotest.tables.Row5
import io.kotest.tables.Row6
import io.kotest.tables.Row7
import io.kotest.tables.Row8
import io.kotest.tables.Row9
import io.kotest.tables.forAll
import io.kotest.tables.headers
import io.kotest.tables.table
import kotlin.reflect.jvm.reflect


fun <A> forall(vararg rows: Row1<A>, testfn: (A) -> Unit) {
  val params = testfn.paramNames
  forall1(params, rows, testfn)
}

internal inline fun <A> forall1(params: List<String>, rows: Array<out Row1<A>>, testfn: (A) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  table(headers(paramA), *rows).forAll { a -> testfn(a) }
}


fun <A, B> forall(vararg rows: Row2<A, B>, testfn: (A, B) -> Unit) {
  val params = testfn.paramNames
  forall2(params, rows, testfn)
}

internal inline fun <A, B> forall2(params: List<String>, rows: Array<out Row2<A, B>>, testfn: (A, B) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  val paramB = params.getOrElse(1) { "b" }
  table(headers(paramA, paramB), *rows).forAll { a, b -> testfn(a, b) }
}


fun <A, B, C> forall(vararg rows: Row3<A, B, C>, testfn: (A, B, C) -> Unit) {
  val params = testfn.paramNames
  forall3(params, rows, testfn)
}

internal inline fun <A, B, C> forall3(params: List<String>, rows: Array<out Row3<A, B, C>>, testfn: (A, B, C) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  val paramB = params.getOrElse(1) { "b" }
  val paramC = params.getOrElse(2) { "c" }
  table(headers(paramA, paramB, paramC), *rows).forAll { a, b, c -> testfn(a, b, c) }
}


fun <A, B, C, D> forall(vararg rows: Row4<A, B, C, D>,
                        testfn: (A, B, C, D) -> Unit) {
  val params = testfn.paramNames
  forall4(params, rows, testfn)
}

internal inline fun <A, B, C, D> forall4(params: List<String>, rows: Array<out Row4<A, B, C, D>>, testfn: (A, B, C, D) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  val paramB = params.getOrElse(1) { "b" }
  val paramC = params.getOrElse(2) { "c" }
  val paramD = params.getOrElse(3) { "d" }
  table(headers(paramA, paramB, paramC, paramD), *rows).forAll { a, b, c, d -> testfn(a, b, c, d) }
}


fun <A, B, C, D, E> forall(vararg rows: Row5<A, B, C, D, E>,
                           testfn: (A, B, C, D, E) -> Unit) {
  val params = testfn.paramNames
  forall5(params, rows, testfn)
}

internal inline fun <A, B, C, D, E> forall5(params: List<String>, rows: Array<out Row5<A, B, C, D, E>>, testfn: (A, B, C, D, E) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  val paramB = params.getOrElse(1) { "b" }
  val paramC = params.getOrElse(2) { "c" }
  val paramD = params.getOrElse(3) { "d" }
  val paramE = params.getOrElse(4) { "e" }
  table(headers(paramA, paramB, paramC, paramD, paramE), *rows).forAll { a, b, c, d, e -> testfn(a, b, c, d, e) }
}


fun <A, B, C, D, E, F> forall(vararg rows: Row6<A, B, C, D, E, F>,
                              testfn: (A, B, C, D, E, F) -> Unit) {
  val params = testfn.paramNames
  forall6(params, rows, testfn)
}

internal inline fun <A, B, C, D, E, F> forall6(params: List<String>, rows: Array<out Row6<A, B, C, D, E, F>>, testfn: (A, B, C, D, E, F) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  val paramB = params.getOrElse(1) { "b" }
  val paramC = params.getOrElse(2) { "c" }
  val paramD = params.getOrElse(3) { "d" }
  val paramE = params.getOrElse(4) { "e" }
  val paramF = params.getOrElse(5) { "f" }
  table(headers(paramA, paramB, paramC, paramD, paramE, paramF), *rows).forAll { a, b, c, d, e, f -> testfn(a, b, c, d, e, f) }
}


fun <A, B, C, D, E, F, G> forall(vararg rows: Row7<A, B, C, D, E, F, G>,
                                 testfn: (A, B, C, D, E, F, G) -> Unit) {
  val params = testfn.paramNames
  forall7(params, rows, testfn)
}

internal inline fun <A, B, C, D, E, F, G> forall7(params: List<String>, rows: Array<out Row7<A, B, C, D, E, F, G>>, testfn: (A, B, C, D, E, F, G) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  val paramB = params.getOrElse(1) { "b" }
  val paramC = params.getOrElse(2) { "c" }
  val paramD = params.getOrElse(3) { "d" }
  val paramE = params.getOrElse(4) { "e" }
  val paramF = params.getOrElse(5) { "f" }
  val paramG = params.getOrElse(6) { "g" }
  table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG), *rows).forAll { a, b, c, d, e, f, g -> testfn(a, b, c, d, e, f, g) }
}


fun <A, B, C, D, E, F, G, H> forall(vararg rows: Row8<A, B, C, D, E, F, G, H>,
                                    testfn: (A, B, C, D, E, F, G, H) -> Unit) {
  val params = testfn.paramNames
  forall8(params, rows, testfn)
}

internal inline fun <A, B, C, D, E, F, G, H> forall8(params: List<String>, rows: Array<out Row8<A, B, C, D, E, F, G, H>>, testfn: (A, B, C, D, E, F, G, H) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  val paramB = params.getOrElse(1) { "b" }
  val paramC = params.getOrElse(2) { "c" }
  val paramD = params.getOrElse(3) { "d" }
  val paramE = params.getOrElse(4) { "e" }
  val paramF = params.getOrElse(5) { "f" }
  val paramG = params.getOrElse(6) { "g" }
  val paramH = params.getOrElse(7) { "h" }
  table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH), *rows).forAll { a, b, c, d, e, f, g, h -> testfn(a, b, c, d, e, f, g, h) }
}

fun <A, B, C, D, E, F, G, H, I> forall(vararg rows: Row9<A, B, C, D, E, F, G, H, I>,
                                       testfn: (A, B, C, D, E, F, G, H, I) -> Unit) {
  val params = testfn.paramNames
  forall9(params, rows, testfn)
}

internal inline fun <A, B, C, D, E, F, G, H, I> forall9(params: List<String>, rows: Array<out Row9<A, B, C, D, E, F, G, H, I>>, testfn: (A, B, C, D, E, F, G, H, I) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  val paramB = params.getOrElse(1) { "b" }
  val paramC = params.getOrElse(2) { "c" }
  val paramD = params.getOrElse(3) { "d" }
  val paramE = params.getOrElse(4) { "e" }
  val paramF = params.getOrElse(5) { "f" }
  val paramG = params.getOrElse(6) { "g" }
  val paramH = params.getOrElse(7) { "h" }
  val paramI = params.getOrElse(8) { "i" }
  table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI), *rows).forAll { a, b, c, d, e, f, g, h, i -> testfn(a, b, c, d, e, f, g, h, i) }
}

fun <A, B, C, D, E, F, G, H, I, J> forall(vararg rows: Row10<A, B, C, D, E, F, G, H, I, J>,
                                          testfn: (A, B, C, D, E, F, G, H, I, J) -> Unit) {
  val params = testfn.paramNames
  forall10(params, rows, testfn)
}

internal inline fun <A, B, C, D, E, F, G, H, I, J> forall10(params: List<String>, rows: Array<out Row10<A, B, C, D, E, F, G, H, I, J>>, testfn: (A, B, C, D, E, F, G, H, I, J) -> Unit) {
  val paramA = params.getOrElse(0) { "a" }
  val paramB = params.getOrElse(1) { "b" }
  val paramC = params.getOrElse(2) { "c" }
  val paramD = params.getOrElse(3) { "d" }
  val paramE = params.getOrElse(4) { "e" }
  val paramF = params.getOrElse(5) { "f" }
  val paramG = params.getOrElse(6) { "g" }
  val paramH = params.getOrElse(7) { "h" }
  val paramI = params.getOrElse(8) { "i" }
  val paramJ = params.getOrElse(9) { "j" }
  table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ), *rows).forAll { a, b, c, d, e, f, g, h, i, j -> testfn(a, b, c, d, e, f, g, h, i, j) }
}

internal val Function<*>.paramNames
  get() = reflect()?.parameters?.mapNotNull { it.name } ?: emptyList()