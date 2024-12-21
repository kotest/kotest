//package io.kotest.datatest.styles
//
//import io.kotest.core.spec.style.FeatureSpec
//import io.kotest.datatest.assertDataTestResults
//import io.kotest.datatest.registerContextTests
//import io.kotest.datatest.registerRootTests
//import io.kotest.matchers.shouldBe
//
//class FeatureSpecDataTest : FeatureSpec() {
//   init {
//
//      val results = registerRootTests()
//      var count = 0
//
//      afterTest {
//         count++
//      }
//
//      afterSpec {
//         results.assertDataTestResults()
//         count shouldBe 110
//      }
//
//      feature("inside a feature") {
//         registerContextTests().assertDataTestResults()
//         feature("inside another feature") {
//            registerContextTests().assertDataTestResults()
//         }
//      }
//
//      feature("a feature should allow nullable maps") {
//      }
//   }
//}
