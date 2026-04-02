@file:Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")

package com.sksamuel.kotest.framework.engine

import io.kotest.core.annotation.Issue
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.project.ProjectContext
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe

/**
 * Verifies that project, spec, and test-case interceptors wrap each other in the correct
 * nested order on JS — matching JVM behaviour.
 *
 * The expected nesting is:
 *   PROJECT enter
 *     SPEC enter
 *       TEST_CASE enter  (foo)
 *       TEST_CASE exit   (foo)
 *       TEST_CASE enter  (bar)
 *       TEST_CASE exit   (bar)
 *     SPEC exit
 *   PROJECT exit
 */
@Issue("https://github.com/kotest/kotest/issues/3340")
class LifecycleOrderTest : FunSpec() {
   init {
      test("project, spec and test interceptors wrap each other in the correct order") {
         lifecycleEvents.clear()
         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
            .withSpecRefs(SpecRef.Function({ LifecycleOrderSpec() }, LifecycleOrderSpec::class))
            .addExtension(JsProjectExtension)
            .execute()
         collector.names shouldBe listOf("foo", "bar")
         lifecycleEvents shouldBe listOf(
            "PROJECT_ENTRY",
            "SPEC_ENTRY",
            "TEST_ENTRY",  // foo
            "TEST_EXIT",   // foo
            "TEST_ENTRY",  // bar
            "TEST_EXIT",   // bar
            "SPEC_EXIT",
            "PROJECT_EXIT",
         )
      }
   }
}

private val lifecycleEvents = mutableListOf<String>()

private object JsProjectExtension : ProjectExtension {
   override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
      lifecycleEvents.add("PROJECT_ENTRY")
      callback(context)
      lifecycleEvents.add("PROJECT_EXIT")
   }
}

private class LifecycleOrderSpec : FunSpec() {
   init {
      extension(object : SpecExtension {
         override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
            lifecycleEvents.add("SPEC_ENTRY")
            execute(spec)
            lifecycleEvents.add("SPEC_EXIT")
         }
      })
      extension(object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
            lifecycleEvents.add("TEST_ENTRY")
            val result = execute(testCase)
            lifecycleEvents.add("TEST_EXIT")
            return result
         }
      })
      test("foo") {}
      test("bar") {}
   }
}
