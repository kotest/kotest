package io.kotest.data.blocking

import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.table
import io.kotest.mpp.reflection

fun <A, B, C> forAll(vararg rows: Row3<A, B, C>, testfn: (A, B, C) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   table(headers(paramA, paramB, paramC), *rows).forAll { A, B, C -> testfn(A, B, C) }
}

fun <A, B, C> forNone(vararg rows: Row3<A, B, C>, testfn: (A, B, C) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   table(headers(paramA, paramB, paramC), *rows).forNone { A, B, C -> testfn(A, B, C) }
}
