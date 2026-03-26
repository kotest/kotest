package com.sksamuel.kotest.engine.callback.order

import io.kotest.core.annotation.Description
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe

val beforeDslVsListenerEvents = mutableListOf<String>()
val afterDslVsListenerEvents = mutableListOf<String>()

/**
 * Registered via @ApplyExtension — added to the global [io.kotest.engine.extensions.ExtensionRegistry],
 * which is position 1 in GLOBAL_FIRST ordering (before project config at position 2, and before
 * spec-level DSL callbacks at position 6).
 */
class BeforeDslOrderAnnotationListener : TestListener {
   override suspend fun beforeEach(testCase: TestCase) {
      if (testCase.spec is BeforeDslVsListenerOrderTest)
         beforeDslVsListenerEvents.add("annotation-beforeEach")
   }

   override suspend fun beforeTest(testCase: TestCase) {
      if (testCase.spec is BeforeDslVsListenerOrderTest)
         beforeDslVsListenerEvents.add("annotation-beforeTest")
   }
}

class AfterDslOrderAnnotationListener : TestListener {
   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      if (testCase.spec is AfterDslVsListenerOrderTest)
         afterDslVsListenerEvents.add("annotation-afterEach")
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      if (testCase.spec is AfterDslVsListenerOrderTest)
         afterDslVsListenerEvents.add("annotation-afterTest")
   }
}

/**
 * Confirms that [beforeEach] and [beforeTest] DSL callbacks — which register spec-level
 * [io.kotest.core.listeners.BeforeEachListener] and [io.kotest.core.listeners.BeforeTestListener]
 * respectively — run *after* any listeners registered via [@ApplyExtension] or project config.
 *
 * Ordering is driven by GLOBAL_FIRST scoping in [io.kotest.engine.config.TestConfigResolver.extensions]:
 * registry (annotation) → project config → spec DSL. Within [io.kotest.engine.test.TestExtensions],
 * the 'be' group (BeforeEachListeners) runs before the 'bt' group (BeforeTestListeners).
 *
 * Per-test expected order:
 *   be group: annotation-beforeEach → project-beforeEach → spec-beforeEach
 *   bt group: annotation-beforeTest → project-beforeTest → spec-beforeTest
 */
@Description("Confirms beforeEach and beforeTest DSL callbacks run after annotation and project config listeners")
@ApplyExtension(BeforeDslOrderAnnotationListener::class)
class BeforeDslVsListenerOrderTest : FunSpec() {
   init {

      beforeEach {
         beforeDslVsListenerEvents.add("spec-beforeEach")
      }

      beforeTest {
         beforeDslVsListenerEvents.add("spec-beforeTest")
      }

      test("test1") {}
      test("test2") {}

      afterProject {
         val perTest = listOf(
            "annotation-beforeEach", "project-beforeEach", "spec-beforeEach",
            "annotation-beforeTest", "project-beforeTest", "spec-beforeTest",
         )
         beforeDslVsListenerEvents shouldBe perTest + perTest
      }
   }
}

/**
 * Confirms that [afterEach] and [afterTest] DSL callbacks — which register spec-level
 * [io.kotest.core.listeners.AfterEachListener] and [io.kotest.core.listeners.AfterTestListener]
 * respectively — run *before* any listeners registered via [@ApplyExtension] or project config.
 *
 * Ordering is driven by LOCAL_FIRST (reversed GLOBAL_FIRST) scoping in
 * [io.kotest.engine.config.TestConfigResolver.extensions]: spec DSL → project config → registry (annotation).
 * Within [io.kotest.engine.test.TestExtensions], the 'at' group (AfterTestListeners) runs before
 * the 'ae' group (AfterEachListeners).
 *
 * Per-test expected order:
 *   at group: spec-afterTest → project-afterTest → annotation-afterTest
 *   ae group: spec-afterEach → project-afterEach → annotation-afterEach
 */
@Description("Confirms afterEach and afterTest DSL callbacks run before annotation and project config listeners")
@ApplyExtension(AfterDslOrderAnnotationListener::class)
class AfterDslVsListenerOrderTest : FunSpec() {
   init {

      afterEach {
         afterDslVsListenerEvents.add("spec-afterEach")
      }

      afterTest {
         afterDslVsListenerEvents.add("spec-afterTest")
      }

      test("test1") {}
      test("test2") {}

      afterProject {
         val perTest = listOf(
            "spec-afterTest", "project-afterTest", "annotation-afterTest",
            "spec-afterEach", "project-afterEach", "annotation-afterEach",
         )
         afterDslVsListenerEvents shouldBe perTest + perTest
      }
   }
}
