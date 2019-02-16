package com.sksamuel.kotlintest.runner.jvm

import io.kotlintest.TestCaseOrder
import io.kotlintest.internal.topLevelTests
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.FunSpec
import io.kotlintest.specs.StringSpec

class TopLevelTestsTest : FunSpec({

  test("spec sequential test ordering") {
    val tests = topLevelTests(SequentialSpec())
    tests.tests.map { it.testCase.name } shouldBe listOf("a", "b", "c", "d", "e")
  }

  test("spec randomized test ordering") {
    val tests1 = topLevelTests(RandomSpec())
    val tests2 = topLevelTests(RandomSpec())
    tests1.tests.map { it.testCase.name } shouldNotBe tests2.tests.map { it.testCase.name }
  }

})

class SequentialSpec : StringSpec() {
  override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential

  init {
    "a" {}
    "b" {}
    "c" {}
    "d" {}
    "e" {}
  }
}

class RandomSpec : StringSpec() {
  override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Random

  init {
    "a" {}
    "b" {}
    "c" {}
    "d" {}
    "e" {}
    "f" {}
    "g" {}
    "h" {}
    "i" {}
    "j" {}
    "k" {}
  }
}