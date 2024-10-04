package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLengthBetween
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldNotEndWith
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.emailLocalPart
import io.kotest.property.checkAll

@EnabledIf(LinuxCondition::class)
class EmailArbTest : ShouldSpec({

   should("Generate only right format emails") {
      checkAll(Arb.email()) {
         it.shouldMatch(".+\\@.+\\..+")   // Simple regex only to get a feeling of what we're generating
      }
   }

   should("Generate emails with up to 320 characters") {
      Arb.email().checkAll {
         it.shouldHaveLengthBetween(3, 320)
      }
   }

   context("Local Part") {
      should("Generate email with all possible characters for local parts") {
         val chars = sortedSetOf<Char>()
         Arb.emailLocalPart().checkAll(iterations = 10_000) {
            chars += it.substringBefore('@').toList()
         }
         chars shouldContainExactlyInAnyOrder (('a'..'z') + ('A'..'Z') + ('0'..'9') + """!#$%&'*+-/=?^_`{|}~.""".toList())
      }

      should("Never generate emails with dot at the start or at the end") {
         Arb.emailLocalPart().checkAll {
            it shouldNotStartWith "."
            it shouldNotEndWith "."
         }
      }

      should("Never generate two dots in a row") {
         Arb.emailLocalPart().checkAll {
            it shouldNotContain ".."
         }
      }

      should("Generate with length ranging from 1 to 64") {
         val sizes = sortedSetOf<Int>()
         Arb.emailLocalPart().checkAll {
            sizes += it.length
         }
         sizes shouldBe (1..64).toSortedSet()
      }
   }
})
