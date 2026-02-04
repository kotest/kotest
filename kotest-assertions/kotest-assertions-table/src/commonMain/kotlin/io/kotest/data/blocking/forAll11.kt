@file:Suppress("DEPRECATION")

package io.kotest.data.blocking

import io.kotest.data.Row11
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.paramNames
import io.kotest.data.table

fun <A, B, C, D, E, F, G, H, I, J, K> forAll(
   vararg rows: Row11<A, B, C, D, E, F, G, H, I, J, K>,
   testfn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit
) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   val paramH = params.getOrElse(7) { "h" }
   val paramI = params.getOrElse(8) { "i" }
   val paramJ = params.getOrElse(9) { "j" }
   val paramK = params.getOrElse(10) { "k" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK), *rows).forAll(testfn)
}

fun <A, B, C, D, E, F, G, H, I, J, K> forNone(
   vararg rows: Row11<A, B, C, D, E, F, G, H, I, J, K>,
   testfn: (A, B, C, D, E, F, G, H, I, J, K) -> Unit
) {
   val params = paramNames(testfn)
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   val paramH = params.getOrElse(7) { "h" }
   val paramI = params.getOrElse(8) { "i" }
   val paramJ = params.getOrElse(9) { "j" }
   val paramK = params.getOrElse(10) { "k" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK), *rows).forNone(testfn)
}
