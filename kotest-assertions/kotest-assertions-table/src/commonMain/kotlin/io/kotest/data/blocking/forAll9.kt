package io.kotest.data.blocking

import io.kotest.data.Row9
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.table
import io.kotest.common.reflection.reflection

fun <A, B, C, D, E, F, G, H, I> forAll(
   vararg rows: Row9<A, B, C, D, E, F, G, H, I>,
   testfn: (A, B, C, D, E, F, G, H, I) -> Unit
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
   val paramI = params.getOrElse(8) { "i" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI), *rows).forAll(testfn)
}

fun <A, B, C, D, E, F, G, H, I> forNone(
   vararg rows: Row9<A, B, C, D, E, F, G, H, I>,
   testfn: (A, B, C, D, E, F, G, H, I) -> Unit
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
   val paramI = params.getOrElse(8) { "i" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI), *rows).forNone(testfn)
}
