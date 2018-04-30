package com.sksamuel.kotlintest.tests

import io.kotlintest.Description
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
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
    "append" {
      Description.root("a").append("b") shouldBe Description(listOf("a"), "b")
      Description(listOf("a"), "b").append("c") shouldBe Description(listOf("a", "b"), "c")
      Description(listOf("a", "b"), "c").append("d") shouldBe Description(listOf("a", "b", "c"), "d")
    }
    "isParentOf" {
      Description(listOf("a", "b"), "c").isParentOf(Description(listOf("a", "b", "c"), "d")).shouldBeTrue()
      Description(listOf("a"), "b").isParentOf(Description(listOf("a", "b"), "c")).shouldBeTrue()
      Description.root("a").isParentOf(Description(listOf("a"), "b")).shouldBeTrue()

      Description.root("a").isParentOf(Description.root("a")).shouldBeFalse()
      Description.root("a").isParentOf(Description(listOf("b"), "a")).shouldBeFalse()
      Description.root("a").isParentOf(Description(listOf("b", "a"), "c")).shouldBeFalse()
      Description.root("a").isParentOf(Description(listOf("a", "b"), "c")).shouldBeFalse()
      Description.root("a").isParentOf(Description.root("a")).shouldBeFalse()
      Description(listOf("a"), "b").isParentOf(Description(listOf("a", "b", "c"), "d")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isParentOf(Description(listOf("a"), "b")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isParentOf(Description(listOf("a", "b", "c"), "d")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isParentOf(Description.root("a")).shouldBeFalse()
    }
    "isAncestorOf" {
      Description(listOf("a", "b"), "c").isAncestorOf(Description(listOf("a", "b", "c"), "d")).shouldBeTrue()
      Description(listOf("a"), "b").isAncestorOf(Description(listOf("a", "b", "c"), "d")).shouldBeTrue()
      Description(listOf("a"), "b").isAncestorOf(Description(listOf("a", "b"), "c")).shouldBeTrue()
      Description.root("a").isAncestorOf(Description(listOf("a"), "b")).shouldBeTrue()
      Description.root("a").isAncestorOf(Description(listOf("a", "b"), "c")).shouldBeTrue()
      Description.root("a").isAncestorOf(Description(listOf("a", "b", "c"), "d")).shouldBeTrue()

      Description(listOf("a"), "b").isAncestorOf(Description(listOf("a"), "b")).shouldBeFalse()
      Description(listOf("a"), "b").isAncestorOf(Description(listOf("b", "a"), "c")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isAncestorOf(Description(listOf("a"), "b")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isAncestorOf(Description(listOf("a", "b", "c"), "d")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d").isAncestorOf(Description.root("a")).shouldBeFalse()
    }
  }
}