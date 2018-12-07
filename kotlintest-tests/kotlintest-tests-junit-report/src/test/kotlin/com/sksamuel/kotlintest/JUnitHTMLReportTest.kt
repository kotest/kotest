package com.sksamuel.kotlintest

import io.kotlintest.Tag
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.specs.WordSpec
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

class JUnitHTMLReportTest : WordSpec() {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  override fun tags(): Set<Tag> = setOf(AppveyorTag, TravisTag)

  fun indexHtml(): String {
    val ReportPath = "kotlintest-tests/kotlintest-tests-core/build/reports/tests/test/index.html"
    val file = when {
      isTravis() -> {
        println("HTML: " + File(System.getenv("TRAVIS_BUILD_DIR") + "/kotlintest-tests/kotlintest-tests-core/build/reports/tests/test").listFiles().joinToString("\n"))
        File(System.getenv("TRAVIS_BUILD_DIR") + "/$ReportPath")
      }
      isAppveyor() -> {
        println("HTML: " + File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/kotlintest-tests/kotlintest-tests-core/build/reports/tests/test").listFiles().joinToString("\n"))
        File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/$ReportPath")
      }
      else -> throw RuntimeException()
    }
    return Files.readAllLines(file.toPath()).joinToString("\n")
  }

  init {
    // we test the output from the earlier test of tests in
    // kotlintest-tests/kotlintest-tests-core
    "JUnit HTML Output" should {
      "include classnames" {
        val html = indexHtml()
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.AutoCloseTest.html">com.sksamuel.kotlintest.AutoCloseTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.specs.feature.FeatureSpecTest.html">com.sksamuel.kotlintest.specs.feature.FeatureSpecTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.TagTest.html">com.sksamuel.kotlintest.TagTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.specs.behavior.BehaviorSpecLambdaTest.html">com.sksamuel.kotlintest.specs.behavior.BehaviorSpecLambdaTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.listeners.TestListenerTest.html">com.sksamuel.kotlintest.listeners.TestListenerTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotlintest.assertions.arrow.ValidatedMatchersTest.html">com.sksamuel.kotlintest.assertions.arrow.ValidatedMatchersTest</a>""")
      }
    }
  }
}