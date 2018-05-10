package com.sksamuel.kotlintest

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.specs.WordSpec
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

class JUnitHTMLReportTest : WordSpec() {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val ReportPath = "kotlintest-tests/kotlintest-tests-core/build/reports/tests/index.html"

  init {
    // we test the output from the earlier test of tests in
    // kotlintest-tests/kotlintest-tests-core
    "JUnit HTML Output" should {

      val file = when {
        System.getenv("TRAVIS") == "true" -> {
          val f = File(System.getenv("TRAVIS_BUILD_DIR") + "/$ReportPath")
          logger.info("build_folder=" + f.listFiles().joinToString(","))
          logger.info("parent=" + f.parentFile.listFiles().joinToString(","))
          f
        }
        System.getenv("APPVEYOR") == "True" -> {
          val f = File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/$ReportPath")
          logger.info("build_folder=" + f.listFiles().joinToString(","))
          logger.info("parent=" + f.parentFile.listFiles().joinToString(","))
          f
        }
        else ->
          File(System.getProperty("user.home") + "/development/workspace/kotlintest/$ReportPath")
      }

      "include classnames" {
        val html = Files.readAllLines(file.toPath()).joinToString("\n")
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