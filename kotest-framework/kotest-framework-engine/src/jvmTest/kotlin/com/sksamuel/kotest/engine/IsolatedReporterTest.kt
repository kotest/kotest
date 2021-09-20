package com.sksamuel.kotest.engine

import io.kotest.core.annotation.Ignored
import io.kotest.core.descriptors.append
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.reporter.IsolatedReporter
import io.kotest.engine.reporter.Reporter
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
         override fun engineStarted(classes: List<KClass<*>>) {
            append("enginestarted")
         }

         override fun engineFinished(t: List<Throwable>) {
            append("engineFinished")
         }

         override fun specStarted(kclass: KClass<*>) {
            append("specStarted:$kclass")
         }

         override fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {
            append("specFinished:$kclass")
         }

         override fun testStarted(testCase: TestCase) {
            append("testStarted:${testCase.name.testName}")
         }

         override fun testFinished(testCase: TestCase, result: TestResult) {
            append("testFinished:${testCase.name.testName}")
         }

      }
      val isolated = IsolatedReporter(reporter)
      isolated.specStarted(IsolatedReporterSpec1::class)
      isolated.testStarted(
         TestCase(
            IsolatedReporterSpec1::class.toDescriptor().append("foo"),
            TestName("foo"),
            IsolatedReporterSpec1(),
            {},
            sourceRef(),
            TestType.Test
         )
      )
      isolated.specStarted(IsolatedReporterSpec2::class)
      isolated.testStarted(
         TestCase(
            IsolatedReporterSpec1::class.toDescriptor().append("bar"),
            TestName("bar"),
            IsolatedReporterSpec2(),
            {},
            sourceRef(),
            TestType.Test
         )
      )
      isolated.specFinished(IsolatedReporterSpec1::class, null, emptyMap())
      isolated.specFinished(IsolatedReporterSpec2::class, null, emptyMap())

      log.trim() shouldBe """
            specStarted:class com.sksamuel.kotest.engine.IsolatedReporterSpec1
            testStarted:foo
            specFinished:class com.sksamuel.kotest.engine.IsolatedReporterSpec1
            specStarted:class com.sksamuel.kotest.engine.IsolatedReporterSpec2
            testStarted:bar
            specFinished:class com.sksamuel.kotest.engine.IsolatedReporterSpec2
            """.trimIndent()
   }
})

@Ignored
class IsolatedReporterSpec1 : FunSpec()

@Ignored
class IsolatedReporterSpec2 : FunSpec()
