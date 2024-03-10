package com.sksamuel.kotest.engine

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.project.ProjectContext
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class LifecycleOrderTest : FunSpec() {
   init {

      test("project, spec and test interceptors should wrap each other") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .withClasses(LifecycleTests::class)
            .withExtensions(LifecycleExtension("project"))
            .launch()
         collector.names shouldBe listOf("foo", "bar")
         LifecycleExtension.state shouldBe listOf(
            Triple("project", Lifecycle.PROJECT, Phase.ENTRY),
            Triple("project", Lifecycle.SPEC, Phase.ENTRY),
            Triple("project", Lifecycle.TEST_CASE, Phase.ENTRY),
            Triple("project", Lifecycle.TEST_CASE, Phase.EXIT),
            Triple("project", Lifecycle.TEST_CASE, Phase.ENTRY),
            Triple("bar", Lifecycle.TEST_CASE, Phase.ENTRY),
            Triple("bar", Lifecycle.TEST_CASE, Phase.EXIT),
            Triple("project", Lifecycle.TEST_CASE, Phase.EXIT),
            Triple("project", Lifecycle.SPEC, Phase.EXIT),
            Triple("project", Lifecycle.PROJECT, Phase.EXIT),
         )
      }
   }
}

private class LifecycleTests : FunSpec() {
   init {
      test("foo") {}
      test("bar").config(extensions = listOf(LifecycleExtension("bar"))) {}
   }
}

private enum class Lifecycle {
   PROJECT,
   SPEC,
   TEST_CASE
}

private enum class Phase {
   ENTRY,
   EXIT
}

private class LifecycleExtension(val name: String) : ProjectExtension, SpecExtension, TestCaseExtension {

   companion object {
      val state = mutableListOf<Triple<String, Lifecycle, Phase>>()
   }

   override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
      state.add(Triple(name, Lifecycle.PROJECT, Phase.ENTRY))
      callback(context)
      state.add(Triple(name, Lifecycle.PROJECT, Phase.EXIT))
   }

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      state.add(Triple(name, Lifecycle.SPEC, Phase.ENTRY))
      execute(spec)
      state.add(Triple(name, Lifecycle.SPEC, Phase.EXIT))
   }

   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      state.add(Triple(name, Lifecycle.TEST_CASE, Phase.ENTRY))
      val result = execute(testCase)
      state.add(Triple(name, Lifecycle.TEST_CASE, Phase.EXIT))
      return result
   }
}
