@file:JvmName("TableTestingKt")
package io.kotest.tables

import io.kotest.assertions.Failures
import io.kotest.assertions.MultiAssertionError

fun headers(a: String) = Headers1(a)
fun headers(a: String, b: String) = Headers2(a, b)
fun headers(a: String, b: String, c: String) = Headers3(a, b, c)
fun headers(a: String, b: String, c: String, d: String) = Headers4(a, b, c, d)
fun headers(a: String, b: String, c: String, d: String, e: String) = Headers5(a, b, c, d, e)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String) = Headers6(a, b, c, d, e, f)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String) = Headers7(a, b, c, d, e, f, g)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String) = Headers8(a, b, c, d, e, f, g, h)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String) = Headers9(a, b, c, d, e, f, g, h, i)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String) = Headers10(a, b, c, d, e, f, g, h, i, j)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String) = Headers11(a, b, c, d, e, f, g, h, i, j, k)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String) = Headers12(a, b, c, d, e, f, g, h, i, j, k, l)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String) = Headers13(a, b, c, d, e, f, g, h, i, j, k, l, m)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String) = Headers14(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String) = Headers15(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String) = Headers16(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String) = Headers17(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String) = Headers18(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String, s: String) = Headers19(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String, s: String, t: String) = Headers20(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String, s: String, t: String, u: String) = Headers21(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String, s: String, t: String, u: String, v: String) = Headers22(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)


fun <A> row(a: A) = Row1(a)
fun <A, B> row(a: A, b: B) = Row2(a, b)
fun <A, B, C> row(a: A, b: B, c: C) = Row3(a, b, c)
fun <A, B, C, D> row(a: A, b: B, c: C, d: D) = Row4(a, b, c, d)
fun <A, B, C, D, E> row(a: A, b: B, c: C, d: D, e: E) = Row5(a, b, c, d, e)
fun <A, B, C, D, E, F> row(a: A, b: B, c: C, d: D, e: E, f: F) = Row6(a, b, c, d, e, f)
fun <A, B, C, D, E, F, G> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G) = Row7(a, b, c, d, e, f, g)
fun <A, B, C, D, E, F, G, H> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H) = Row8(a, b, c, d, e, f, g, h)
fun <A, B, C, D, E, F, G, H, I> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I) = Row9(a, b, c, d, e, f, g, h, i)
fun <A, B, C, D, E, F, G, H, I, J> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J) = Row10(a, b, c, d, e, f, g, h, i, j)
fun <A, B, C, D, E, F, G, H, I, J, K> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K) = Row11(a, b, c, d, e, f, g, h, i, j, k)
fun <A, B, C, D, E, F, G, H, I, J, K, L> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L) = Row12(a, b, c, d, e, f, g, h, i, j, k, l)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M) = Row13(a, b, c, d, e, f, g, h, i, j, k, l, m)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N) = Row14(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O) = Row15(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P) = Row16(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q) = Row17(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R) = Row18(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S) = Row19(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T) = Row20(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U) = Row21(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V) = Row22(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)


