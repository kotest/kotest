@file:Suppress("DEPRECATION")

package io.kotest.data

import kotlin.jvm.JvmName

suspend fun <A, B, C, D, E, F, G, H> forAll(
   vararg rows: Row8<A, B, C, D, E, F, G, H>,
   testfn: suspend (A, B, C, D, E, F, G, H) -> Unit
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
   table(
      headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH),
      *rows
   ).forAll { A, B, C, D, E, F, G, H ->
      testfn(A, B, C, D, E, F, G, H)
   }
}

@JvmName("forall8")
inline fun <A, B, C, D, E, F, G, H> forAll(
   table: Table8<A, B, C, D, E, F, G, H>,
   testfn: (A, B, C, D, E, F, G, H) -> Unit
) =
   table.forAll(testfn)

inline fun <A, B, C, D, E, F, G, H> Table8<A, B, C, D, E, F, G, H>.forAll(fn: (A, B, C, D, E, F, G, H) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

suspend fun <A, B, C, D, E, F, G, H> forNone(
   vararg rows: Row8<A, B, C, D, E, F, G, H>,
   testfn: suspend (A, B, C, D, E, F, G, H) -> Unit
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
   table(
      headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH),
      *rows
   ).forNone { A, B, C, D, E, F, G, H ->
      testfn(A, B, C, D, E, F, G, H)
   }
}

@JvmName("fornone8")
inline fun <A, B, C, D, E, F, G, H> forNone(
   table: Table8<A, B, C, D, E, F, G, H>,
   testfn: (A, B, C, D, E, F, G, H) -> Unit
) =
   table.forNone(testfn)

inline fun <A, B, C, D, E, F, G, H> Table8<A, B, C, D, E, F, G, H>.forNone(fn: (A, B, C, D, E, F, G, H) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
