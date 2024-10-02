package io.kotest.matchers.collections

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.equals.Equality
import io.kotest.equals.EqualityResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

@EnabledIf(LinuxCondition::class)
class MatchCollectionsWithVerifierTest : StringSpec() {
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
      "match" {
         matchCollectionsWithVerifier(
            listOf("Apple", "ORANGE", "apple"),
            listOf("Apple", "orange", "APPLE"),
            caseInsensitiveStringEquality
         ) shouldBe null
      }
      "actual is too long" {
         matchCollectionsWithVerifier(
            listOf("Apple", "ORANGE", "apple"),
            listOf("Apple", "orange"),
            caseInsensitiveStringEquality
         ) shouldBe CollectionMismatchWithCustomVerifier("Actual has an element at index 2, expected is shorter")
      }
      "expected is too long" {
         matchCollectionsWithVerifier(
            listOf("Apple", "ORANGE"),
            listOf("Apple", "orange", "apple"),
            caseInsensitiveStringEquality
         ) shouldBe CollectionMismatchWithCustomVerifier("Expected has an element at index 2, actual is shorter")
      }
      "elements mismatch at index 2" {
         matchCollectionsWithVerifier(
            listOf("Apple", "ORANGE", "orange"),
            listOf("Apple", "orange", "apple"),
            caseInsensitiveStringEquality
         )!!.message shouldStartWith "Elements differ at index 2, expected: <\"apple\">, but was <\"orange\">,  \"apple\" is not equal to \"orange\" by Case Insensitive String Matcher"
      }
   }
}
