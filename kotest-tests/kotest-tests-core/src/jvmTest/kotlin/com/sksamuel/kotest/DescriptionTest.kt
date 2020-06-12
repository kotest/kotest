package com.sksamuel.kotest

import io.kotest.core.test.Description
import io.kotest.core.spec.CompositeSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val descriptionTests = stringSpec {
   "parents" {
      Description(listOf("a", "b", "c"), "d").parent() shouldBe Description(listOf("a", "b"), "c")
      Description(listOf("a"), "b").parent() shouldBe Description(listOf(), "a")
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
      Description.spec("a").isAncestorOf(Description.spec("a").append("prefix", "b")).shouldBeTrue()
      Description.spec("a").isAncestorOf(Description.spec("a").append(null, "b")).shouldBeTrue()

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
      Description.spec("a").isAncestorOf(Description.spec("a").append("prefix", "b")).shouldBeTrue()
      Description.spec("a").isAncestorOf(Description.spec("a").append(null, "b")).shouldBeTrue()
      Description.spec("a").isAncestorOf(Description.spec("a").append("prefix", "b").append(null, "b")).shouldBeTrue()
      Description.spec("a").isAncestorOf(Description.spec("a").append(null, "b").append("prefix", "b")).shouldBeTrue()

      Description(listOf("a"), "b")
         .isAncestorOf(Description(listOf("a"), "b")).shouldBeFalse()
      Description(listOf("a"), "b")
         .isAncestorOf(Description(listOf("b", "a"), "c")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d")
         .isAncestorOf(Description(listOf("a"), "b")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d")
         .isAncestorOf(Description(listOf("a", "b", "c"), "d")).shouldBeFalse()
      Description(listOf("a", "b", "c"), "d")
         .isAncestorOf(Description.spec("a")).shouldBeFalse()
   }
}

class DescriptionTest : CompositeSpec(descriptionTests)
