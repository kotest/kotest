package com.sksamuel.kotest.assertions

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.AssertionMode
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class AssertionModeThreadTest : FunSpec() {

   override fun assertionMode() = AssertionMode.Error

   init {

      test("assertions from another thread should be counted") {
         withContext(Dispatchers.Default.limitedParallelism(1)) {
            launch {
               1 shouldBe 1
               1 shouldBe 1
               1 shouldBe 1
            }
         }
      }
   }
}
