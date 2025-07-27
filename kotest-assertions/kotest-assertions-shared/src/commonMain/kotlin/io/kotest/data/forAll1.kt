package io.kotest.data

import io.kotest.common.reflection.reflection
import kotlin.jvm.JvmName

suspend fun <A> forAll(vararg rows: Row1<A>, testfn: suspend (A) -> Unit) {
  val params = reflection.paramNames(testfn) ?: emptyList<String>()
  val paramA = params.getOrElse(0) { "a" }
  table(headers(paramA), *rows).forAll { a -> testfn(a) }
}

@JvmName("forall1")
inline fun <A> forAll(table: Table1<A>, testfn: (A) -> Unit) = table.forAll(testfn)

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

suspend fun <A> forNone(vararg rows: Row1<A>, testfn: suspend (A) -> Unit) {
  val params = reflection.paramNames(testfn) ?: emptyList<String>()
  val paramA = params.getOrElse(0) { "a" }
  table(headers(paramA), *rows).forNone { a -> testfn(a) }
}

@JvmName("fornone1")
inline fun <A> forNone(table: Table1<A>, testfn: (A) -> Unit) = table.forNone(testfn)

inline fun <A> Table1<A>.forNone(fn: (A) -> Unit) {
  for (row in rows) {
    try {
      fn(row.a)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(headers.values(), row.values())
  }
}
