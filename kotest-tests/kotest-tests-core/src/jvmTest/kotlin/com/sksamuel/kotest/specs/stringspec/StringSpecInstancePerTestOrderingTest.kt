package com.sksamuel.kotest.specs.stringspec

import io.kotest.IsolationMode
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.shouldBe
import io.kotest.specs.StringSpec
import java.util.concurrent.atomic.AtomicInteger

class StringSpecInstancePerTestOrderingTest : StringSpec() {

  companion object {
    var string = ""
  }

  override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerTest

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