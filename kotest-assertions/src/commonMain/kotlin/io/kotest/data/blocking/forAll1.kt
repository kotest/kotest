package io.kotest.data.blocking

import io.kotest.data.*
import io.kotest.mpp.paramNames
import kotlin.jvm.JvmName

fun <A> forAll(vararg rows: Row1<A>, testfn: (A) -> Unit) {
   val params = testfn.paramNames
   val paramA = params.getOrElse(0) { "a" }
   table(headers(paramA), *rows).forAll { a -> testfn(a) }
}


fun <A> forNone(vararg rows: Row1<A>, testfn: (A) -> Unit) {
   val params = testfn.paramNames
   val paramA = params.getOrElse(0) { "a" }
   table(headers(paramA), *rows).forNone { a -> testfn(a) }
}
