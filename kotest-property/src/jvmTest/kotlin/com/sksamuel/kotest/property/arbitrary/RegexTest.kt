package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec

class RegexTest : FunSpec({

   test("regex generation") {
      val regex = "a.b.".toRegex(RegexOption.DOT_MATCHES_ALL)
//      Arb.regex(regex).random().take(1000).forAll { it.shouldMatch(regex) }
   }

   test("regex generation 2") {
      val regex = "[a-z]-[0-9]abbc.".toRegex(RegexOption.DOT_MATCHES_ALL)
//      Arb.regex(regex).random().take(1000).forAll { it.shouldMatch(regex) }
   }
})
