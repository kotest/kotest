package io.kotlintest

import io.kotlintest.specs.StringSpec

class TestCaseTest : StringSpec() {

  object TagA : Tag()
  object TagB : Tag()

  init {
    val testTaggedA: TestCase = "should be tagged with tagA" { }
    testTaggedA.config(tag = TagA)

    val untaggedTest = "should be untagged" { }

    val testTaggedB = "should be tagged with tagB" { }
    testTaggedB.config(tag = TagB)

    "only tests without excluded tags should be active" {
      System.setProperty("excludeTags", "TagB")
      testTaggedA.isActive shouldBe true
      untaggedTest.isActive shouldBe true
      testTaggedB.isActive shouldBe false
    }

    "only tests with included tags should be active" {
      System.setProperty("includeTags", "TagA")
      testTaggedA.isActive shouldBe true
      untaggedTest.isActive shouldBe false
      testTaggedB.isActive shouldBe false
    }.config(tag = TagA)
  }

  override fun afterEach() {
    System.clearProperty("excludeTags")
    System.clearProperty("includeTags")
  }
}