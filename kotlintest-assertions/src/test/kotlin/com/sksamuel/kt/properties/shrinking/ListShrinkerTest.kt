package com.sksamuel.kt.properties.shrinking

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldHaveElementAt
import io.kotlintest.properties.shrinking.ListShrinker
import io.kotlintest.specs.WordSpec

class ListShrinkerTest : WordSpec({

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