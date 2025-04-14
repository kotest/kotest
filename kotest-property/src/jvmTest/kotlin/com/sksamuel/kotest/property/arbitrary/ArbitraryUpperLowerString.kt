package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAtLeast
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.property.Arb
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.upperLowerCase

@EnabledIf(LinuxOnlyGithubCondition::class)
class ArbitraryUpperLowerString : FreeSpec(
   {
      "UpperLowerString should generate strings with upper and lower case letters" {
         // Upper lower would blow up if we generate all permutations of this string eagerly
         val veryLongString =
            "abcdefghijklmnopqrstuvxyabcdefghijklmnopqrstuvx" +
               "yabcdefghijklmnopqrstuvxyabcdefghijklmnopqrstuvx" +
               "yzzzzabcdefghijklmnopqrstuvxyzacbacbacbabcbacbab"

         Arb.upperLowerCase(veryLongString)
            .take(1000)
            .toList()
            .forAtLeastOne {
               it.all(Char::isUpperCase)
            }.forAtLeastOne {
               it.all(Char::isLowerCase)
            }.forAtLeast(900) {
               it.count(Char::isUpperCase) shouldBeGreaterThan veryLongString.length / 4
               it.count(Char::isLowerCase) shouldBeGreaterThan veryLongString.length / 4
            }.run {
               toSet().shouldHaveAtLeastSize(990) // Might be duplicates because of edgecases?
            }
      }
   }
)
