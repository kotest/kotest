package io.kotest.provided

import com.sksamuel.kotest.engine.callback.order.afterEachEvents
import com.sksamuel.kotest.engine.callback.order.afterTestEvents
import com.sksamuel.kotest.engine.callback.order.beforeEachEvents
import com.sksamuel.kotest.engine.callback.order.beforeTestEvents
import io.kotest.core.config.AbstractProjectConfig
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
      }
   )
}
