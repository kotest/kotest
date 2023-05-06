package io.kotest.extensions.junitxml

import io.kotest.core.names.TestName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope

import kotlin.time.Duration.Companion.seconds

class JunitXmlReporterTest : FreeSpec({

   "output dir test" - {
      val simple = "./"
      val relativeToNonExisting = "./notExistingIrrelevantDir/../"

      "simple: $simple" {
         this.writeSampleReport(simple)
      }

      // this is useful when the JunitXmlReporter.DefaultBuildDir build dir doesn't exist and
      // e.g. maven build dir `../target` should be configured.
      "relative to non existing directory: $relativeToNonExisting" {
         this.writeSampleReport(relativeToNonExisting)
      }
   }
}) {
   companion object {
      suspend fun TestScope.writeSampleReport(outputDir: String) {
         val reporter = JunitXmlReporter(outputDir = outputDir)
         reporter.finalizeSpec(JunitXmlReporterTest::class, mapOf(
            TestCase(
               this.testCase.descriptor,
               name = TestName("dummy"),
               spec = this.testCase.spec,
               test = { TODO() },
               source = this.testCase.source,
               type = this.testCase.type,
               config = this.testCase.config
            ) to TestResult.Success(1.seconds)
         ))
      }
   }

}
