package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.take

class StringPatternCommonTest : FunSpec({
   test("Arb.stringPattern matches the regex on every target") {
      Arb.stringPattern("[a-z]{3}").take(50).forEach {
         it.shouldMatch(Regex("[a-z]{3}"))
      }
   }
})
