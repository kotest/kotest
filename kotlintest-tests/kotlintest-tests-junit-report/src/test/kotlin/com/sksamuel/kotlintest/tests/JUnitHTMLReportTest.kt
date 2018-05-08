package com.sksamuel.kotlintest.tests

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.specs.WordSpec
import java.io.File
import java.nio.file.Files

class JUnitHTMLReportTest : WordSpec() {
  init {
    // we test the output from the earlier test of tests in
    // kotlintest-tests/kotlintest-tests-core
    "JUnit HTML Output" should {

      val file = if (System.getenv("TRAVIS") == "true") {
        File("/home/travis/build/kotlintest/kotlintest/kotlintest-tests/kotlintest-tests-core/build/reports/tests/index.html")
      } else {
        File("./kotlintest-tests/kotlintest-tests-core/build/reports/tests/index.html")
      }

      val html = Files.readAllLines(file.toPath()).joinToString("\n")

      "include classnames" {
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.tests.AutoCloseTest.html">com.sksamuel.kotlintest.tests.AutoCloseTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.tests.specs.FeatureSpecTest.html">com.sksamuel.kotlintest.tests.specs.FeatureSpecTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.tests.TagTest.html">com.sksamuel.kotlintest.tests.TagTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.tests.matchers.string.StringMatchersTest.html">com.sksamuel.kotlintest.tests.matchers.string.StringMatchersTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.tests.specs.BehaviorSpecLambdaTest.html">com.sksamuel.kotlintest.tests.specs.BehaviorSpecLambdaTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.tests.listeners.TestListenerTest.html">com.sksamuel.kotlintest.tests.listeners.TestListenerTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.tests.assertions.arrow.ValidatedMatchersTest.html">com.sksamuel.kotlintest.tests.assertions.arrow.ValidatedMatchersTest</a>""")
      }
    }
  }
}