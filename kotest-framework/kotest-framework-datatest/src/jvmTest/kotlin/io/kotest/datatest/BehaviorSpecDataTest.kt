//package io.kotest.datatest
//
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.spec.style.BehaviorSpec
//
//@ExperimentalKotest
//internal class BehaviorSpecDataTest : BehaviorSpec() {
//   init {
//
//      val results = registerRootTests()
//
//      afterSpec {
//         results.assertDataTestResults()
//      }
//
//      given("inside a given") {
//         registerContextTests().assertDataTestResults()
//         and("inside an and") {
//            registerContextTests().assertDataTestResults()
//         }
//         When("inside a when") {
//            registerContextTests().assertDataTestResults()
//            and("inside an and") {
//               registerContextTests().assertDataTestResults()
//            }
//            then("inside a then") {
//               registerContextTests().assertDataTestResults()
//            }
//         }
//      }
//   }
//}
