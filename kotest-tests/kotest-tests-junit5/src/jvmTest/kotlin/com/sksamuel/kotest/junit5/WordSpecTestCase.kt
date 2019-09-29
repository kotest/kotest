package com.sksamuel.kotest.junit5

import io.kotest.shouldBe
import io.kotest.specs.WordSpec

@Suppress("UNREACHABLE_CODE")
class WordSpecTestCase : WordSpec({

  "a container" should {
    "skip a test".config(enabled = false) {}
    "fail a test" { 1 shouldBe 2 }
    "pass a test" { 1 shouldBe 1 }
    "error" { throw RuntimeException() }
  }

  "an empty when container" should {

  }

  "an empty should container" should {

  }

  "this when container" `when` {
    "contain an empty should container" should {

    }
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
      "skip a test".config(enabled = false) {}
    }
  }

  "a failing container" should {
    throw RuntimeException()
    "not reach this test" {}
  }
})