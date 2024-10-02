package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.ProjectTimeoutEngineInterceptor
import io.kotest.engine.interceptors.ProjectTimeoutException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxCondition::class)
class ProjectTimeoutEngineInterceptorTest : FunSpec({

   test("should return ProjectTimeoutException when project times out") {
      val c = ProjectConfiguration()
      c.projectTimeout = 1.milliseconds
      val result = ProjectTimeoutEngineInterceptor.intercept(EngineContext.empty.withConfiguration(c)) {
         delay(1000)
         EngineResult.empty
      }
      result.errors.size shouldBe 1
      result.errors.first().shouldBeInstanceOf<ProjectTimeoutException>()
   }

   test("should not return ProjectTimeoutException when project does not time out") {
      val c = ProjectConfiguration()
      c.projectTimeout = 100000.milliseconds
      val result = ProjectTimeoutEngineInterceptor.intercept(EngineContext.empty.withConfiguration(c)) {
         delay(1)
         EngineResult.empty
      }
      result.errors.size shouldBe 0
   }
})
