package io.kotest.data.blocking

import io.kotest.data.Row4
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.table
import io.kotest.mpp.reflection

fun <A, B, C, D> forAll(vararg rows: Row4<A, B, C, D>, testfn: (A, B, C, D) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(2) { "d" }
   table(headers(paramA, paramB, paramC, paramD), *rows).forAll { A, B, C, D -> testfn(A, B, C, D) }
}

fun <A, B, C, D> forNone(vararg rows: Row4<A, B, C, D>, testfn: (A, B, C, D) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(2) { "d" }
   table(headers(paramA, paramB, paramC, paramD), *rows).forNone { A, B, C, D -> testfn(A, B, C, D) }
}
