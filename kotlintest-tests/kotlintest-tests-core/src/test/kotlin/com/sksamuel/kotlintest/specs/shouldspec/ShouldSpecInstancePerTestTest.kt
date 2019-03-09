package com.sksamuel.kotlintest.specs.shouldspec

import io.kotlintest.IsolationMode
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

class ShouldSpecInstancePerTestTest : ShouldSpec() {

  override fun isolationMode() = IsolationMode.InstancePerTest

  companion object {
    val invocations = mutableListOf<String>()
  }

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    invocations.joinToString("  ") shouldBe "1  1  1.1  1  1.2  1  1.2  1.2.1  1  1.2  1.2.2  1  1.2  1.2.2  1.2.2.1  1  1.2  1.2.2  1.2.2.2  2  2  2.1  2  2.1  2.1.1  2  2.1  2.1.2"
  }

  init {

    "1" {
      invocations.add("1")
      should("1.1") {
        invocations.add("1.1")
      }
      "1.2" {
        invocations.add("1.2")
        should("1.2.1") {
          invocations.add("1.2.1")
        }
        "1.2.2" {
          invocations.add("1.2.2")
          should("1.2.2.1") {
            invocations.add("1.2.2.1")
          }
          should("1.2.2.2") {
            invocations.add("1.2.2.2")
          }
        }
      }
    }
    "2" {
      invocations.add("2")
      "2.1" {
        invocations.add("2.1")
        should("2.1.1") {
          invocations.add("2.1.1")
        }
        should("2.1.2") {
          invocations.add("2.1.2")
        }
      }
    }
  }
}