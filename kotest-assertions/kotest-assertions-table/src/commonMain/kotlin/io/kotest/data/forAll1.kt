@file:Suppress("DEPRECATION")

package io.kotest.data

import kotlin.jvm.JvmName

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A> forAll(vararg rows: Row1<A>, testfn: suspend (A) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   table(headers(paramA), *rows).forAll { a -> testfn(a) }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("forall1")
inline fun <A> forAll(table: Table1<A>, testfn: (A) -> Unit) = table.forAll(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A> Table1<A>.forAll(fn: (A) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A> forNone(vararg rows: Row1<A>, testfn: suspend (A) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   table(headers(paramA), *rows).forNone { a -> testfn(a) }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("fornone1")
inline fun <A> forNone(table: Table1<A>, testfn: (A) -> Unit) = table.forNone(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A> Table1<A>.forNone(fn: (A) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a)
      } catch (_: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
