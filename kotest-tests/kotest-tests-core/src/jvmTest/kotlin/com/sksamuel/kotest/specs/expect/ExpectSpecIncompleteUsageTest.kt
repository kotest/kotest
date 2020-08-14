package com.sksamuel.kotest.specs.expect

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.Spec
import io.kotest.engine.spec.SpecExecutor
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.DslState
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@OptIn(kotlin.time.ExperimentalTime::class)
class FeatureSpecIncompleteUsageTest : FunSpec({

   test("expect block without .config should error at runtime") {

      var result: Throwable? = null

      val listener = object : TestEngineListener {
         override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
            result = t
         }
      }

      val executor = SpecExecutor(listener)
      executor.execute(IncompleteExpectSpec::class)
      result!!.message shouldBe "Test 'Expect: incomplete expect usage' is incomplete"
      DslState.state = null
   }

})

@Ignored
class IncompleteExpectSpec : ExpectSpec({
   context("foo") {
      expect("incomplete expect usage")
   }
})
