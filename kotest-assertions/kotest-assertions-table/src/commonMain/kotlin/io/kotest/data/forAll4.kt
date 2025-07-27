package io.kotest.data

import io.kotest.common.reflection.reflection
import kotlin.jvm.JvmName

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C, D> forAll(vararg rows: Row4<A, B, C, D>, testfn: suspend (A, B, C, D) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   table(headers(paramA, paramB, paramC, paramD), *rows).forAll { A, B, C, D -> testfn(A, B, C, D) }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("forall4")
inline fun <A, B, C, D> forAll(table: Table4<A, B, C, D>, testfn: (A, B, C, D) -> Unit) = table.forAll(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A, B, C, D> Table4<A, B, C, D>.forAll(fn: (A, B, C, D) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C, D> forNone(vararg rows: Row4<A, B, C, D>, testfn: suspend (A, B, C, D) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   table(headers(paramA, paramB, paramC, paramD), *rows).forNone { A, B, C, D -> testfn(A, B, C, D) }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("fornone4")
inline fun <A, B, C, D> forNone(table: Table4<A, B, C, D>, testfn: (A, B, C, D) -> Unit) = table.forNone(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A, B, C, D> Table4<A, B, C, D>.forNone(fn: (A, B, C, D) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
