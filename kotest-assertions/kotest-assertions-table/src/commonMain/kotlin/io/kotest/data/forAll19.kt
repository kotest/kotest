@file:Suppress("DEPRECATION")

package io.kotest.data

import kotlin.jvm.JvmName

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> forAll(
   vararg rows: Row19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>,
   testfn: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit
) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   val paramH = params.getOrElse(7) { "h" }
   val paramI = params.getOrElse(8) { "i" }
   val paramJ = params.getOrElse(9) { "j" }
   val paramK = params.getOrElse(10) { "k" }
   val paramL = params.getOrElse(11) { "l" }
   val paramM = params.getOrElse(12) { "m" }
   val paramN = params.getOrElse(13) { "n" }
   val paramO = params.getOrElse(14) { "o" }
   val paramP = params.getOrElse(15) { "p" }
   val paramQ = params.getOrElse(16) { "q" }
   val paramR = params.getOrElse(17) { "r" }
   val paramS = params.getOrElse(18) { "s" }
   table(
      headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK, paramL, paramM, paramN, paramO, paramP, paramQ, paramR, paramS),
      *rows
   ).forAll { A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S ->
      testfn(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)
   }
}

@JvmName("forall19")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> forAll(table: Table19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, testfn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit) =
   table.forAll(testfn)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Table19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.forAll(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit) {
   val collector = ErrorCollector()
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s)
      } catch (e: Throwable) {
         collector.append(error(e, headers.values(), row.values()))
      }
   }
   collector.assertAll()
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> forNone(
   vararg rows: Row19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>,
   testfn: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit
) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   val paramH = params.getOrElse(7) { "h" }
   val paramI = params.getOrElse(8) { "i" }
   val paramJ = params.getOrElse(9) { "j" }
   val paramK = params.getOrElse(10) { "k" }
   val paramL = params.getOrElse(11) { "l" }
   val paramM = params.getOrElse(12) { "m" }
   val paramN = params.getOrElse(13) { "n" }
   val paramO = params.getOrElse(14) { "o" }
   val paramP = params.getOrElse(15) { "p" }
   val paramQ = params.getOrElse(16) { "q" }
   val paramR = params.getOrElse(17) { "r" }
   val paramS = params.getOrElse(18) { "s" }
   table(
      headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK, paramL, paramM, paramN, paramO, paramP, paramQ, paramR, paramS),
      *rows
   ).forNone { A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S ->
      testfn(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)
   }
}

@JvmName("fornone19")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> forNone(table: Table19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, testfn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit) =
   table.forNone(testfn)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Table19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.forNone(fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit) {
   for (row in rows) {
      try {
         fn(row.a, row.b, row.c, row.d, row.e, row.f, row.g, row.h, row.i, row.j, row.k, row.l, row.m, row.n, row.o, row.p, row.q, row.r, row.s)
      } catch (e: AssertionError) {
         continue
      }
      throw forNoneError(headers.values(), row.values())
   }
}
