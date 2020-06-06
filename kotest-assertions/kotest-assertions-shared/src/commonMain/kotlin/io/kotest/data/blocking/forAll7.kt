package io.kotest.data.blocking

import io.kotest.data.Row7
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.table
import io.kotest.mpp.reflection

fun <A, B, C, D, E, F, G> forAll(
   vararg rows: Row7<A, B, C, D, E, F, G>,
   testfn: (A, B, C, D, E, F, G) -> Unit
) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG), *rows).forAll { A, B, C, D, E, F, G ->
      testfn(A, B, C, D, E, F, G)
   }
}

fun <A, B, C, D, E, F, G> forNone(
   vararg rows: Row7<A, B, C, D, E, F, G>,
   testfn: (A, B, C, D, E, F, G) -> Unit
) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   val paramG = params.getOrElse(6) { "g" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF, paramG), *rows).forNone { A, B, C, D, E, F, G ->
      testfn(A, B, C, D, E, F, G)
   }
}
