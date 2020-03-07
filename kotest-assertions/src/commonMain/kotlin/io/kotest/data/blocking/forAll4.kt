package io.kotest.data.blocking

import io.kotest.data.*
import io.kotest.mpp.paramNames
import kotlin.jvm.JvmName

fun <A, B, C, D> forAll(vararg rows: Row4<A, B, C, D>, testfn: (A, B, C, D) -> Unit) {
   val params = testfn.paramNames
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(2) { "d" }
   table(headers(paramA, paramB, paramC, paramD), *rows).forAll { A, B, C, D -> testfn(A, B, C, D) }
}

fun <A, B, C, D> forNone(vararg rows: Row4<A, B, C, D>, testfn: (A, B, C, D) -> Unit) {
   val params = testfn.paramNames
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(2) { "d" }
   table(headers(paramA, paramB, paramC, paramD), *rows).forNone { A, B, C, D -> testfn(A, B, C, D) }
}
