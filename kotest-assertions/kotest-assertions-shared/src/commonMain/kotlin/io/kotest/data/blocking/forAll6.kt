package io.kotest.data.blocking

import io.kotest.data.Row6
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.table
import io.kotest.mpp.reflection

fun <A, B, C, D, E, F> forAll(vararg rows: Row6<A, B, C, D, E, F>, testfn: (A, B, C, D, E, F) -> Unit) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF), *rows).forAll { A, B, C, D, E, F ->
      testfn(A, B, C, D, E, F)
   }
}

fun <A, B, C, D, E, F> forNone(
   vararg rows: Row6<A, B, C, D, E, F>,
   testfn: (A, B, C, D, E, F) -> Unit
) {
   val params = reflection.paramNames(testfn) ?: emptyList<String>()
   val paramA = params.getOrElse(0) { "a" }
   val paramB = params.getOrElse(1) { "b" }
   val paramC = params.getOrElse(2) { "c" }
   val paramD = params.getOrElse(3) { "d" }
   val paramE = params.getOrElse(4) { "e" }
   val paramF = params.getOrElse(5) { "f" }
   table(headers(paramA, paramB, paramC, paramD, paramE, paramF), *rows).forNone { A, B, C, D, E, F ->
      testfn(A, B, C, D, E, F)
   }
}
