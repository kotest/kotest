package com.sksamuel.kotest.junit5

import io.kotest.shouldBe
import io.kotest.specs.FreeSpec

class FreeSpecTestCase : FreeSpec({

  "a simple failing test" {
    1 shouldBe 2
  }

  "a simple passing test" {
    1 shouldBe 1
  }

  "a simple erroring test" {
    throw RuntimeException()
  }

  "a simple skipped test".config(enabled = false) {}

  "a container with" - {
    "a failing test" {
      1 shouldBe 2
    }

    "a passing test" {
      1 shouldBe 1
    }

    "a erroring test" {
      throw RuntimeException()
    }

    "a skipped test".config(enabled = false) {}
  }

  "an outer container with" - {
    "an inner container with" - {
      "a failing test" {
        1 shouldBe 2
      }

      "a passing test" {
        1 shouldBe 1
      }

      "a erroring test" {
        throw RuntimeException()
      }

      "a skipped test".config(enabled = false) {}
    }
  }

  "an empty outer container with" - {
  }

  "an outer container that conatins" - {
    "an empty inner container" - {

    }
  }

  "an outer container with only passing tests" - {
    "a passing test 1" {
      1 shouldBe 1
    }
    "a passing test 2" {
      2 shouldBe 2
    }
  }

})