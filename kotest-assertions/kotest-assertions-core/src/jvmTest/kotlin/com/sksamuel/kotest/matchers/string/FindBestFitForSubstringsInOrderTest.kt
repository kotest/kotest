package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.string.findBestFitForSubstringsInOrder

class FindBestFitForSubstringsInOrderTest : StringSpec() {
   val shortText = "The quick brown fox jumps over the lazy dog"
   val longText = """
      Lorem ipsum reads: Lorem ipsum dolor sit amet,
      consectetur adipiscing elit, sed do eiusmod tempor incididunt
      ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis
      nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat
   """.trimIndent()
   init {
       "finds a few substrings in order" {
          findBestFitForSubstringsInOrder(
             shortText, listOf("quick", "fox", "over"), { 1 }
          ).bestFitIndexes shouldContainExactly listOf(0, 1, 2)
       }
      "finds all words in short text in order" {
         findBestFitForSubstringsInOrder(
            shortText, shortText.split(" "), { 1 }
         ).bestFitIndexes shouldContainExactly listOf(0, 1, 2, 3, 4, 5, 6, 7, 8,)
      }
      "finds best match when mismatch in the beginning" {
         val allWords = shortText.split(" ").replaceElement(0, "wolf")
         findBestFitForSubstringsInOrder(
            shortText, allWords, { 1 }
         ).bestFitIndexes shouldContainExactly listOf(1, 2, 3, 4, 5, 6, 7, 8,)
      }
      "finds best match when mismatch in the middle" {
         val allWords = shortText.split(" ").replaceElement(4, "wolf")
         findBestFitForSubstringsInOrder(
            shortText, allWords, { 1 }
         ).bestFitIndexes shouldContainExactly listOf(0, 1, 2, 3, 5, 6, 7, 8,)
      }
      "finds best match when mismatch in the end" {
         val allWords = shortText.split(" ").replaceElement(8, "wolf")
         findBestFitForSubstringsInOrder(
            shortText, allWords, { 1 }
         ).bestFitIndexes shouldContainExactly listOf(0, 1, 2, 3, 4, 5, 6, 7,)
      }
      "finds some words in long text in order" {
         findBestFitForSubstringsInOrder(
            longText, listOf("Lorem", "ipsum", "dolor", "elit", "sed", "do", "eiusmod", "tempor", "incididunt"
            ), { 1 }
         ).bestFitIndexes shouldContainExactly listOf(0, 1, 2, 3, 4, 5, 6, 7, 8,)
      }
       "finds all words in long text in order" {
          val allWords = longText.split(" ")
          findBestFitForSubstringsInOrder(
             longText, allWords, { 1 }
          ).bestFitIndexes shouldContainExactly (0 until allWords.size).toList()
       }
      "finds all words except one in long text in order" {
         val indexToReplace = 7
         val allWords = longText.split(" ").replaceElement(indexToReplace, "NOT_IN_TEXT")
         findBestFitForSubstringsInOrder(
            longText, allWords, { 1 }
         ).bestFitIndexes shouldContainExactly (0 until allWords.size).toList().filter { it != indexToReplace }
      }
   }
}

private fun List<String>.replaceElement(index: Int, value: String) =
   this.mapIndexed { i, s -> if (i == index) value else s }
