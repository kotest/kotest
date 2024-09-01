package com.sksamuel.kotest.engine.coroutines

import com.sksamuel.kotest.engine.spec.coroutine.currentThreadWithoutCoroutine
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

class CallbackThreadSpecificationTest : FunSpec() {

   override suspend fun beforeTest(testCase: TestCase) {
      // strip off the coroutine suffix
      listenerThread = currentThreadWithoutCoroutine()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      // strip off the coroutine suffix
      listenerThread shouldBe currentThreadWithoutCoroutine()
   }

   private var listenerThread = ""

   init {
      test("run listeners on the same thread as the test when a single invocation") {
         Thread.currentThread().name.shouldStartWith(listenerThread)
      }
   }
}
