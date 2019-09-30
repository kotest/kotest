package com.sksamuel.kotest

import io.kotest.Description
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

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
    "append" {
      Description.spec("a").append("b") shouldBe Description(listOf("a"), "b")
      Description(listOf("a"), "b").append("c") shouldBe Description(listOf("a", "b"), "c")
      Description(listOf("a", "b"), "c").append("d") shouldBe Description(listOf("a", "b", "c"), "d")
    }
    "isParentOf" {
      Description(listOf("a", "b"), "c").isParentOf(Description(listOf("a", "b", "c"), "d")).shouldBeTrue()
      Description(listOf("a"), "b").isParentOf(Description(listOf("a", "b"), "c")).shouldBeTrue()
      Description.spec("a").isParentOf(Description(listOf("a"), "b")).shouldBeTrue()

      Description.spec("a").isParentOf(Description.spec("a")).shouldBeFalse()
      Description.spec("a").isParentOf(Description(listOf("b"), "a")).shouldBeFalse()
      Description.spec("a").isParentOf(Description(listOf("b", "a"), "c")).shouldBeFalse()
      Description.spec("a").isParentOf(Description(listOf("a", "b"), "c")).shouldBeFalse()
      Description.spec("a").isParentOf(Description.spec("a")).shouldBeFalse()
      Description(listOf("a"), "b").isParentOf(Description(listOf("a", "b", "c"), "d")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isParentOf(Description(listOf("a"), "b")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isParentOf(Description(listOf("a", "b", "c"), "d")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isParentOf(Description.spec("a")).shouldBeFalse()
    }
    "isAncestorOf" {
      Description(listOf("a", "b"), "c").isAncestorOf(Description(listOf("a", "b", "c"), "d")).shouldBeTrue()
      Description(listOf("a"), "b").isAncestorOf(Description(listOf("a", "b", "c"), "d")).shouldBeTrue()
      Description(listOf("a"), "b").isAncestorOf(Description(listOf("a", "b"), "c")).shouldBeTrue()
      Description.spec("a").isAncestorOf(Description(listOf("a"), "b")).shouldBeTrue()
      Description.spec("a").isAncestorOf(Description(listOf("a", "b"), "c")).shouldBeTrue()
      Description.spec("a").isAncestorOf(Description(listOf("a", "b", "c"), "d")).shouldBeTrue()

      Description(listOf("a"), "b").isAncestorOf(Description(listOf("a"), "b")).shouldBeFalse()
      Description(listOf("a"), "b").isAncestorOf(Description(listOf("b", "a"), "c")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isAncestorOf(Description(listOf("a"), "b")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isAncestorOf(Description(listOf("a", "b", "c"), "d")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isAncestorOf(Description.spec("a")).shouldBeFalse()
    }
  }
}
