package com.sksamuel.kotest.engine.interceptors

import com.sksamuel.kotest.engine.active.BangDisableFunSpec
import com.sksamuel.kotest.engine.active.FocusTest
import com.sksamuel.kotest.engine.active.IgnoredTestsTest
import io.kotest.core.TestSuite
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.config.MutableConfiguration
import io.kotest.engine.config.toConfiguration
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.matchers.shouldBe

@Isolate
class SpecSortEngineInterceptorTest : FunSpec({

   test("should sort classes") {

      val c = MutableConfiguration()
      c.specExecutionOrder = SpecExecutionOrder.Lexicographic

      var sorted = emptyList<SpecRef>()
      SpecSortEngineInterceptor.intercept(
         EngineContext.empty.withProjectConfiguration(c.toConfiguration()).withTestSuite(
            TestSuite(
               listOf(
                  ReflectiveSpecRef(IgnoredTestsTest::class),
                  ReflectiveSpecRef(BangDisableFunSpec::class),
                  ReflectiveSpecRef(FocusTest::class),
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
