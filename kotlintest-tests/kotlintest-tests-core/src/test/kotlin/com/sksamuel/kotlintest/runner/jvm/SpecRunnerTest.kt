package com.sksamuel.kotlintest.runner.jvm

import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.TestCaseOrder
import io.kotlintest.runner.jvm.SpecExecutor
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.specs.WordSpec

class SpecRunnerTest : WordSpec({

  "SpecRunner" should {

    "support sequential order to order tests" {
      val listener = mock<TestEngineListener> {}
      val executor = SpecExecutor(listener)
      val (active, _) = executor.topLevelTests(SequentialSpec())
      active.map { it.name } shouldBe listOf("a", "b", "c", "d", "e")
    }

    "support randomized order to order tests" {
      val listener = mock<TestEngineListener> {}
      val executor = SpecExecutor(listener)
      val (active1, _) = executor.topLevelTests(RandomSpec())
      val (active2, _) = executor.topLevelTests(RandomSpec())
      active1.map { it.name } shouldNotBe active2.map { it.name }
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
    "j" {}
    "k" {}
    "l" {}
    "m" {}
  }
}