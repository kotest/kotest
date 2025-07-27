package io.kotest.data

import kotlin.collections.get

fun <Key, Value> Map<Key, Value>.toTable(
   headers: Headers2 = headers("key", "value"),
) = table(headers, entries.map { row(it.key, it.value) })

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A> table(headers: Headers1, rows: List<Row1<A>>) = Table1(headers, rows)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B> table(headers: Headers2, rows: List<Row2<A, B>>) = Table2(headers, rows)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C> table(headers: Headers3, rows: List<Row3<A, B, C>>) = Table3(headers, rows)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D> table(headers: Headers4, rows: List<Row4<A, B, C, D>>) = Table4(headers, rows)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E> table(headers: Headers5, rows: List<Row5<A, B, C, D, E>>) = Table5(headers, rows)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F> table(headers: Headers6, rows: List<Row6<A, B, C, D, E, F>>) = Table6(headers, rows)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G> table(headers: Headers7, rows: List<Row7<A, B, C, D, E, F, G>>) = Table7(headers, rows)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H> table(headers: Headers8, rows: List<Row8<A, B, C, D, E, F, G, H>>) = Table8(headers, rows)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I> table(headers: Headers9, rows: List<Row9<A, B, C, D, E, F, G, H, I>>) = Table9(headers, rows)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <ARow: Row, A> Table1<A>.mapRows(fn: (Row1<A>) -> ARow): List<ARow> = rows.map(fn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <ARow: Row, A, B> Table2<A, B>.mapRows(fn: (Row2<A, B>) -> ARow): List<ARow> = rows.map(fn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <ARow: Row, A, B, C> Table3<A, B, C>.mapRows(fn: (Row3<A, B, C>) -> ARow): List<ARow> = rows.map(fn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <ARow: Row, A, B, C, D> Table4<A, B, C, D>.mapRows(fn: (Row4<A, B, C, D>) -> ARow): List<ARow> = rows.map(fn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <ARow: Row, A, B, C, D, E> Table5<A, B, C, D, E>.mapRows(fn: (Row5<A, B, C, D, E>) -> ARow): List<ARow> = rows.map(fn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <ARow: Row, A, B, C, D, E, F> Table6<A, B, C, D, E, F>.mapRows(fn: (Row6<A, B, C, D, E, F>) -> ARow): List<ARow> = rows.map(fn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <ARow: Row, A, B, C, D, E, F, G> Table7<A, B, C, D, E, F, G>.mapRows(fn: (Row7<A, B, C, D, E, F, G>) -> ARow): List<ARow> = rows.map(fn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <ARow: Row, A, B, C, D, E, F, G, H> Table8<A, B, C, D, E, F, G, H>.mapRows(fn: (Row8<A, B, C, D, E, F, G, H>) -> ARow): List<ARow> = rows.map(fn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <ARow: Row, A, B, C, D, E, F, G, H, I> Table9<A, B, C, D, E, F, G, H, I>.mapRows(fn: (Row9<A, B, C, D, E, F, G, H, I>) -> ARow): List<ARow> = rows.map(fn)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A> table(headers: Headers1, vararg rows: Row1<A>) = Table1(headers, rows.asList())

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B> table(headers: Headers2, vararg rows: Row2<A, B>) = Table2(headers, rows.asList())

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C> table(headers: Headers3, vararg rows: Row3<A, B, C>) = Table3(headers, rows.asList())

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D> table(headers: Headers4, vararg rows: Row4<A, B, C, D>) = Table4(headers, rows.asList())
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E> table(headers: Headers5, vararg rows: Row5<A, B, C, D, E>) = Table5(headers, rows.asList())
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F> table(headers: Headers6, vararg rows: Row6<A, B, C, D, E, F>) = Table6(headers, rows.asList())
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G> table(headers: Headers7, vararg rows: Row7<A, B, C, D, E, F, G>) = Table7(headers, rows.asList())
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H> table(headers: Headers8, vararg rows: Row8<A, B, C, D, E, F, G, H>) = Table8(headers, rows.asList())
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I> table(headers: Headers9, vararg rows: Row9<A, B, C, D, E, F, G, H, I>) = Table9(headers, rows.asList())
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I, J> table(headers: Headers10, vararg rows: Row10<A, B, C, D, E, F, G, H, I, J>) = Table10(headers, rows.asList())
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
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

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table1<out A>(val headers: Headers1, val rows: List<Row1<A>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table2<out A, out B>(val headers: Headers2, val rows: List<Row2<A, B>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table3<out A, out B, out C>(val headers: Headers3, val rows: List<Row3<A, B, C>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table4<out A, out B, out C, out D>(val headers: Headers4, val rows: List<Row4<A, B, C, D>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table5<out A, out B, out C, out D, out E>(val headers: Headers5, val rows: List<Row5<A, B, C, D, E>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table6<out A, out B, out C, out D, out E, out F>(val headers: Headers6, val rows: List<Row6<A, B, C, D, E, F>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table7<out A, out B, out C, out D, out E, out F, out G>(val headers: Headers7, val rows: List<Row7<A, B, C, D, E, F, G>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table8<out A, out B, out C, out D, out E, out F, out G, out H>(val headers: Headers8, val rows: List<Row8<A, B, C, D, E, F, G, H>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val headers: Headers9, val rows: List<Row9<A, B, C, D, E, F, G, H, I>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
data class Table10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val headers: Headers10, val rows: List<Row10<A, B, C, D, E, F, G, H, I, J>>)
@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
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

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A> table(headers: Headers1, fileContent: String, transform: (String) -> Row1<A>) = Table1(
   headers = headers,
   rows = StringTable(headers.values(), fileContent.lines())
      .mapRows { transform(it[0]) }
)

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B> table(headers: Headers2, fileContent: String, transform: (String, String) -> Row2<A, B>) = Table2(
   headers = headers,
   rows = StringTable(headers.values(), fileContent.lines())
      .mapRows { transform(it[0], it[1]) }
)

fun <A, B, C> table(headers: Headers3, fileContent: String, transform:(String, String, String) -> Row3<A, B, C>
): Table3<A, B, C> = Table3(
   headers = headers,
   rows = StringTable(headers.values(), fileContent.lines())
      .mapRows { transform(it[0], it[1], it[2]) }
)

fun <A, B, C, D> table(headers: Headers4, fileContent: String, transform:(String, String, String, String) -> Row4<A, B, C, D>) = Table4(
   headers = headers,
   rows = StringTable(headers.values(), fileContent.lines())
      .mapRows { transform(it[0], it[1], it[2], it[3])}
)
fun <A, B, C, D, E> table(headers: Headers5, fileContent: String, transform:(String, String, String, String, String) -> Row5<A, B, C, D, E>) = Table5(
   headers = headers,
   rows = StringTable(headers.values(), fileContent.lines())
      .mapRows { transform(it[0], it[1], it[2], it[3], it[4])}
)
fun <A, B, C, D, E, F> table(headers: Headers6, fileContent: String, transform:(String, String, String, String, String, String) -> Row6<A, B, C, D, E, F>) = Table6(
   headers = headers,
   rows = StringTable(headers.values(), fileContent.lines())
      .mapRows { transform(it[0], it[1], it[2], it[3], it[4],it[5]) }
)
fun <A, B, C, D, E, F, G> table(headers: Headers7, fileContent: String, transform:(String, String, String, String, String, String, String) -> Row7<A, B, C, D, E, F, G>) = Table7(
   headers = headers,
   rows = StringTable(headers.values(), fileContent.lines())
      .mapRows { transform(it[0], it[1], it[2], it[3], it[4],it[5], it[6])}
)
fun <A, B, C, D, E, F, G, H> table(headers: Headers8, fileContent: String, transform:(String, String, String, String, String, String, String, String) -> Row8<A, B, C, D, E, F, G, H>) = Table8(
   headers = headers,
   rows = StringTable(headers.values(), fileContent.lines())
      .mapRows { transform(it[0], it[1], it[2], it[3], it[4],it[5], it[6], it[7])}
)
fun <A, B, C, D, E, F, G, H, I> table(headers: Headers9, fileContent: String, transform:(String, String, String, String, String, String, String, String, String) -> Row9<A, B, C, D, E, F, G, H, I>) = Table9(
   headers = headers,
   rows = StringTable(headers.values(), fileContent.lines())
      .mapRows { transform(it[0], it[1], it[2], it[3], it[4],it[5], it[6], it[7], it[8])}
)
