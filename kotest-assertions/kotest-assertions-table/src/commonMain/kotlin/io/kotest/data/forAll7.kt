package io.kotest.data

import kotlin.jvm.JvmName

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C, D, E, F, G> forAll(
   vararg rows: Row7<A, B, C, D, E, F, G>,
   testfn: suspend (A, B, C, D, E, F, G) -> Unit
) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG), *rows).forAll { A, B, C, D, E, F, G ->
      testfn(A, B, C, D, E, F, G)
   }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("forall7")
inline fun <A, B, C, D, E, F, G> forAll(table: Table7<A, B, C, D, E, F, G>, testfn: (A, B, C, D, E, F, G) -> Unit) =
   table.forAll(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A, B, C, D, E, F, G> Table7<A, B, C, D, E, F, G>.forAll(fn: (A, B, C, D, E, F, G) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C, D, E, F, G> forNone(
   vararg rows: Row7<A, B, C, D, E, F, G>,
   testfn: suspend (A, B, C, D, E, F, G) -> Unit
) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG), *rows).forNone { A, B, C, D, E, F, G ->
      testfn(A, B, C, D, E, F, G)
   }
}

@JvmName("fornone7")
inline fun <A, B, C, D, E, F, G> forNone(table: Table7<A, B, C, D, E, F, G>, testfn: (A, B, C, D, E, F, G) -> Unit) =
   table.forNone(testfn)

inline fun <A, B, C, D, E, F, G> Table7<A, B, C, D, E, F, G>.forNone(fn: (A, B, C, D, E, F, G) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
