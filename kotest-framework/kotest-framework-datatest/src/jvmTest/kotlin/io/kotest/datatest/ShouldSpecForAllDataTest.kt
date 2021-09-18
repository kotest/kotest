//package io.kotest.datatest
//
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.spec.style.ShouldSpec
//
//@ExperimentalKotest
//internal class ShouldSpecForAllDataTest : ShouldSpec() {
//   init {
//
//      val results = registerRootTests()
//
//      afterSpec {
//         results.assertDataTestResults()
//      }
//
//      should("inside a should") {
//         registerContextTests().assertDataTestResults()
//      }
//
//      context("inside a context") {
//         registerContextTests().assertDataTestResults()
//         context("inside another context") {
//            registerContextTests().assertDataTestResults()
//            should("inside a contexted should") {
//               registerContextTests().assertDataTestResults()
//            }
//         }
//      }
//   }
//}
