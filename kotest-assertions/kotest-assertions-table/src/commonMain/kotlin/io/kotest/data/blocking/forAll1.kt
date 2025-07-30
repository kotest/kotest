@file:Suppress("DEPRECATION")

package io.kotest.data.blocking

import io.kotest.data.Row1
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.paramNames
import io.kotest.data.table

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A> forAll(vararg rows: Row1<A>, testfn: (A) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   table(headers(paramA), *rows).forAll(testfn)
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A> forNone(vararg rows: Row1<A>, testfn: (A) -> Unit) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   table(headers(paramA), *rows).forNone(testfn)
}
