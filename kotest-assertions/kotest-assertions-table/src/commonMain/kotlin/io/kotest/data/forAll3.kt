@file:Suppress("DEPRECATION")

package io.kotest.data

import kotlin.jvm.JvmName

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C> forAll(vararg rows: Row3<A, B, C>, testfn: suspend (A, B, C) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   table(headers(paramA, paramB, paramC), *rows).forAll { A, B, C -> testfn(A, B, C) }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("forall3")
inline fun <A, B, C> forAll(table: Table3<A, B, C>, testfn: (A, B, C) -> Unit) = table.forAll(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A, B, C> Table3<A, B, C>.forAll(fn: (A, B, C) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C> forNone(vararg rows: Row3<A, B, C>, testfn: suspend (A, B, C) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   table(headers(paramA, paramB, paramC), *rows).forNone { A, B, C -> testfn(A, B, C) }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("fornone3")
inline fun <A, B, C> forNone(table: Table3<A, B, C>, testfn: (A, B, C) -> Unit) = table.forNone(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A, B, C> Table3<A, B, C>.forNone(fn: (A, B, C) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
