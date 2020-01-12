//package com.sksamuel.kotest.specs.shouldspec
//
//import io.kotest.core.IsolationMode
//import io.kotest.SpecClass
//import io.kotest.core.TestCase
//import io.kotest.core.TestResult
//import io.kotest.core.spec.SpecConfiguration
//import io.kotest.shouldBe
//import io.kotest.specs.ShouldSpec
//
//class ShouldSpecInstancePerLeafTest : ShouldSpec() {
//
//  override fun isolationMode() = IsolationMode.InstancePerLeaf
//
//  companion object {
//    val invocations = mutableListOf<String>()
//  }
//
//  override fun finalizeSpec(spec: SpecConfiguration, results: Map<TestCase, TestResult>) {
//    invocations.joinToString("  ") shouldBe "1  1.1  1  1.2  1.2.1  1  1.2  1.2.2  1.2.2.1  1  1.2  1.2.2  1.2.2.2  2  2.1  2.1.1  2  2.1  2.1.2"
//  }
//
//  init {
//
//    "1" {
//      invocations.add("1")
//      should("1.1") {
//        invocations.add("1.1")
//      }
//      "1.2" {
//        invocations.add("1.2")
//        should("1.2.1") {
//          invocations.add("1.2.1")
//        }
//        "1.2.2" {
//          invocations.add("1.2.2")
//          should("1.2.2.1") {
//            invocations.add("1.2.2.1")
//          }
//          should("1.2.2.2") {
//            invocations.add("1.2.2.2")
//          }
//        }
//      }
//    }
//    "2" {
//      invocations.add("2")
//      "2.1" {
//        invocations.add("2.1")
//        should("2.1.1") {
//          invocations.add("2.1.1")
//        }
//        should("2.1.2") {
//          invocations.add("2.1.2")
//        }
//      }
//    }
//  }
//}
