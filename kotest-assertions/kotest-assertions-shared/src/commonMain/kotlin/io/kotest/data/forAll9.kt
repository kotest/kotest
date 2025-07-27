package io.kotest.data

import io.kotest.common.reflection.reflection
import kotlin.jvm.JvmName

suspend fun <A, B, C, D, E, F, G, H, I> forAll(
   vararg rows: Row9<A, B, C, D, E, F, G, H, I>,
   testfn: suspend (A, B, C, D, E, F, G, H, I) -> Unit
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
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI), *rows).forAll { A, B, C, D, E, F, G, H, I ->
      testfn(A, B, C, D, E, F, G, H, I)
   }
}

@JvmName("forall8")
inline fun <A, B, C, D, E, F, G, H, I> forAll(table: Table9<A, B, C, D, E, F, G, H, I>, testfn: (A, B, C, D, E, F, G, H, I) -> Unit) =
   table.forAll(testfn)

inline fun <A, B, C, D, E, F, G, H, I> Table9<A, B, C, D, E, F, G, H, I>.forAll(fn: (A, B, C, D, E, F, G, H, I) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

suspend fun <A, B, C, D, E, F, G, H, I> forNone(
   vararg rows: Row9<A, B, C, D, E, F, G, H, I>,
   testfn: suspend (A, B, C, D, E, F, G, H, I) -> Unit
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
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI), *rows).forNone { A, B, C, D, E, F, G, H, I ->
      testfn(A, B, C, D, E, F, G, H, I)
   }
}

@JvmName("fornone8")
inline fun <A, B, C, D, E, F, G, H, I> forNone(table: Table9<A, B, C, D, E, F, G, H, I>, testfn: (A, B, C, D, E, F, G, H, I) -> Unit) =
   table.forNone(testfn)

inline fun <A, B, C, D, E, F, G, H, I> Table9<A, B, C, D, E, F, G, H, I>.forNone(fn: (A, B, C, D, E, F, G, H, I) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
