package io.kotest.data.blocking

import io.kotest.data.*
import io.kotest.mpp.paramNames
import kotlin.jvm.JvmName

fun <A, B, C, D, E> forAll(vararg rows: Row5<A, B, C, D, E>, testfn: (A, B, C, D, E) -> Unit) {
   val params = testfn.paramNames
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   table(headers(paramA, paramB, paramC, paramD, paramE), *rows).forAll { A, B, C, D, E -> testfn(A, B, C, D, E) }
}

fun <A, B, C, D, E> forNone(vararg rows: Row5<A, B, C, D, E>, testfn: (A, B, C, D, E) -> Unit) {
   val params = testfn.paramNames
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   table(headers(paramA, paramB, paramC, paramD, paramE), *rows).forNone { A, B, C, D, E -> testfn(A, B, C, D, E) }
}
