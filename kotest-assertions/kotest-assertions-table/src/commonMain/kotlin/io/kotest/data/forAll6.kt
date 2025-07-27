package io.kotest.data

import io.kotest.common.reflection.reflection
import kotlin.jvm.JvmName

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C, D, E, F> forAll(vararg rows: Row6<A, B, C, D, E, F>, testfn: suspend (A, B, C, D, E, F) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF), *rows).forAll { A, B, C, D, E, F ->
      testfn(A, B, C, D, E, F)
   }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("forall6")
inline fun <A, B, C, D, E, F> forAll(table: Table6<A, B, C, D, E, F>, testfn: (A, B, C, D, E, F) -> Unit) =
   table.forAll(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A, B, C, D, E, F> Table6<A, B, C, D, E, F>.forAll(fn: (A, B, C, D, E, F) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C, D, E, F> forNone(
   vararg rows: Row6<A, B, C, D, E, F>,
   testfn: suspend (A, B, C, D, E, F) -> Unit
) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF), *rows).forNone { A, B, C, D, E, F ->
      testfn(A, B, C, D, E, F)
   }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("fornone6")
inline fun <A, B, C, D, E, F> forNone(table: Table6<A, B, C, D, E, F>, testfn: (A, B, C, D, E, F) -> Unit) =
   table.forNone(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A, B, C, D, E, F> Table6<A, B, C, D, E, F>.forNone(fn: (A, B, C, D, E, F) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
