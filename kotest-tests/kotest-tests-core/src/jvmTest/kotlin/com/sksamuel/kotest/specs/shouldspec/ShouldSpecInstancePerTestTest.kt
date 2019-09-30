package com.sksamuel.kotest.specs.shouldspec

import io.kotest.IsolationMode
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.shouldBe
import io.kotest.specs.ShouldSpec

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