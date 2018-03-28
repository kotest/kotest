package io.kotlintest.extensions.allure

import io.kotlintest.extensions.junitxml.JUnitXmlListener
import io.kotlintest.matchers.haveLength
import io.kotlintest.should
import io.kotlintest.specs.WordSpec

class JUnitXmlTest : WordSpec() {

  override fun listeners() = listOf(JUnitXmlListener())

  init {

    "this is a test scope" should {
      "this is a test case" {
        "sammy" should haveLength(5)
      }
    }

    "this test" should {
      "is another test" {
        "bobby" should haveLength(5)
      }
    }
  }
}