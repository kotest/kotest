package com.sksamuel.kotest

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class NullTests : WordSpec() {

  // don't want compiler to compile this away
  private fun getNull(): String? = if (System.currentTimeMillis() > 1234) null else throw RuntimeException()

  private fun notNull(): String? = if (System.currentTimeMillis() > 1234) "qwerty" else throw RuntimeException()

  init {

    "null" should {
      "not match value" {
        shouldThrow<AssertionError> {
          getNull() shouldBe "q"
        }
      }
      "match null" {
        getNull() shouldBe null
      }
      "match null variable when equal operation is override" {
        val g: A? = null
        A(0) shouldBe g
      }
    }
    "not null" should {
      "match value" {
        notNull() shouldBe "qwerty"
      }
      "not match null" {
        shouldThrow<AssertionError> {
          notNull() shouldBe null
        }
      }
    }
  }
}

private class A(var i: Int) {
  override fun equals(other: Any?): Boolean = other == null && i == 0
}
