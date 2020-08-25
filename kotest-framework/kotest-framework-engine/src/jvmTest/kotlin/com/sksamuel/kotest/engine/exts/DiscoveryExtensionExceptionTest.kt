package com.sksamuel.kotest.engine.exts

import io.kotest.core.config.configuration
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.launcher.execute
import io.kotest.engine.reporter.Reporter
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

/**
 * Asserts that an exception through in a [DiscoveryExtension] is correctly handled.
 */
class DiscoveryExtensionExceptionTest : FunSpec() {
   init {
      test("an exception in a discovery extension should be handled") {

         val reporter = object : Reporter {

            var errors = emptyList<Throwable>()

            override fun hasErrors(): Boolean = errors.isNotEmpty()
            override fun engineStarted(classes: List<KClass<out Spec>>) {}
            override fun engineFinished(t: List<Throwable>) {
               errors = t
            }

            override fun specStarted(kclass: KClass<out Spec>) {}
            override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {}
            override fun testStarted(testCase: TestCase) {}
            override fun testFinished(testCase: TestCase, result: TestResult) {}
         }

         configuration.registerExtension(ext)
         execute(reporter, "com.sksamuel.kotest.engine.active", null, null, null)
         reporter.hasErrors() shouldBe true
         configuration.deregisterExtension(ext)
      }
   }
}

private class EmptySpec : FunSpec()

private val ext = object : DiscoveryExtension {
   override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
      error("discovery goes boom")
   }
}
