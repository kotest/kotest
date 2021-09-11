package com.sksamuel.kotest.property.arbitrary

import com.sksamuel.kotest.property.PropSpec
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern

class RegexTest : PropSpec({

   val pattern1 = "a.b."
   val pattern2 = "[a-z]-[0-9]abbc."

   prop("regex generation pattern 1", Arb.stringPattern(pattern1)) {
      it.shouldMatch(pattern1.toRegex(RegexOption.DOT_MATCHES_ALL))
   }

   prop("regex generation pattern 2", Arb.stringPattern(pattern2)) {
      it.shouldMatch(pattern1.toRegex(RegexOption.DOT_MATCHES_ALL))
   }
})
