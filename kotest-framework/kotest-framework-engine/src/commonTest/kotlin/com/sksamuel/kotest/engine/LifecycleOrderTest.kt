package com.sksamuel.kotest.engine

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
            .withClasses(LifecycleTests::class)
            .withExtensions(LifecycleExtension("engine"))
            .launch()
         collector.names shouldBe listOf("foo", "bar")
         LifecycleExtension.state shouldBe listOf(
            Triple("engine", Type.PROJECT, Phase.ENTRY),
            Triple("engine", Type.SPEC, Phase.ENTRY),
            Triple("engine", Type.TEST_CASE, Phase.ENTRY), // this is foo
            Triple("engine", Type.TEST_CASE, Phase.EXIT), // this is foo
            Triple("test-config", Type.TEST_CASE, Phase.ENTRY), // this is bar, extension from config
            Triple("engine", Type.TEST_CASE, Phase.ENTRY),  // this is bar
            Triple("engine", Type.TEST_CASE, Phase.EXIT), // this is bar
            Triple("test-config", Type.TEST_CASE, Phase.EXIT), // this is bar, extension from config
            Triple("engine", Type.SPEC, Phase.EXIT),
            Triple("engine", Type.PROJECT, Phase.EXIT),
         )
      }
   }
}

private class LifecycleTests : FunSpec() {
   init {
      test("foo") {}
      test("bar").config(extensions = listOf(LifecycleExtension("test-config"))) {}
   }
}

private enum class Type {
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
      val state = mutableListOf<Triple<String, Type, Phase>>()
   }

   override suspend fun interceptProject(context: ProjectContext, callback: suspend (ProjectContext) -> Unit) {
      state.add(Triple(name, Type.PROJECT, Phase.ENTRY))
      callback(context)
      state.add(Triple(name, Type.PROJECT, Phase.EXIT))
   }

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      state.add(Triple(name, Type.SPEC, Phase.ENTRY))
      execute(spec)
      state.add(Triple(name, Type.SPEC, Phase.EXIT))
   }

   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      state.add(Triple(name, Type.TEST_CASE, Phase.ENTRY))
      val result = execute(testCase)
      state.add(Triple(name, Type.TEST_CASE, Phase.EXIT))
      return result
   }
}
