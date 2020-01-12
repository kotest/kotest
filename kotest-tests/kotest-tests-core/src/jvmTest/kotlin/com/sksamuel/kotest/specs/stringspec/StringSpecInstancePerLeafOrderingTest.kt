package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.IsolationMode
import io.kotest.SpecClass
import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.shouldBe
import io.kotest.specs.StringSpec
import java.util.concurrent.atomic.AtomicInteger

class StringSpecInstancePerLeafOrderingTest : StringSpec() {

  companion object {
    var string = ""
  }

  override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerLeaf

  override fun afterSpecClass(spec: SpecClass, results: Map<TestCase, TestResult>) {
    string shouldBe "a_z_b_y_c_"
  }

  private var uniqueCount = AtomicInteger(0)

  init {
    "a" {
      string += "a_"
    }

    "z" {
      string += "z_"
    }

    "b" {
      string += "b_"
    }

    "y" {
      string += "y_"
    }

    "c" {
      string += "c_"
    }
  }
}
