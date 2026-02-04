@file:Suppress("DEPRECATION")

package io.kotest.data

import kotlin.jvm.JvmName

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forAll(
   vararg rows: Row11<A, B, C, D, E, F, G, H, I, J, K>,
   testfn: suspend (A, B, C, D, E, F, G, H, I, J, K) -> Unit
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
   table(
      headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK),
      *rows
   ).forAll { A, B, C, D, E, F, G, H, I, J, K ->
      testfn(A, B, C, D, E, F, G, H, I, J, K)
   }
}

@JvmName("forall11")
inline fun <A, B, C, D, E, F, G, H, I, J, K> forAll(table: Table11<A, B, C, D, E, F, G, H, I, J, K>, testfn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit) =
   table.forAll(testfn)

inline fun <A, B, C, D, E, F, G, H, I, J, K> Table11<A, B, C, D, E, F, G, H, I, J, K>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forNone(
   vararg rows: Row11<A, B, C, D, E, F, G, H, I, J, K>,
   testfn: suspend (A, B, C, D, E, F, G, H, I, J, K) -> Unit
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
   table(
      headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK),
      *rows
   ).forNone { A, B, C, D, E, F, G, H, I, J, K ->
      testfn(A, B, C, D, E, F, G, H, I, J, K)
   }
}

@JvmName("fornone11")
inline fun <A, B, C, D, E, F, G, H, I, J, K> forNone(table: Table11<A, B, C, D, E, F, G, H, I, J, K>, testfn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit) =
   table.forNone(testfn)

inline fun <A, B, C, D, E, F, G, H, I, J, K> Table11<A, B, C, D, E, F, G, H, I, J, K>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}