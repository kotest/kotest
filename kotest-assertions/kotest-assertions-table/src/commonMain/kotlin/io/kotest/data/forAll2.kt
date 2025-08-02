@file:Suppress("DEPRECATION")

package io.kotest.data

import kotlin.jvm.JvmName

suspend fun <A, B> forAll(vararg rows: Row2<A, B>, testfn: suspend (A, B) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   table(headers(paramA, paramB), *rows).forAll { a, b -> testfn(a, b) }
}

@JvmName("forall2")
inline fun <A, B> forAll(table: Table2<A, B>, testfn: (A, B) -> Unit) = table.forAll(testfn)

inline fun <A, B> Table2<A, B>.forAll(fn: (A, B) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

suspend fun <A, B> forNone(vararg rows: Row2<A, B>, testfn: suspend (A, B) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   table(headers(paramA, paramB), *rows).forNone { a, b -> testfn(a, b) }
}

@JvmName("fornone2")
inline fun <A, B> forNone(table: Table2<A, B>, testfn: (A, B) -> Unit) = table.forNone(testfn)

inline fun <A, B> Table2<A, B>.forNone(fn: (A, B) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
