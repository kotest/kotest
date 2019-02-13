package com.sksamuel.kotlintest.junit5

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class WordSpecTestCase : WordSpec({

  "a container" should {
    "skip a test".config(enabled = false) {}
    "fail a test" { 1 shouldBe 2 }
    "pass a test" { 1 shouldBe 1 }
    "error" { throw RuntimeException() }
  }

  "an empty container" should {

  }

  "a when container with a failing test" `when` {
    "with a should container" should {
      "fail a test" { 1 shouldBe 2 }
      "pass a test" { 1 shouldBe 1 }
    }
  }

  "a when container" `when` {
    "with a should container" should {
      "pass a test" { 1 shouldBe 1 }
    }
  }

  "a failing container" should {
    throw RuntimeException()
    "not reach this test" {}
  }
})