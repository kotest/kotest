package io.kotest.data

import kotlin.jvm.JvmName

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C, D, E> forAll(vararg rows: Row5<A, B, C, D, E>, testfn: suspend (A, B, C, D, E) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   table(headers(paramA, paramB, paramC, paramD, paramE), *rows).forAll { A, B, C, D, E -> testfn(A, B, C, D, E) }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("forall5")
inline fun <A, B, C, D, E> forAll(table: Table5<A, B, C, D, E>, testfn: (A, B, C, D, E) -> Unit) = table.forAll(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A, B, C, D, E> Table5<A, B, C, D, E>.forAll(fn: (A, B, C, D, E) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
suspend fun <A, B, C, D, E> forNone(vararg rows: Row5<A, B, C, D, E>, testfn: suspend (A, B, C, D, E) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   table(headers(paramA, paramB, paramC, paramD, paramE), *rows).forNone { A, B, C, D, E -> testfn(A, B, C, D, E) }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@JvmName("fornone5")
inline fun <A, B, C, D, E> forNone(table: Table5<A, B, C, D, E>, testfn: (A, B, C, D, E) -> Unit) =
   table.forNone(testfn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
inline fun <A, B, C, D, E> Table5<A, B, C, D, E>.forNone(fn: (A, B, C, D, E) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
