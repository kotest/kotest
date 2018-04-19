package com.sksamuel.kotlintest.tests

import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class DescriptionTest : StringSpec() {
  init {
    "parents" {
      Description(listOf("a", "b", "c"), "d").parent() shouldBe Description(listOf("a", "b"), "c")
      Description(listOf("a"), "b").parent() shouldBe Description(listOf(), "a")
      Description(listOf(), "a").parent() shouldBe null
    }
    "full name" {
      Description(listOf("a", "b", "c"), "d").fullName() shouldBe "a b c d"
      Description(listOf("a"), "b").fullName() shouldBe "a b"
      Description(listOf(), "a").fullName() shouldBe "a"
    }
  }
}