package com.sksamuel.kotest.engine.interceptors

import com.sksamuel.kotest.engine.active.BangDisableFunSpec
import com.sksamuel.kotest.engine.active.FocusTest
import com.sksamuel.kotest.engine.active.IgnoredTestsTest
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.matchers.shouldBe

@Isolate
class SpecSortEngineInterceptorTest : FunSpec({

   test("should sort classes") {

      val c = Configuration()
      c.specExecutionOrder = SpecExecutionOrder.Lexicographic

      var sorted = emptyList<SpecRef>()
      SpecSortEngineInterceptor.intercept(
         EngineContext.empty.withConfiguration(c).withTestSuite(
            TestSuite(
               listOf(
                  IgnoredTestsTest::class,
                  BangDisableFunSpec::class,
                  FocusTest::class
               )
            )
         )
      ) {
         sorted = it.suite.specs
         EngineResult(emptyList())
      }
      sorted.map { it.kclass } shouldBe listOf(BangDisableFunSpec::class, FocusTest::class, IgnoredTestsTest::class)
   }
})
