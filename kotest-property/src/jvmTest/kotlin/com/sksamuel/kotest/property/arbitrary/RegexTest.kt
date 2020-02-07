package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.string.shouldMatch
import io.kotest.properties.Gen
import io.kotest.properties.regex

class RegexTest : FunSpec({

   test("regex generation") {
      val regex = "a.b.".toRegex(RegexOption.DOT_MATCHES_ALL)
      Gen.regex(regex).random().take(1000).forAll { it.shouldMatch(regex) }
   }

   test("regex generation 2") {
      val regex = "[a-z]-[0-9]abbc.".toRegex(RegexOption.DOT_MATCHES_ALL)
      Gen.regex(regex).random().take(1000).forAll { it.shouldMatch(regex) }
   }
})
