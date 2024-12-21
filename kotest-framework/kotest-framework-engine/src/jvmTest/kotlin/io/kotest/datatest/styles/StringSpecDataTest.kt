//package io.kotest.datatest.styles
//
//import io.kotest.core.names.DuplicateTestNameMode
//import io.kotest.core.spec.style.StringSpec
//import io.kotest.datatest.assertDataTestResults
//import io.kotest.datatest.registerRootTests
//import io.kotest.matchers.shouldBe
//
//class StringSpecDataTest : StringSpec() {
//   init {
//
//      duplicateTestNameMode = DuplicateTestNameMode.Silent
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
//         count shouldBe 36
//      }
//   }
//}
