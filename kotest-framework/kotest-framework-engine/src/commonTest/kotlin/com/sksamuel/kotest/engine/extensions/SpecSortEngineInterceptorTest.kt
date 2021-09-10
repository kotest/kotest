package com.sksamuel.kotest.engine.extensions

import com.sksamuel.kotest.engine.active.BangDisableFunSpec
import com.sksamuel.kotest.engine.active.FocusTest
import com.sksamuel.kotest.engine.active.IgnoredTestsTest
import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe

@Isolate
class SpecSortEngineInterceptorTest : FunSpec({

   test("should sort classes") {
      val previous = configuration.specExecutionOrder
      configuration.specExecutionOrder = SpecExecutionOrder.Lexicographic
      var sorted = emptyList<SpecRef>()
      SpecSortEngineInterceptor.intercept(
         TestSuite(listOf(IgnoredTestsTest::class, BangDisableFunSpec::class, FocusTest::class)),
         NoopTestEngineListener
      ) { suite, _ ->
         sorted = suite.specs
         EngineResult(emptyList())
      }
      sorted shouldBe listOf(BangDisableFunSpec::class, FocusTest::class, IgnoredTestsTest::class)
      configuration.specExecutionOrder = previous
   }

})
