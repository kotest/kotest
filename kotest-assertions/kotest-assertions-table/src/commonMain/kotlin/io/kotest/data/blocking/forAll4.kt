@file:Suppress("DEPRECATION")

package io.kotest.data.blocking

import io.kotest.data.Row4
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.paramNames
import io.kotest.data.table

fun <A, B, C, D> forAll(vararg rows: Row4<A, B, C, D>, testfn: (A, B, C, D) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   table(headers(paramA, paramB, paramC, paramD), *rows).forAll(testfn)
}

fun <A, B, C, D> forNone(vararg rows: Row4<A, B, C, D>, testfn: (A, B, C, D) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   table(headers(paramA, paramB, paramC, paramD), *rows).forNone(testfn)
}
