package com.sksamuel.kotest.properties.shrinking

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.properties.PropertyTesting
import io.kotest.properties.shrinking.ListShrinker

class ListShrinkerTest : WordSpec() {

   override fun afterSpec(spec: Spec) {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

   override fun beforeSpec(spec: Spec) {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   init {
      "ListShrinker" should {
         "return empty list for the min value" {
            ListShrinker<String>().shrink(listOf()).shouldBeEmpty()
         }
         "include emptyList as the first candidate" {
            ListShrinker<String>().shrink(listOf("a", "b")).shouldHaveElementAt(0, emptyList())
         }
         "include list of single last element" {
            ListShrinker<String>().shrink(listOf("a", "b", "c")).shouldContain(listOf("c"))
         }
         "include list without last element" {
            ListShrinker<String>().shrink(listOf("a", "b", "c")).shouldContain(listOf("a", "b"))
         }
         "include 1/3" {
            ListShrinker<String>().shrink(listOf("a", "b", "c", "d", "e", "f")).shouldContain(listOf("a", "b"))
         }
         "include 1/2" {
            val candidates = ListShrinker<String>().shrink(listOf("a", "b", "c", "d"))
            candidates.shouldContain(listOf("a", "b"))
         }
         "include 2/3" {
            ListShrinker<String>().shrink(listOf("a", "b", "c", "d", "e", "f"))
               .shouldContain(listOf("a", "b", "c", "d"))
         }
      }
   }
}