fun <A> table(headers: Headers1, vararg rows: Row1<A>) = Table1(headers, rows.asList())
fun <A, B> table(headers: Headers2, vararg rows: Row2<A, B>) = Table2(headers, rows.asList())
fun <A, B, C> table(headers: Headers3, vararg rows: Row3<A, B, C>) = Table3(headers, rows.asList())
fun <A, B, C, D> table(headers: Headers4, vararg rows: Row4<A, B, C, D>) = Table4(headers, rows.asList())
fun <A, B, C, D, E> table(headers: Headers5, vararg rows: Row5<A, B, C, D, E>) = Table5(headers, rows.asList())
fun <A, B, C, D, E, F> table(headers: Headers6, vararg rows: Row6<A, B, C, D, E, F>) = Table6(headers, rows.asList())
fun <A, B, C, D, E, F, G> table(headers: Headers7, vararg rows: Row7<A, B, C, D, E, F, G>) = Table7(headers, rows.asList())
fun <A, B, C, D, E, F, G, H> table(headers: Headers8, vararg rows: Row8<A, B, C, D, E, F, G, H>) = Table8(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I> table(headers: Headers9, vararg rows: Row9<A, B, C, D, E, F, G, H, I>) = Table9(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J> table(headers: Headers10, vararg rows: Row10<A, B, C, D, E, F, G, H, I, J>) = Table10(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K> table(headers: Headers11, vararg rows: Row11<A, B, C, D, E, F, G, H, I, J, K>) = Table11(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L> table(headers: Headers12, vararg rows: Row12<A, B, C, D, E, F, G, H, I, J, K, L>) = Table12(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> table(headers: Headers13, vararg rows: Row13<A, B, C, D, E, F, G, H, I, J, K, L, M>) = Table13(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> table(headers: Headers14, vararg rows: Row14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>) = Table14(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> table(headers: Headers15, vararg rows: Row15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>) = Table15(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> table(headers: Headers16, vararg rows: Row16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>) = Table16(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> table(headers: Headers17, vararg rows: Row17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>) = Table17(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> table(headers: Headers18, vararg rows: Row18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>) = Table18(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> table(headers: Headers19, vararg rows: Row19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>) = Table19(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> table(headers: Headers20, vararg rows: Row20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>) = Table20(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> table(headers: Headers21, vararg rows: Row21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>) = Table21(headers, rows.asList())
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> table(headers: Headers22, vararg rows: Row22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>) = Table22(headers, rows.asList())

@PublishedApi
internal fun error(e: Throwable, headers: List<String>, values: List<*>): AssertionError {
  val params = headers.zip(values).joinToString(", ")
  // Include class name for non-assertion errors, since the class is often meaningful and there might not
  // be a message (e.g. NullPointerException)
  val message = when (e) {
    is AssertionError -> e.message
    else -> e.toString()
  }

  return Failures.failure("Test failed for $params with error $message")
}

@PublishedApi
internal fun forNoneError(headers: List<String>, values: List<*>): AssertionError {
  val params = headers.zip(values).joinToString(", ")
  return Failures.failure("Test passed for $params but expected failure")
}

@PublishedApi
internal class ErrorCollector {
  private val errors = mutableListOf<Throwable>()

  operator fun plusAssign(t: Throwable) {
    errors += t
  }

  fun assertAll() {
    if (errors.size == 1) {
      throw errors[0]
    } else if (errors.size > 1) {
      throw MultiAssertionError(errors)
    }
  }
}

inline fun <A> forAll(table: Table1<A>, fn: (A) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B> forAll(table: Table2<A, B>, fn: (A, B) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C> forAll(table: Table3<A, B, C>, fn: (A, B, C) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D> forAll(table: Table4<A, B, C, D>, fn: (A, B, C, D) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E> forAll(table: Table5<A, B, C, D, E>, fn: (A, B, C, D, E) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}


inline fun <A, B, C, D, E, F> forAll(table: Table6<A, B, C, D, E, F>, fn: (A, B, C, D, E, F) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G> forAll(table: Table7<A, B, C, D, E, F, G>, fn: (A, B, C, D, E, F, G) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H> forAll(table: Table8<A, B, C, D, E, F, G, H>, fn: (A, B, C, D, E, F, G, H) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I> forAll(table: Table9<A, B, C, D, E, F, G, H, I>, fn: (A, B, C, D, E, F, G, H, I) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J> forAll(table: Table10<A, B, C, D, E, F, G, H, I, J>, fn: (A, B, C, D, E, F, G, H, I, J) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K> forAll(table: Table11<A, B, C, D, E, F, G, H, I, J, K>, fn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L> forAll(table: Table12<A, B, C, D, E, F, G, H, I, J, K, L>, fn: (A, B, C, D, E, F, G, H, I, J, K, L) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forAll(table: Table13<A, B, C, D, E, F, G, H, I, J, K, L, M>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> forAll(table: Table14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> forAll(table: Table15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> forAll(table: Table16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> forAll(table: Table17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> forAll(table: Table18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> forAll(table: Table19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> forAll(table: Table20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s, row.t)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> forAll(table: Table21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s, row.t, row.u)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> forAll(table: Table22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) -> Unit) {
  val collector = ErrorCollector()
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s, row.t, row.u, row.v)
    } catch (e: Throwable) {
      collector += error(e, table.headers.values(), row.values())
    }
  }
  collector.assertAll()
}

@JvmName("forAll1receiver") inline fun <A> Table1<A>.forAll(fn: (A) -> Unit) = forAll(this, fn)
@JvmName("forAll2receiver") inline fun <A, B> Table2<A, B>.forAll(fn: (A, B) -> Unit) = forAll(this, fn)
@JvmName("forAll3receiver") inline fun <A, B, C> Table3<A, B, C>.forAll(fn: (A, B, C) -> Unit) = forAll(this, fn)
@JvmName("forAll4receiver") inline fun <A, B, C, D> Table4<A, B, C, D>.forAll(fn: (A, B, C, D) -> Unit) = forAll(this, fn)
@JvmName("forAll5receiver") inline fun <A, B, C, D, E> Table5<A, B, C, D, E>.forAll(fn: (A, B, C, D, E) -> Unit) = forAll(this, fn)
@JvmName("forAll6receiver") inline fun <A, B, C, D, E, F> Table6<A, B, C, D, E, F>.forAll(fn: (A, B, C, D, E, F) -> Unit) = forAll(this, fn)
@JvmName("forAll7receiver") inline fun <A, B, C, D, E, F, G> Table7<A, B, C, D, E, F, G>.forAll(fn: (A, B, C, D, E, F, G) -> Unit) = forAll(this, fn)
@JvmName("forAll8receiver") inline fun <A, B, C, D, E, F, G, H> Table8<A, B, C, D, E, F, G, H>.forAll(fn: (A, B, C, D, E, F, G, H) -> Unit) = forAll(this, fn)
@JvmName("forAll9receiver") inline fun <A, B, C, D, E, F, G, H, I> Table9<A, B, C, D, E, F, G, H, I>.forAll(fn: (A, B, C, D, E, F, G, H, I) -> Unit) = forAll(this, fn)
@JvmName("forAll10receiver") inline fun <A, B, C, D, E, F, G, H, I, J> Table10<A, B, C, D, E, F, G, H, I, J>.forAll(fn: (A, B, C, D, E, F, G, H, I, J) -> Unit) = forAll(this, fn)
@JvmName("forAll11receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K> Table11<A, B, C, D, E, F, G, H, I, J, K>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit) = forAll(this, fn)
@JvmName("forAll12receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Table12<A, B, C, D, E, F, G, H, I, J, K, L>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L) -> Unit) = forAll(this, fn)
@JvmName("forAll13receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Table13<A, B, C, D, E, F, G, H, I, J, K, L, M>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit) = forAll(this, fn)
@JvmName("forAll14receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Table14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> Unit) = forAll(this, fn)
@JvmName("forAll15receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Table15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> Unit) = forAll(this, fn)
@JvmName("forAll16receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Table16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Unit) = forAll(this, fn)
@JvmName("forAll17receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Table17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> Unit) = forAll(this, fn)
@JvmName("forAll18receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Table18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> Unit) = forAll(this, fn)
@JvmName("forAll19receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Table19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit) = forAll(this, fn)
@JvmName("forAll20receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Table20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> Unit) = forAll(this, fn)
@JvmName("forAll21receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Table21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) -> Unit) = forAll(this, fn)
@JvmName("forAll22receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Table22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) -> Unit) = forAll(this, fn)

inline fun <A> forNone(table: Table1<A>, fn: (A) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B> forNone(table: Table2<A, B>, fn: (A, B) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C> forNone(table: Table3<A, B, C>, fn: (A, B, C) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D> forNone(table: Table4<A, B, C, D>, fn: (A, B, C, D) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E> forNone(table: Table5<A, B, C, D, E>, fn: (A, B, C, D, E) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}


inline fun <A, B, C, D, E, F> forNone(table: Table6<A, B, C, D, E, F>, fn: (A, B, C, D, E, F) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G> forNone(table: Table7<A, B, C, D, E, F, G>, fn: (A, B, C, D, E, F, G) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H> forNone(table: Table8<A, B, C, D, E, F, G, H>, fn: (A, B, C, D, E, F, G, H) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I> forNone(table: Table9<A, B, C, D, E, F, G, H, I>, fn: (A, B, C, D, E, F, G, H, I) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J> forNone(table: Table10<A, B, C, D, E, F, G, H, I, J>, fn: (A, B, C, D, E, F, G, H, I, J) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K> forNone(table: Table11<A, B, C, D, E, F, G, H, I, J, K>, fn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L> forNone(table: Table12<A, B, C, D, E, F, G, H, I, J, K, L>, fn: (A, B, C, D, E, F, G, H, I, J, K, L) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forNone(table: Table13<A, B, C, D, E, F, G, H, I, J, K, L, M>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> forNone(table: Table14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> forNone(table: Table15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> forNone(table: Table16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> forNone(table: Table17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> forNone(table: Table18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> forNone(table: Table19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> forNone(table: Table20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s, row.t)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> forNone(table: Table21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s, row.t, row.u)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> forNone(table: Table22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) -> Unit) {
  for (row in table.rows) {
    try {
      fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s, row.t, row.u, row.v)
    } catch (e: AssertionError) {
      continue
    }
    throw forNoneError(table.headers.values(), row.values())
  }
}

@JvmName("forNone1receiver") inline fun <A> Table1<A>.forNone(fn: (A) -> Unit) = forNone(this, fn)
@JvmName("forNone2receiver") inline fun <A, B> Table2<A, B>.forNone(fn: (A, B) -> Unit) = forNone(this, fn)
@JvmName("forNone3receiver") inline fun <A, B, C> Table3<A, B, C>.forNone(fn: (A, B, C) -> Unit) = forNone(this, fn)
@JvmName("forNone4receiver") inline fun <A, B, C, D> Table4<A, B, C, D>.forNone(fn: (A, B, C, D) -> Unit) = forNone(this, fn)
@JvmName("forNone5receiver") inline fun <A, B, C, D, E> Table5<A, B, C, D, E>.forNone(fn: (A, B, C, D, E) -> Unit) = forNone(this, fn)
@JvmName("forNone6receiver") inline fun <A, B, C, D, E, F> Table6<A, B, C, D, E, F>.forNone(fn: (A, B, C, D, E, F) -> Unit) = forNone(this, fn)
@JvmName("forNone7receiver") inline fun <A, B, C, D, E, F, G> Table7<A, B, C, D, E, F, G>.forNone(fn: (A, B, C, D, E, F, G) -> Unit) = forNone(this, fn)
@JvmName("forNone8receiver") inline fun <A, B, C, D, E, F, G, H> Table8<A, B, C, D, E, F, G, H>.forNone(fn: (A, B, C, D, E, F, G, H) -> Unit) = forNone(this, fn)
@JvmName("forNone9receiver") inline fun <A, B, C, D, E, F, G, H, I> Table9<A, B, C, D, E, F, G, H, I>.forNone(fn: (A, B, C, D, E, F, G, H, I) -> Unit) = forNone(this, fn)
@JvmName("forNone10receiver") inline fun <A, B, C, D, E, F, G, H, I, J> Table10<A, B, C, D, E, F, G, H, I, J>.forNone(fn: (A, B, C, D, E, F, G, H, I, J) -> Unit) = forNone(this, fn)
@JvmName("forNone11receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K> Table11<A, B, C, D, E, F, G, H, I, J, K>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit) = forNone(this, fn)
@JvmName("forNone12receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Table12<A, B, C, D, E, F, G, H, I, J, K, L>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L) -> Unit) = forNone(this, fn)
@JvmName("forNone13receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Table13<A, B, C, D, E, F, G, H, I, J, K, L, M>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit) = forNone(this, fn)
@JvmName("forNone14receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Table14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> Unit) = forNone(this, fn)
@JvmName("forNone15receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Table15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> Unit) = forNone(this, fn)
@JvmName("forNone16receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Table16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Unit) = forNone(this, fn)
@JvmName("forNone17receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Table17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> Unit) = forNone(this, fn)
@JvmName("forNone18receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Table18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> Unit) = forNone(this, fn)
@JvmName("forNone19receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Table19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit) = forNone(this, fn)
@JvmName("forNone20receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Table20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> Unit) = forNone(this, fn)
@JvmName("forNone21receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Table21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) -> Unit) = forNone(this, fn)
@JvmName("forNone22receiver") inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Table22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) -> Unit) = forNone(this, fn)


data class Table1<out A>(val headers: Headers1, val rows: List<Row1<A>>)
data class Table2<out A, out B>(val headers: Headers2, val rows: List<Row2<A, B>>)
data class Table3<out A, out B, out C>(val headers: Headers3, val rows: List<Row3<A, B, C>>)
data class Table4<out A, out B, out C, out D>(val headers: Headers4, val rows: List<Row4<A, B, C, D>>)
data class Table5<out A, out B, out C, out D, out E>(val headers: Headers5, val rows: List<Row5<A, B, C, D, E>>)
data class Table6<out A, out B, out C, out D, out E, out F>(val headers: Headers6, val rows: List<Row6<A, B, C, D, E, F>>)
data class Table7<out A, out B, out C, out D, out E, out F, out G>(val headers: Headers7, val rows: List<Row7<A, B, C, D, E, F, G>>)
data class Table8<out A, out B, out C, out D, out E, out F, out G, out H>(val headers: Headers8, val rows: List<Row8<A, B, C, D, E, F, G, H>>)
data class Table9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val headers: Headers9, val rows: List<Row9<A, B, C, D, E, F, G, H, I>>)
data class Table10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val headers: Headers10, val rows: List<Row10<A, B, C, D, E, F, G, H, I, J>>)
data class Table11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(val headers: Headers11, val rows: List<Row11<A, B, C, D, E, F, G, H, I, J, K>>)
data class Table12<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L>(val headers: Headers12, val rows: List<Row12<A, B, C, D, E, F, G, H, I, J, K, L>>)
data class Table13<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M>(val headers: Headers13, val rows: List<Row13<A, B, C, D, E, F, G, H, I, J, K, L, M>>)
data class Table14<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N>(val headers: Headers14, val rows: List<Row14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>>)
data class Table15<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O>(val headers: Headers15, val rows: List<Row15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>>)
data class Table16<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P>(val headers: Headers16, val rows: List<Row16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>>)
data class Table17<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q>(val headers: Headers17, val rows: List<Row17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>>)
data class Table18<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R>(val headers: Headers18, val rows: List<Row18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>>)
data class Table19<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S>(val headers: Headers19, val rows: List<Row19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>>)
data class Table20<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T>(val headers: Headers20, val rows: List<Row20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>>)
data class Table21<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U>(val headers: Headers21, val rows: List<Row21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>>)
data class Table22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V>(val headers: Headers22, val rows: List<Row22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>>)

data class Headers1(val labelA: String) {
  fun values() = listOf(labelA)
}

data class Headers2(val labelA: String, val labelB: String) {
  fun values() = listOf(labelA, labelB)
}

data class Headers3(val labelA: String, val labelB: String, val labelC: String) {
  fun values() = listOf(labelA, labelB, labelC)
}

data class Headers4(val labelA: String, val labelB: String, val labelC: String, val labelD: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD)
}

data class Headers5(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE)
}

data class Headers6(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF)
}

data class Headers7(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG)
}

data class Headers8(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH)
}

data class Headers9(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI)
}

data class Headers10(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ)
}

data class Headers11(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK)
}

data class Headers12(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL)
}

data class Headers13(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM)
}

data class Headers14(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN)
}

data class Headers15(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO)
}

data class Headers16(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP)
}

data class Headers17(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ)
}

data class Headers18(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR)
}

data class Headers19(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String, val labelS: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR, labelS)
}

data class Headers20(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String, val labelS: String, val labelT: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR, labelS, labelT)
}

data class Headers21(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String, val labelS: String, val labelT: String, val labelU: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR, labelS, labelT, labelU)
}

data class Headers22(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String, val labelS: String, val labelT: String, val labelU: String, val labelV: String) {
  fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR, labelS, labelT, labelU, labelV)
}

data class Row1<out A>(val a: A) {
  fun values() = listOf(a)
}

data class Row2<out A, out B>(val a: A, val b: B) {
  fun values() = listOf(a, b)
}

data class Row3<out A, out B, out C>(val a: A, val b: B, val c: C) {
  fun values() = listOf(a, b, c)
}

data class Row4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) {
  fun values() = listOf(a, b, c, d)
}

data class Row5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) {
  fun values() = listOf(a, b, c, d, e)
}

data class Row6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F) {
  fun values() = listOf(a, b, c, d, e, f)
}

data class Row7<out A, out B, out C, out D, out E, out F, out G>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G) {
  fun values() = listOf(a, b, c, d, e, f, g)
}

data class Row8<out A, out B, out C, out D, out E, out F, out G, out H>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H) {
  fun values() = listOf(a, b, c, d, e, f, g, h)
}

data class Row9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i)
}

data class Row10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j)
}

data class Row11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k)
}

data class Row12<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l)
}

data class Row13<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m)
}

data class Row14<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
}

data class Row15<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
}

data class Row16<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
}

data class Row17<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
}

data class Row18<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
}

data class Row19<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
}

data class Row20<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
}

data class Row21<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
}

data class Row22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U, val v: V) {
  fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)
}
