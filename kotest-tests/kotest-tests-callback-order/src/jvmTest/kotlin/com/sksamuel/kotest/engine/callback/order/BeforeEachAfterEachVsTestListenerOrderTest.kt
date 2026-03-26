package com.sksamuel.kotest.engine.callback.order

import io.kotest.core.annotation.Description
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe

val beforeEachVsListenerEvents = mutableListOf<String>()
val afterEachVsListenerEvents = mutableListOf<String>()

class BeforeEachAnnotationBeforeTestListener : BeforeTestListener {
   override suspend fun beforeTest(testCase: TestCase) {
      beforeEachVsListenerEvents.add("annotation-listener")
   }
}

class AfterEachAnnotationAfterTestListener : AfterTestListener {
   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      afterEachVsListenerEvents.add("annotation-listener")
   }
}

/**
 * Confirms that [beforeEach] DSL callbacks (which register a [io.kotest.core.listeners.BeforeEachListener])
 * run *before* any [BeforeTestListener]s registered via [@ApplyExtension] or project config.
 *
 * Execution order is driven by [io.kotest.engine.test.TestExtensions.beforeTestBeforeEachBeforeContainer]:
 * all BeforeEachListeners (group 'be') are invoked first, then all BeforeTestListeners (group 'bt').
 * Within 'bt', GLOBAL_FIRST ordering applies: registry extensions (@ApplyExtension) precede project config.
 */
@Description("Confirms beforeEach runs before BeforeTestListeners from @ApplyExtension and project config")
@ApplyExtension(BeforeEachAnnotationBeforeTestListener::class)
class BeforeEachVsBeforeTestListenerOrderTest : FunSpec() {
   init {

      beforeEach {
         beforeEachVsListenerEvents.add("beforeEach")
      }

      test("test1") {}
      test("test2") {}

      afterProject {
         beforeEachVsListenerEvents shouldBe listOf(
            "beforeEach", "annotation-listener", "project-listener",
            "beforeEach", "annotation-listener", "project-listener",
         )
      }
   }
}

/**
 * Confirms that [afterEach] DSL callbacks (which register a [io.kotest.core.listeners.AfterEachListener])
 * run *after* any [AfterTestListener]s registered via [@ApplyExtension] or project config.
 *
 * Execution order is driven by [io.kotest.engine.test.TestExtensions.afterTestAfterEachAfterContainer]:
 * all AfterTestListeners (group 'at') are invoked first, then all AfterEachListeners (group 'ae').
 * Within 'at', LOCAL_FIRST ordering applies: project config precedes registry extensions (@ApplyExtension).
 */
@Description("Confirms afterEach runs after AfterTestListeners from @ApplyExtension and project config")
@ApplyExtension(AfterEachAnnotationAfterTestListener::class)
class AfterEachVsAfterTestListenerOrderTest : FunSpec() {
   init {

      afterEach {
         afterEachVsListenerEvents.add("afterEach")
      }

      test("test1") {}
      test("test2") {}

      afterProject {
         afterEachVsListenerEvents shouldBe listOf(
            "project-listener", "annotation-listener", "afterEach",
            "project-listener", "annotation-listener", "afterEach",
         )
      }
   }
}
