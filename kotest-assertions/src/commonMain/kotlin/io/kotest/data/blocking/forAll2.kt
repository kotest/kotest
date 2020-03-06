package io.kotest.data.blocking

import io.kotest.data.*
import io.kotest.mpp.paramNames
import kotlin.jvm.JvmName

fun <A, B> forAll(vararg rows: Row2<A, B>, testfn: (A, B) -> Unit) {
   val params = testfn.paramNames
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   table(headers(paramA, paramB), *rows).forAll { a, b -> testfn(a, b) }
}


fun <A, B> forNone(vararg rows: Row2<A, B>, testfn: (A, B) -> Unit) {
   val params = testfn.paramNames
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   table(headers(paramA, paramB), *rows).forNone { a, b -> testfn(a, b) }
}
