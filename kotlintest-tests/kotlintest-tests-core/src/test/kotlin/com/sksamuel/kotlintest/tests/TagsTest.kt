package com.sksamuel.kotlintest.tests

import io.kotlintest.Description
import io.kotlintest.Tag
import io.kotlintest.TestResult
import io.kotlintest.specs.StringSpec

class TagsTest : StringSpec() {

  object TagA : Tag()
  object TagB : Tag()

  init {

    val testTaggedA = "should be tagged with tagA".config(tags = setOf(TagA)) { }

    val untaggedTest = "should be untagged" { }

    val testTaggedB = "should be tagged with tagB".config(tags = setOf(TagB)) { }

    "test exclude system property" {
      System.setProperty("kotlintest.tags.exclude", "TagB")
      //   testTaggedA.isActive() shouldBe true
      //   untaggedTest.isActive() shouldBe true
      //    testTaggedB.isActive() shouldBe false
    }

    "test include system property" {
      System.setProperty("kotlintest.tags.include", "TagA")
      //    testTaggedA.isActive() shouldBe true
      //    untaggedTest.isActive() shouldBe false
      //    testTaggedB.isActive() shouldBe false
    }

    "all tests should be active by default" {
      //   testTaggedA.isActive() shouldBe true
      //    untaggedTest.isActive() shouldBe true
      //    testTaggedB.isActive() shouldBe true
    }
  }

  override fun afterTest(description: Description, result: TestResult) {
    System.clearProperty("kotlintest.tags.exclude")
    System.clearProperty("kotlintest.tags.include")
  }
}