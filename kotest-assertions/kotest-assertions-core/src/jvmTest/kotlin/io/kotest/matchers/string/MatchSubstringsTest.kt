package io.kotest.matchers.string

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class MatchSubstringsTest : StringSpec() {
   private val value = "The quick brown fox jumps over the lazy dog"
   private val words = listOf("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog")

   init {
      "matchSubstrings returns Match if not substrings" {
         matchSubstrings(value, listOf()) shouldBe ContainInOrderOutcome.Match
      }
      "matchSubstrings returns Match if one substring matches" {
         words.forEach { substring ->
            matchSubstrings(value, listOf(substring)) shouldBe ContainInOrderOutcome.Match
         }
      }
      "matchSubstrings returns Match if all even substrings match" {
         val evenWords = words.filterIndexed { index, _ -> index % 2 == 0 }
         matchSubstrings(value, evenWords) shouldBe ContainInOrderOutcome.Match
      }
      "matchSubstrings returns Match if all even substrings match, including lots of empty strings" {
         val sparseWords = words.flatMap { word -> listOf("", word, "") }
         matchSubstrings(value, sparseWords) shouldBe ContainInOrderOutcome.Match
      }
      "matchSubstrings returns Match if all odd substrings match" {
         val oddWords = words.filterIndexed { index, _ -> index % 2 == 1 }
         matchSubstrings(value, oddWords) shouldBe ContainInOrderOutcome.Match
      }
      "matchSubstrings returns Match if all substrings match" {
         matchSubstrings(value, words) shouldBe ContainInOrderOutcome.Match
      }
      "matchSubstrings returns Match if all substrings match without gaps between them" {
         matchSubstrings("1234567890", listOf("1", "23", "456", "789", "0")) shouldBe ContainInOrderOutcome.Match
      }
      "matchSubstrings returns mismatch on every substring" {
         words.indices.toList().forAll { index ->
            val tokenNotInValue = "green"
            matchSubstrings(value, replaceWord(words, tokenNotInValue, index)) shouldBe ContainInOrderOutcome.Mismatch(
               tokenNotInValue, index
            )
         }
      }
      "matchSubstrings returns mismatch if every substring matches but not in order" {
         words.indices.drop(1).toList().forAll { index ->
            matchSubstrings(value, swapWordWithPrevious(words, index)) shouldBe
               ContainInOrderOutcome.Mismatch(words[index - 1], index)
         }
      }
   }

   private fun replaceWord(words: List<String>, newWord: String, index: Int): List<String> {
      words.toMutableList().run {
         this[index] = newWord
         return this.toList()
      }
   }

   private fun swapWordWithPrevious(words: List<String>, index: Int): List<String> {
      words.toMutableList().run {
         val wordToSwap = this[index]
         this[index] = this[index - 1]
         this[index - 1] = wordToSwap
         return this.toList()
      }
   }
}
