//package io.kotest.extensions.allure
//
//import io.kotest.matchers.string.haveLength
//import io.kotest.specs.WordSpec
//import io.qameta.allure.SeverityLevel
//
//class AllureTest : WordSpec() {
//
//  override fun listeners() = listOf(AllureExtension)
//
//  init {
//
//    "this is a test scope" should {
//      "this is a test case" {
//        "sammy" should haveLength(5)
//      }
//    }
//
//    "this test" should {
//      "have severity" {
//        putMetaData("Severity", Severity(SeverityLevel.CRITICAL))
//        "bobby" should haveLength(5)
//      }
//    }
//  }
//}
