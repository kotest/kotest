package io.kotest.data.blocking

import io.kotest.data.Row5
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.table
import io.kotest.mpp.reflection

fun <A, B, C, D, E> forAll(vararg rows: Row5<A, B, C, D, E>, testfn: (A, B, C, D, E) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   table(headers(paramA, paramB, paramC, paramD, paramE), *rows).forAll { A, B, C, D, E -> testfn(A, B, C, D, E) }
}

fun <A, B, C, D, E> forNone(vararg rows: Row5<A, B, C, D, E>, testfn: (A, B, C, D, E) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   table(headers(paramA, paramB, paramC, paramD, paramE), *rows).forNone { A, B, C, D, E -> testfn(A, B, C, D, E) }
}
