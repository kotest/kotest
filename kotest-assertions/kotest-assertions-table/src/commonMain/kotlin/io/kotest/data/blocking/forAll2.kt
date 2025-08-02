@file:Suppress("DEPRECATION")

package io.kotest.data.blocking

import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.paramNames
import io.kotest.data.table

fun <A, B> forAll(vararg rows: Row2<A, B>, testfn: (A, B) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   table(headers(paramA, paramB), *rows).forAll(testfn)
}

fun <A, B> forNone(vararg rows: Row2<A, B>, testfn: (A, B) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   table(headers(paramA, paramB), *rows).forNone(testfn)
}
