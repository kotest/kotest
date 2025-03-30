package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveMaxLength
import io.kotest.matchers.string.shouldNotEndWith
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.domain
import io.kotest.property.arbitrary.filter
import io.kotest.property.checkAll
import java.nio.charset.StandardCharsets

@EnabledIf(LinuxOnlyGithubCondition::class)
class DomainArbTest : ShouldSpec({
   should("Generate domains with the most common TLDs by default") {

      val tlds = mutableListOf<String>()
      Arb.domain().checkAll(iterations = 10_000) {
         tlds += it.substringAfterLast(".")
      }

      tlds shouldContainAll listOf("com", "net", "org")
   }

   should("Only generate domains with selected TLDs") {
      Arb.domain(tlds = listOf("abc")).checkAll {
         it shouldEndWith ".abc"
      }
   }

   should("Generate domains with 2 to 4 labels") {
      val generatedLevels = mutableSetOf<Int>()
      Arb.domain().checkAll(iterations = 10_000) {
         generatedLevels += it.split(".").size
      }

      generatedLevels shouldBe setOf(2, 3, 4)
   }

   should("Never exceed 253 characters") {
      Arb.domain().checkAll {
         it shouldHaveMaxLength 253
      }
   }

   should("Not have labels exceeding 63 characters") {
      Arb.domain().checkAll {
         it.split(".").forAll { it shouldHaveMaxLength 63 }
      }
   }

   should("Have only ASCII characters") {
      Arb.domain().checkAll {
         StandardCharsets.US_ASCII.newEncoder().canEncode(it)
      }
   }

   should("Eventually generate domains with all valid characters") {
      val generatedLetters = mutableSetOf<Char>()

      Arb.domain().checkAll(iterations = 10_000) {
         generatedLetters += it.toList()
      }

      (generatedLetters - '.') shouldBe (('a'..'z') + ('A'..'Z') + ('0'..'9') + '-').toSet()
   }

   should("Never start with a hyphen") {
      Arb.domain().checkAll(iterations = 1000) {
         it shouldNotStartWith "-"
      }
   }

   should("Never end with a hyphen") {
      Arb.domain().checkAll(iterations = 1000) {
         it shouldNotEndWith "-"
      }
   }

   // https://whogohost.com/host/knowledgebase/308/Valid-Domain-Name-Characters.html
   // 2021-01-05
   should("Never contain a double dash in the 3rd and 4th positions") {
      Arb.domain().filter { it.length >= 4 }.checkAll(iterations = 1000) {
         it.substring(2, 4) shouldNotBe "--"
      }
   }
})
