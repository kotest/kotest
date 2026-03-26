@file:Suppress("DEPRECATION")

package io.kotest.data

import kotlin.jvm.JvmName

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forAll(
   vararg rows: Row13<A, B, C, D, E, F, G, H, I, J, K, L, M>,
   testfn: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
) {
   val params = paramNames(testfn)
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
   val paramK = params.getOrElse(10) { "k" }
   val paramL = params.getOrElse(11) { "l" }
   val paramM = params.getOrElse(12) { "m" }
   table(
      headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK, paramL, paramM),
      *rows
   ).forAll { A, B, C, D, E, F, G, H, I, J, K, L, M ->
      testfn(A, B, C, D, E, F, G, H, I, J, K, L, M)
   }
}

@JvmName("forall13")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forAll(table: Table13<A, B, C, D, E, F, G, H, I, J, K, L, M>, testfn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit) =
   table.forAll(testfn)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Table13<A, B, C, D, E, F, G, H, I, J, K, L, M>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forNone(
   vararg rows: Row13<A, B, C, D, E, F, G, H, I, J, K, L, M>,
   testfn: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
) {
   val params = paramNames(testfn)
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
   val paramK = params.getOrElse(10) { "k" }
   val paramL = params.getOrElse(11) { "l" }
   val paramM = params.getOrElse(12) { "m" }
   table(
      headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK, paramL, paramM),
      *rows
   ).forNone { A, B, C, D, E, F, G, H, I, J, K, L, M ->
      testfn(A, B, C, D, E, F, G, H, I, J, K, L, M)
   }
}

@JvmName("fornone13")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forNone(table: Table13<A, B, C, D, E, F, G, H, I, J, K, L, M>, testfn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit) =
   table.forNone(testfn)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Table13<A, B, C, D, E, F, G, H, I, J, K, L, M>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
