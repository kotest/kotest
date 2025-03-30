package com.sksamuel.kotest.engine.interceptors

import com.sksamuel.kotest.engine.active.BangDisableFunSpec
import com.sksamuel.kotest.engine.active.FocusTest
import com.sksamuel.kotest.engine.active.EnabledTestConfigFlagTest
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.project.TestSuite
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.matchers.shouldBe

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class SpecSortEngineInterceptorTest : FunSpec({

   test("should sort classes") {

      val p = object : AbstractProjectConfig() {
         override val specExecutionOrder = SpecExecutionOrder.Lexicographic
      }

      var sorted = emptyList<SpecRef>()
      SpecSortEngineInterceptor.intercept(
         EngineContext.empty.withProjectConfig(p).withTestSuite(
            TestSuite(
               listOf(
                  SpecRef.Reference(EnabledTestConfigFlagTest::class),
                  SpecRef.Reference(BangDisableFunSpec::class),
                  SpecRef.Reference(FocusTest::class),
               )
            )
         )
      ) {
         sorted = it.suite.specs
         EngineResult(emptyList())
      }
      sorted.map { it.kclass } shouldBe listOf(
         BangDisableFunSpec::class,
         EnabledTestConfigFlagTest::class,
         FocusTest::class,
      )
   }
})
