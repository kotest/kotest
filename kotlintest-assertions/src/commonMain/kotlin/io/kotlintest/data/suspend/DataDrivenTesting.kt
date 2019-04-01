package io.kotlintest.data.suspend

import io.kotlintest.data.forall1
import io.kotlintest.data.forall10
import io.kotlintest.data.forall2
import io.kotlintest.data.forall3
import io.kotlintest.data.forall4
import io.kotlintest.data.forall5
import io.kotlintest.data.forall6
import io.kotlintest.data.forall7
import io.kotlintest.data.forall8
import io.kotlintest.data.forall9
import io.kotlintest.data.paramNames
import io.kotlintest.tables.Row1
import io.kotlintest.tables.Row10
import io.kotlintest.tables.Row2
import io.kotlintest.tables.Row3
import io.kotlintest.tables.Row4
import io.kotlintest.tables.Row5
import io.kotlintest.tables.Row6
import io.kotlintest.tables.Row7
import io.kotlintest.tables.Row8
import io.kotlintest.tables.Row9

suspend fun <A> forall(vararg rows: Row1<A>, testfn: suspend (A) -> Unit) {
  val params = testfn.paramNames
  forall1(params, rows) { testfn(it) }
}

suspend fun <A, B> forall(vararg rows: Row2<A, B>, testfn: suspend (A, B) -> Unit) {
  val params = testfn.paramNames
  forall2(params, rows) { a, b -> testfn(a, b) }
}

suspend fun <A, B, C> forall(vararg rows: Row3<A, B, C>, testfn: suspend (A, B, C) -> Unit) {
  val params = testfn.paramNames
  forall3(params, rows) { a, b, c -> testfn(a, b, c) }
}

suspend fun <A, B, C, D> forall(vararg rows: Row4<A, B, C, D>,
                                testfn: suspend (A, B, C, D) -> Unit) {
  val params = testfn.paramNames
  forall4(params, rows) { a, b, c, d -> testfn(a, b, c, d) }
}

suspend fun <A, B, C, D, E> forall(vararg rows: Row5<A, B, C, D, E>,
                                   testfn: suspend (A, B, C, D, E) -> Unit) {
  val params = testfn.paramNames
  forall5(params, rows) { a, b, c, d, e -> testfn(a, b, c, d, e) }
}

suspend fun <A, B, C, D, E, F> forall(vararg rows: Row6<A, B, C, D, E, F>,
                                      testfn: suspend (A, B, C, D, E, F) -> Unit) {
  val params = testfn.paramNames
  forall6(params, rows) { a, b, c, d, e, f -> testfn(a, b, c, d, e, f) }
}

suspend fun <A, B, C, D, E, F, G> forall(vararg rows: Row7<A, B, C, D, E, F, G>,
                                         testfn: suspend (A, B, C, D, E, F, G) -> Unit) {
  val params = testfn.paramNames
  forall7(params, rows) { a, b, c, d, e, f, g -> testfn(a, b, c, d, e, f, g) }
}

suspend fun <A, B, C, D, E, F, G, H> forall(vararg rows: Row8<A, B, C, D, E, F, G, H>,
                                            testfn: suspend (A, B, C, D, E, F, G, H) -> Unit) {
  val params = testfn.paramNames
  forall8(params, rows) { a, b, c, d, e, f, g, h -> testfn(a, b, c, d, e, f, g, h) }
}

suspend fun <A, B, C, D, E, F, G, H, I> forall(vararg rows: Row9<A, B, C, D, E, F, G, H, I>,
                                               testfn: suspend (A, B, C, D, E, F, G, H, I) -> Unit) {
  val params = testfn.paramNames
  forall9(params, rows) { a, b, c, d, e, f, g, h, i -> testfn(a, b, c, d, e, f, g, h, i) }
}

suspend fun <A, B, C, D, E, F, G, H, I, J> forall(vararg rows: Row10<A, B, C, D, E, F, G, H, I, J>,
                                                  testfn: suspend (A, B, C, D, E, F, G, H, I, J) -> Unit) {
  val params = testfn.paramNames
  forall10(params, rows) { a, b, c, d, e, f, g, h, i, j -> testfn(a, b, c, d, e, f, g, h, i, j) }
}