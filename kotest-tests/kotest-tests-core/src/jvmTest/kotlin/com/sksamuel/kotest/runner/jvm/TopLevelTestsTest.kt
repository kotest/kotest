package com.sksamuel.kotest.runner.jvm

import io.kotest.TestCaseOrder
import io.kotest.internal.topLevelTests
import io.kotest.shouldBe
import io.kotest.shouldNotBe
import io.kotest.specs.FunSpec
import io.kotest.specs.StringSpec

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