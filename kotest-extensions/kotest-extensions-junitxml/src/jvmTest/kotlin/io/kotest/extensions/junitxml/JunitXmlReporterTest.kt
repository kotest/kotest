package io.kotest.extensions.junitxml

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

class JunitXmlReporterTest : FreeSpec({

   suspend fun TestScope.writeSampleReport(outputDir: String) {
      val reporter = JunitXmlReporter(outputDir = outputDir)
      reporter.finalizeSpec(
         JunitXmlReporterTest::class, mapOf(
            TestCase(
               this.testCase.descriptor,
               name = TestNameBuilder.builder("dummy").build(),
               spec = this.testCase.spec,
               test = { TODO() },
               source = this.testCase.source,
               type = this.testCase.type,
               config = this.testCase.config
            ) to TestResult.Success(1.seconds)
         )
      )
   }

   "output dir test" - {
      val simple = "./"
      val relativeToNonExisting = "./notExistingIrrelevantDir/../"

      "simple: $simple" {
         testScope.writeSampleReport(simple)
      }

      // this is useful when the JunitXmlReporter.DefaultBuildDir build dir doesn't exist and
      // e.g. maven build dir `../target` should be configured.
      "relative to non existing directory: $relativeToNonExisting" {
         testScope.writeSampleReport(relativeToNonExisting)
      }
   }

   "should populate Error element for errors" {
      val e = JunitXmlReporter().createTestCaseElement(
         "mytest",
         TestResult.Error(10.seconds, IOException("boom")),
         JunitXmlReporterTest::class,
      )
      val error = e.getChild(JunitXmlReporter.ELEMENT_ERROR)
      error.getAttribute(JunitXmlReporter.ATTRIBUTE_TYPE).value shouldBe "java.io.IOException"
      error.getAttribute(JunitXmlReporter.ATTRIBUTE_MESSAGE).value shouldBe "boom"
      error.text shouldStartWith "java.io.IOException: boom"
   }

   "should populate Failure element for errors" {
      val e = JunitXmlReporter().createTestCaseElement(
         "mytest",
         TestResult.Failure(10.seconds, AssertionError("wallop")),
         JunitXmlReporterTest::class,
      )
      val failure = e.getChild(JunitXmlReporter.ELEMENT_FAILURE)
      failure.getAttribute(JunitXmlReporter.ATTRIBUTE_TYPE).value shouldBe "java.lang.AssertionError"
      failure.getAttribute(JunitXmlReporter.ATTRIBUTE_MESSAGE).value shouldBe "wallop"
      failure.text shouldStartWith "java.lang.AssertionError: wallop"
   }

})

