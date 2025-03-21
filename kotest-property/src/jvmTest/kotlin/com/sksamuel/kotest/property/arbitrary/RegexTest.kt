package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.take

@EnabledIf(NotMacOnGithubCondition::class)
class RegexTest : FunSpec({

   test("regex generation") {
      val pattern = "a.b."
      Arb.stringPattern(pattern).take(100).forAll { it.shouldMatch(pattern.toRegex(RegexOption.DOT_MATCHES_ALL)) }
   }

   test("regex generation 2") {
      val pattern = "[a-z]-[0-9]abbc."
      Arb.stringPattern(pattern).take(100).forAll { it.shouldMatch(pattern.toRegex(RegexOption.DOT_MATCHES_ALL)) }
   }
})
