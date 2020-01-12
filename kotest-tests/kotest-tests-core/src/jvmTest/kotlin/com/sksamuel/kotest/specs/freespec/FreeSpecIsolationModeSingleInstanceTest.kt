//package com.sksamuel.kotest.specs.freespec
//
//import io.kotest.core.test.Description
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.spec.SpecConfiguration
//import io.kotest.shouldBe
//import io.kotest.specs.FreeSpec
//
//class FreeSpecIsolationModeSingleInstanceTest : FreeSpec() {
//
//  companion object {
//    var string = ""
//  }
//
//  override fun isolationMode() = IsolationMode.SingleInstance
//
//  override fun afterSpecCompleted(description: Description, spec: SpecConfiguration) {
//    string shouldBe "abcccde_"
//  }
//
//  init {
//    "a" - {
//      string += "a"
//      "b" - {
//        string += "b"
//        // since we execute this test 3 times, and we are in single instance mode,
//        // the letter c should appear 3 times in turn
//        "c".config(invocations = 3) {
//          string += "c"
//        }
//      }
//      "d" - {
//        string += "d"
//        "e" {
//          string += "e"
//        }
//      }
//      string += "_"
//    }
//  }
//}
