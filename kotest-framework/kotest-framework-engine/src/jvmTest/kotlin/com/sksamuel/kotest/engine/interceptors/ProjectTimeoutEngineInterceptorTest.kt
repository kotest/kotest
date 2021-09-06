package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.interceptors.ProjectTimeoutEngineInterceptor
import io.kotest.engine.interceptors.ProjectTimeoutException
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.delay

class ProjectTimeoutEngineInterceptorTest : FunSpec({

   test("should return ProjectTimeoutException when project times out") {
      val result = ProjectTimeoutEngineInterceptor(1).intercept(
         TestSuite.empty,
         NoopTestEngineListener
      ) { _, _ ->
         delay(1000)
         EngineResult.empty
      }
      result.errors.size shouldBe 1
      result.errors.first().shouldBeInstanceOf<ProjectTimeoutException>()
   }

   test("should not return ProjectTimeoutException when project does not time out") {
      val result = ProjectTimeoutEngineInterceptor(1000).intercept(
         TestSuite.empty,
         NoopTestEngineListener
      ) { _, _ ->
         delay(1)
         EngineResult.empty
      }
      result.errors.size shouldBe 0
   }
})
