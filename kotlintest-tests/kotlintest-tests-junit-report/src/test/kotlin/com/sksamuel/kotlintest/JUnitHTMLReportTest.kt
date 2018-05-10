package com.sksamuel.kotlintest

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.specs.WordSpec
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

class JUnitHTMLReportTest : WordSpec() {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val ReportPath = "kotlintest-tests/kotlintest-tests-core/build/reports/tests/test/index.html"

  init {
    // we test the output from the earlier test of tests in
    // kotlintest-tests/kotlintest-tests-core
    "JUnit HTML Output" should {

      val file = when {
        System.getenv("TRAVIS") == "true" -> {
          println("HTML: " +File(System.getenv("TRAVIS_BUILD_DIR") + "/kotlintest-tests/kotlintest-tests-core/build/reports/tests/test").listFiles().joinToString("\n"))
          File(System.getenv("TRAVIS_BUILD_DIR") + "/$ReportPath")
        }
        System.getenv("APPVEYOR") == "True" -> {
          println("HTML: " + File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/kotlintest-tests/kotlintest-tests-core/build/reports/tests/test").listFiles().joinToString("\n"))
          File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/$ReportPath")
        }
        else ->
          File(System.getProperty("user.home") + "/development/workspace/kotlintest/$ReportPath")
      }

      "include classnames" {
        val html = Files.readAllLines(file.toPath()).joinToString("\n")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.AutoCloseTest.html">com.sksamuel.kotlintest.AutoCloseTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.specs.FeatureSpecTest.html">com.sksamuel.kotlintest.specs.FeatureSpecTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.TagTest.html">com.sksamuel.kotlintest.TagTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.matchers.string.StringMatchersTest.html">com.sksamuel.kotlintest.matchers.string.StringMatchersTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.specs.BehaviorSpecLambdaTest.html">com.sksamuel.kotlintest.specs.BehaviorSpecLambdaTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.listeners.TestListenerTest.html">com.sksamuel.kotlintest.listeners.TestListenerTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.assertions.arrow.ValidatedMatchersTest.html">com.sksamuel.kotlintest.assertions.arrow.ValidatedMatchersTest</a>""")
      }
    }
  }
}