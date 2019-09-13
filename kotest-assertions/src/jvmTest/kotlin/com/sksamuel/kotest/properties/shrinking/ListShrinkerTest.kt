package com.sksamuel.kotest.properties.shrinking

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.properties.PropertyTesting
import io.kotest.properties.shrinking.ListShrinker
import io.kotest.specs.WordSpec

class ListShrinkerTest : WordSpec({

   beforeSpec {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   afterSpec {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

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
      ListShrinker<String>().shrink(listOf("a", "b", "c", "d", "e", "f")).shouldContain(listOf("a", "b", "c", "d"))
    }
  }
})
