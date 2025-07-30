@file:Suppress("DEPRECATION")

package io.kotest.data

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A> row(a: A) = Row1(a)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B> row(a: A, b: B) = Row2(a, b)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C> row(a: A, b: B, c: C) = Row3(a, b, c)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D> row(a: A, b: B, c: C, d: D) = Row4(a, b, c, d)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E> row(a: A, b: B, c: C, d: D, e: E) = Row5(a, b, c, d, e)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F> row(a: A, b: B, c: C, d: D, e: E, f: F) = Row6(a, b, c, d, e, f)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G) = Row7(a, b, c, d, e, f, g)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H) = Row8(a, b, c, d, e, f, g, h)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I) = Row9(a, b, c, d, e, f, g, h, i)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I, J> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J) = Row10(a, b, c, d, e, f, g, h, i, j)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I, J, K> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K) = Row11(a, b, c, d, e, f, g, h, i, j, k)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I, J, K, L> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L) = Row12(a, b, c, d, e, f, g, h, i, j, k, l)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M) = Row13(a, b, c, d, e, f, g, h, i, j, k, l, m)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N) = Row14(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O) = Row15(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P) = Row16(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q) = Row17(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R) = Row18(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S) = Row19(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T) = Row20(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U) = Row21(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> row(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V) = Row22(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
interface Row {
   fun values(): List<Any?>
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row1<out A>(val a: A): Row {
   override fun values() = listOf<Any?>(a)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row2<out A, out B>(val a: A, val b: B): Row {
   override fun values() = listOf(a, b)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row3<out A, out B, out C>(val a: A, val b: B, val c: C): Row {
   override fun values() = listOf(a, b, c)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D): Row {
   override fun values(): List<Any?> = listOf(a, b, c, d)
}

data class Row5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E): Row {
   override fun values() = listOf(a, b, c, d, e)
}

data class Row6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F): Row {
   override fun values() = listOf(a, b, c, d, e, f)
}

data class Row7<out A, out B, out C, out D, out E, out F, out G>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G): Row {
   override fun values() = listOf(a, b, c, d, e, f, g)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row8<out A, out B, out C, out D, out E, out F, out G, out H>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row12<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row13<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row14<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row15<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row16<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row17<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row18<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row19<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row20<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row21<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
}
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Row22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U, val v: V): Row {
   override fun values() = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)
}
