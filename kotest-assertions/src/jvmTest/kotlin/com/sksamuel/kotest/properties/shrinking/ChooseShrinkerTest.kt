package com.sksamuel.kotest.properties.shrinking

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.properties.PropertyTesting
import io.kotest.properties.shrinking.ChooseShrinker
import io.kotest.specs.WordSpec

class ChooseShrinkerTest : WordSpec({

   beforeSpec {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   afterSpec {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

  "ChooseShrinker" should {
    "return empty list for the min value" {
      ChooseShrinker(100, 200).shrink(100).shouldBeEmpty()
    }
    "include min value as the first candidate" {
      ChooseShrinker(100, 200).shrink(55).shouldHaveElementAt(0, 100)
    }
    "include 5 smaller sizes" {
      val candidates = ChooseShrinker(40, 60).shrink(56)
      candidates.shouldContainAll(55, 54, 53, 52, 51)
    }
    "include 1/3" {
      val candidates = ChooseShrinker(10, 60).shrink(56)
      candidates.shouldContain(18)
    }
    "exclude 1/3 if too small" {
      val candidates = ChooseShrinker(40, 60).shrink(51)
      candidates.shouldNotContain(17)
    }
    "include 1/2" {
      val candidates = ChooseShrinker(10, 60).shrink(56)
      candidates.shouldContain(28)
    }
    "exclude 1/2 if too small" {
      val candidates = ChooseShrinker(40, 60).shrink(50)
      candidates.shouldNotContain(25)
    }
    "include 2/3" {
      val candidates = ChooseShrinker(10, 60).shrink(56)
      candidates.shouldContain(37)
    }
    "exclude 2/3 if too small" {
      val candidates = ChooseShrinker(40, 60).shrink(51)
      candidates.shouldNotContain(34)
    }
  }
})
