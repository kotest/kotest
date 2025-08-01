package com.sksamuel.kotest.equals

import io.kotest.assertions.equals.Equality
import io.kotest.assertions.equals.EqualityResult
import io.kotest.assertions.equals.countByEquality
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class CountByEqualityTest : StringSpec() {
   private val caseInsensitiveStringEquality: Equality<String> = object : Equality<String> {
      override fun name() = "Case Insensitive String Matcher"

      override fun verify(actual: String, expected: String): EqualityResult {
         return if (actual.uppercase() == expected.uppercase())
            EqualityResult.equal(actual, expected, this)
         else
            EqualityResult.notEqual(actual, expected, this)
      }
   }

   init {
      "handle empty list" {
         listOf<String>().countByEquality(caseInsensitiveStringEquality) shouldBe mapOf()
      }
      "handle one element" {
         listOf<String>("apple").countByEquality(caseInsensitiveStringEquality) shouldBe mapOf("apple" to 1)
      }
      "handle multiple elements" {
         listOf<String>(
            "apple",
            "Orange",
            "Apple",
            "ORANGE",
            "APPLE"
         ).countByEquality(caseInsensitiveStringEquality) shouldBe
            mapOf("apple" to 3, "Orange" to 2)
      }
   }
}
