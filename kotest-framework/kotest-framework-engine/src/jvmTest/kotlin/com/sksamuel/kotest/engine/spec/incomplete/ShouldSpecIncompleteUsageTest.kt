package com.sksamuel.kotest.engine.spec.incomplete

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.Spec
import io.kotest.engine.spec.SpecExecutor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.scopes.DslState
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class ShouldSpecIncompleteUsageTest : FunSpec({

   test("should block without .config should error at runtime") {

      var result: Throwable? = null

      val listener = object : TestEngineListener {
         override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
            result = t
         }
      }

      val executor = SpecExecutor(listener)
      executor.execute(IncompleteShouldSpec::class)
      result!!.message shouldBe "Test 'incomplete should usage' is incomplete"
      DslState.state = null
   }

})

@Ignored
class IncompleteShouldSpec : ShouldSpec({
   context("foo") {
      should("incomplete should usage")
   }
})
