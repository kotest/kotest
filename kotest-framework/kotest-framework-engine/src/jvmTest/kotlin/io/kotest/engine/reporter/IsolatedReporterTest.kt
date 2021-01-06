package io.kotest.engine.reporter

import io.kotest.core.annotation.Ignored
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class IsolatedReporterTest : FunSpec({

   test("isolated reporter happy path") {
      var log = ""
      val reporter = object : Reporter {

         private fun append(s: String) {
            log += "$s\n"
         }

         override fun hasErrors(): Boolean = false
         override fun engineStarted(classes: List<KClass<out Spec>>) {
            append("enginestarted")
         }

         override fun engineFinished(t: List<Throwable>) {
            append("engineFinished")
         }

         override fun specStarted(kclass: KClass<out Spec>) {
            append("specStarted:$kclass")
         }

         override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
            append("specFinished:$kclass")
         }

         override fun testStarted(testCase: TestCase) {
            append("testStarted:${testCase.displayName}")
         }

         override fun testFinished(testCase: TestCase, result: TestResult) {
            append("testFinished:${testCase.displayName}")
         }

      }
      val isolated = IsolatedReporter(reporter)
      isolated.specStarted(IsolatedReporterSpec1::class)
      isolated.testStarted(
         TestCase(
            IsolatedReporterSpec1::class.toDescription().appendTest("foo"),
            IsolatedReporterSpec1(),
            {},
            sourceRef(),
            TestType.Test
         )
      )
      isolated.specStarted(IsolatedReporterSpec2::class)
      isolated.testStarted(
         TestCase(
            IsolatedReporterSpec1::class.toDescription().appendTest("bar"),
            IsolatedReporterSpec2(),
            {},
            sourceRef(),
            TestType.Test
         )
      )
      isolated.specFinished(IsolatedReporterSpec1::class, null, emptyMap())
      isolated.specFinished(IsolatedReporterSpec2::class, null, emptyMap())

      log.trim() shouldBe """
            specStarted:class io.kotest.engine.reporter.IsolatedReporterSpec1
            testStarted:foo
            specFinished:class io.kotest.engine.reporter.IsolatedReporterSpec1
            specStarted:class io.kotest.engine.reporter.IsolatedReporterSpec2
            testStarted:bar
            specFinished:class io.kotest.engine.reporter.IsolatedReporterSpec2
            """.trimIndent()
   }
})

@Ignored
class IsolatedReporterSpec1 : FunSpec()

@Ignored
class IsolatedReporterSpec2 : FunSpec()
