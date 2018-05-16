package com.sksamuel.kotlintest.runner.jvm

import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.TestCaseOrder
import io.kotlintest.runner.jvm.SharedInstanceSpecRunner
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.specs.WordSpec

class SpecRunnerTest : WordSpec({
  "SpecRunner" should {
    "support sequential order to order tests" {
      val listener = mock<TestEngineListener> {}
      val runner = SharedInstanceSpecRunner(listener)
      val tests = runner.topLevelTests(SequentialSpec())
      tests.map { it.name } shouldBe listOf("a", "b", "c", "d", "e")
    }
    "support randomized order to order tests" {
      val listener = mock<TestEngineListener> {}
      val runner = SharedInstanceSpecRunner(listener)
      val tests1 = runner.topLevelTests(RandomSpec())
      val tests2 = runner.topLevelTests(RandomSpec())
      tests1.map { it.name } shouldNotBe tests2.map { it.name }
    }
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
  }
}