package io.kotest.data

import io.kotest.mpp.reflection
import kotlin.jvm.JvmName

suspend fun <A, B, C, D, E, F, G, H, I, J> forAll(
   vararg rows: Row10<A, B, C, D, E, F, G, H, I, J>,
   testfn: suspend (A, B, C, D, E, F, G, H, I, J) -> Unit
) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
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
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ), *rows).forAll { A, B, C, D, E, F, G, H, I, J ->
      testfn(A, B, C, D, E, F, G, H, I, J)
   }
}

@JvmName("forall8")
inline fun <A, B, C, D, E, F, G, H, I, J> forAll(table: Table10<A, B, C, D, E, F, G, H, I, J>, testfn: (A, B, C, D, E, F, G, H, I, J) -> Unit) =
   table.forAll(testfn)

inline fun <A, B, C, D, E, F, G, H, I, J> Table10<A, B, C, D, E, F, G, H, I, J>.forAll(fn: (A, B, C, D, E, F, G, H, I, J) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

suspend fun <A, B, C, D, E, F, G, H, I, J> forNone(
   vararg rows: Row10<A, B, C, D, E, F, G, H, I, J>,
   testfn: suspend (A, B, C, D, E, F, G, H, I, J) -> Unit
) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
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
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ), *rows).forNone { A, B, C, D, E, F, G, H, I, J ->
      testfn(A, B, C, D, E, F, G, H, I, J)
   }
}

@JvmName("fornone8")
inline fun <A, B, C, D, E, F, G, H, I, J> forNone(table: Table10<A, B, C, D, E, F, G, H, I, J>, testfn: (A, B, C, D, E, F, G, H, I, J) -> Unit) =
   table.forNone(testfn)

inline fun <A, B, C, D, E, F, G, H, I, J> Table10<A, B, C, D, E, F, G, H, I, J>.forNone(fn: (A, B, C, D, E, F, G, H, I, J) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
