package com.sksamuel.kotest

import io.kotest.core.Tag
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.string.shouldContain
import java.io.File
import java.nio.file.Files

@Ignored
class JUnitHTMLReportTest : WordSpec() {

  override fun tags(): Set<Tag> = setOf(GithubActionsTag)

  fun indexHtml(): String {
    val ReportPath = "kotest-tests/kotest-tests-core/build/reports/tests/test/index.html"
    val file = when {
      isGitHubActions() -> {
        println("HTML: " + File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/kotest-tests/kotest-tests-core/build/reports/tests/test").listFiles().joinToString("\n"))
        File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/$ReportPath")
      }
      else -> throw RuntimeException()
    }
    return Files.readAllLines(file.toPath()).joinToString("\n")
  }

  init {
    // we test the output from the earlier test of tests in
    // kotest-tests/kotest-tests-core
    "JUnit HTML Output" should {
      "include classnames" {
        val html = indexHtml()
        html.shouldContain("""<a href="classes/com.sksamuel.kotest.AutoCloseTest.html">com.sksamuel.kotest.AutoCloseTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotest.specs.feature.FeatureSpecTest.html">com.sksamuel.kotest.specs.feature.FeatureSpecTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotest.TagTest.html">com.sksamuel.kotest.TagTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotest.matchers.string.StringMatchersTest.html">com.sksamuel.kotest.matchers.string.StringMatchersTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotest.specs.behavior.BehaviorSpecLambdaTest.html">com.sksamuel.kotest.specs.behavior.BehaviorSpecLambdaTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotest.listeners.TestListenerTest.html">com.sksamuel.kotest.listeners.TestListenerTest</a>""")
        html.shouldContain("""<a href="classes/com.sksamuel.kotest.assertions.arrow.ValidatedMatchersTest.html">com.sksamuel.kotest.assertions.arrow.ValidatedMatchersTest</a>""")
      }
    }
  }
}
