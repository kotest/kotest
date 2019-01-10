package com.sksamuel.kotlintest.specs.stringspec

import io.kotlintest.IsolationMode
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.concurrent.atomic.AtomicInteger

class StringSpecInstancePerLeafOrderingTest : StringSpec() {

  companion object {
    var string = ""
  }

  override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerLeaf

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
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