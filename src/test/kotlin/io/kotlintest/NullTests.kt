package io.kotlintest

import io.kotlintest.specs.WordSpec

class NullTests : WordSpec() {

  // don't want compiler to compile this away
  fun getNull(): String? = if (System.currentTimeMillis() > 1234) null else throw RuntimeException()

  fun notNull(): String? = if (System.currentTimeMillis() > 1234) "qwerty" else throw RuntimeException()

  init {
    "null" should {
      "not match value" {
        shouldThrow<TestFailedException> {
          getNull() shouldBe "q"
        }
      }
      "match null" {
        getNull() shouldBe null
      }
    }
    "not null" should {
      "match value" {
        notNull() shouldBe "qwerty"
      }
      "not match null" {
        shouldThrow<TestFailedException> {
          notNull() shouldBe null
        }
      }
    }
  }
}