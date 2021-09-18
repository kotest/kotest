//package io.kotest.datatest
//
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.spec.style.FreeSpec
//
//@ExperimentalKotest
//class FreeSpecForAllDataClassDataTest : FreeSpec() {
//   init {
//
//      val results = registerRootTests()
//
//      afterSpec {
//         results.assertDataTestResults()
//      }
//
//      "inside a context" - {
//         registerContextTests().assertDataTestResults()
//         "inside another context" - {
//            registerContextTests().assertDataTestResults()
//         }
//      }
//   }
//}
