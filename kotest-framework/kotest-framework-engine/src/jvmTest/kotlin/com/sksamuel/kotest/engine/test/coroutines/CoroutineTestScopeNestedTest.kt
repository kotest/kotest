package com.sksamuel.kotest.engine.test.coroutines

import io.kotest.core.annotation.Description
import io.kotest.core.annotation.Issue
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

@Description("Tests that nested tests using coroutineTestScope run successfully")
@Issue("https://github.com/kotest/kotest/issues/5118")
class CoroutineTestScopeNestedTest : FunSpec({

   coroutineTestScope = true
   val extension = extension(CoroutineExtension())

   test("working test") {
      withContext(extension.dispatcher) {
      }
   }

   context("context") {
      test("failing test") {
         withContext(extension.dispatcher) {
            // in this nested test we have the runTest TestScope already
         }
      }
   }
})

internal class CoroutineExtension : BeforeEachListener, AfterEachListener {

   lateinit var dispatcher: TestDispatcher

   @OptIn(ExperimentalCoroutinesApi::class)
   override suspend fun beforeEach(testCase: TestCase) {
      val scheduler = coroutineContext[TestCoroutineScheduler]
      dispatcher = StandardTestDispatcher(scheduler)
      Dispatchers.setMain(dispatcher)
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      Dispatchers.resetMain()
   }
}
