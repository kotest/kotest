//package io.kotest.datatest
//
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.spec.style.WordSpec
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.string.shouldHaveLength
//
//@ExperimentalKotest
//internal class WordSpecForAllDataTest : WordSpec() {
//   init {
//
//      // root test
//      withData(
//         PythagTriple(3, 4, 5),
//         PythagTriple(6, 8, 10),
//      ) { (a, b, c) ->
//         a * a + b * b shouldBe c * c
//      }
//
////      // root from sequence
////      withData(
////         sequenceOf(
////            PythagTriple(8, 15, 17),
////            PythagTriple(9, 12, 15),
////            PythagTriple(15, 20, 25),
////         )
////      ) { (a, b, c) ->
////         a * a + b * b shouldBe c * c
////      }
////
////      // nested root tests + sequence
////      withData("a", "b") { a ->
////         withData(sequenceOf("x", "y")) { b ->
////            a + b shouldHaveLength 2
//////            results.add(a + b)
////         }
////      }
//
//      //  afterSpec {
//      //     results.assertDataTestResults()
//      // }
//
////      "inside a when" `when` {
////         // registerContextTests().assertDataTestResults()
////         "inside a should" should {
////            //    registerContextTests().assertDataTestResults()
////            "wibble" {
////
////            }
////         }
////      }
//   }
//}
