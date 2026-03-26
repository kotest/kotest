package io.kotest.provided

import com.sksamuel.kotest.engine.callback.order.afterEachEvents
import com.sksamuel.kotest.engine.callback.order.afterEachVsListenerEvents
import com.sksamuel.kotest.engine.callback.order.afterTestEvents
import com.sksamuel.kotest.engine.callback.order.beforeEachEvents
import com.sksamuel.kotest.engine.callback.order.beforeEachVsListenerEvents
import com.sksamuel.kotest.engine.callback.order.beforeTestEvents
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult

class ProjectConfig : AbstractProjectConfig() {
   override val extensions = listOf(
      object : TestListener {

         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            if (testCase.spec::class.simpleName == "TestListenerPrecedenceTest")
               afterEachEvents.add("projectAfterEach")
         }

         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (testCase.spec::class.simpleName == "TestListenerPrecedenceTest")
               afterTestEvents.add("projectAfterTest")
         }

         override suspend fun beforeTest(testCase: TestCase) {
            if (testCase.spec::class.simpleName == "TestListenerPrecedenceTest")
               beforeTestEvents.add("projectBeforeTest")
         }

         override suspend fun beforeEach(testCase: TestCase) {
            if (testCase.spec::class.simpleName == "TestListenerPrecedenceTest")
               beforeEachEvents.add("projectBeforeEach")
         }
      },
      // Pure BeforeTestListener for BeforeEachVsBeforeTestListenerOrderTest.
      // Must NOT implement BeforeEachListener so it only appears in the 'bt' group,
      // confirming that beforeEach (group 'be') runs before it.
      object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (testCase.spec::class.simpleName == "BeforeEachVsBeforeTestListenerOrderTest")
               beforeEachVsListenerEvents.add("project-listener")
         }
      },
      // Pure AfterTestListener for AfterEachVsAfterTestListenerOrderTest.
      // Must NOT implement AfterEachListener so it only appears in the 'at' group,
      // confirming that afterEach (group 'ae') runs after it.
      object : AfterTestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (testCase.spec::class.simpleName == "AfterEachVsAfterTestListenerOrderTest")
               afterEachVsListenerEvents.add("project-listener")
         }
      },
   )
}
