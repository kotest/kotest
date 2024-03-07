package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.listeners.BeforeInvocationListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.test.TestDispatcher

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
class ProvideTestDispatcherInBeforeInvocation : FunSpec({
   coroutineTestScope = true
   var wasEverNotTestDispatcher = false

   extension(object : BeforeInvocationListener {
      override suspend fun beforeInvocation(testCase: TestCase, iteration: Int) {
         val dispatcher = currentCoroutineContext()[CoroutineDispatcher]
         wasEverNotTestDispatcher = wasEverNotTestDispatcher || dispatcher !is TestDispatcher

      }
   })

   context("beforeInvocation should contain test dispatcher when test scope is enabled") {
      test("nest me!") {
         wasEverNotTestDispatcher.shouldBe(false)
      }
   }
}
)
