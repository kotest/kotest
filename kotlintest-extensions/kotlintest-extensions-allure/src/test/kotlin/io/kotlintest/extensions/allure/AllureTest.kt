package io.kotlintest.extensions.allure

import io.kotlintest.matchers.haveLength
import io.kotlintest.specs.WordSpec
import io.qameta.allure.SeverityLevel

class AllureTest : WordSpec() {

  override fun listeners() = listOf(AllureExtension)

  init {

    "this is a test scope" should {
      "this is a test case" {
        "sammy" should haveLength(5)
      }
    }

    "this test" should {
      "have severity" {
        putMetaData("Severity", Severity(SeverityLevel.CRITICAL))
        "bobby" should haveLength(5)
      }
    }
  }
}