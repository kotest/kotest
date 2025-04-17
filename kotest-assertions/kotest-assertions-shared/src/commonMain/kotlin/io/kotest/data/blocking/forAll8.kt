package io.kotest.data.blocking

import io.kotest.data.Row8
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.table
import io.kotest.mpp.reflection

fun <A, B, C, D, E, F, G, H> forAll(
   vararg rows: Row8<A, B, C, D, E, F, G, H>,
   testfn: (A, B, C, D, E, F, G, H) -> Unit
) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   val paramH = params.getOrElse(7) { "h" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH), *rows).forAll(testfn)
}

fun <A, B, C, D, E, F, G, H> forNone(
   vararg rows: Row8<A, B, C, D, E, F, G, H>,
   testfn: (A, B, C, D, E, F, G, H) -> Unit
) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   val paramH = params.getOrElse(7) { "h" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH), *rows).forNone(testfn)
}
