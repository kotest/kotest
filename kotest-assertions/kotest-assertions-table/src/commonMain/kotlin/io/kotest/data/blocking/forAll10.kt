@file:Suppress("DEPRECATION")

package io.kotest.data.blocking

import io.kotest.data.Row10
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.paramNames
import io.kotest.data.table

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I, J> forAll(
   vararg rows: Row10<A, B, C, D, E, F, G, H, I, J>,
   testfn: (A, B, C, D, E, F, G, H, I, J) -> Unit
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
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ), *rows).forAll(testfn)
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
fun <A, B, C, D, E, F, G, H, I, J> forNone(
   vararg rows: Row10<A, B, C, D, E, F, G, H, I, J>,
   testfn: (A, B, C, D, E, F, G, H, I, J) -> Unit
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
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ), *rows).forNone(testfn)
}
