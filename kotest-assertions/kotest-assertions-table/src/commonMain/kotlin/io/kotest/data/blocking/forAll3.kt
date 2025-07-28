@file:Suppress("DEPRECATION")

package io.kotest.data.blocking

import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.paramNames
import io.kotest.data.table

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C> forAll(vararg rows: Row3<A, B, C>, testfn: (A, B, C) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   table(headers(paramA, paramB, paramC), *rows).forAll(testfn)
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C> forNone(vararg rows: Row3<A, B, C>, testfn: (A, B, C) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   table(headers(paramA, paramB, paramC), *rows).forNone(testfn)
}
