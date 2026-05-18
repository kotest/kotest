package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.pattern
import io.kotest.property.arbitrary.take

class PatternCommonTest : FunSpec({
   test("Arb.pattern matches the regex on every target") {
      Arb.pattern("[a-z]{3}").take(50).forEach {
         it.shouldMatch(Regex("[a-z]{3}"))
      }
   }
})
