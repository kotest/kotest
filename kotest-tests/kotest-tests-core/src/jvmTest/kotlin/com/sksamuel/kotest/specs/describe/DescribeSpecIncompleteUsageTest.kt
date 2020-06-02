package com.sksamuel.kotest.specs.describe

import io.kotest.core.annotation.Ignored
import io.kotest.core.engine.SpecExecutor
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.DslState
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@OptIn(kotlin.time.ExperimentalTime::class)
class DescribeSpecIncompleteUsageTest : FunSpec({

   test("it block without .config should error at runtime") {

      var result: Throwable? = null

      val listener = object : TestEngineListener {
         override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
            result = t
         }
      }

      val executor = SpecExecutor(listener)
      executor.execute(DescribeDslTestSpec::class)
      result!!.message shouldBe "Test 'incomplete it usage' is incomplete"
      DslState.state = null
   }

})

@Ignored
class DescribeDslTestSpec : DescribeSpec({
   describe("foo") {
      it("incomplete it usage")
   }
})
