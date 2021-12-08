package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineDispatcher

@ExperimentalStdlibApi
class InheritedTestCoroutineDispatcherTest : FunSpec() {
   init {
      testCoroutineDispatcher = true
      context("a context with a test dispatcher should be inherited by nested tests") {
         val dispatcher = coroutineContext[CoroutineDispatcher]
         test("nest me!") {
            coroutineContext[CoroutineDispatcher] shouldBe dispatcher
         }
      }
   }
}
