package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.project.ProjectContext
import io.kotest.core.project.projectContext
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.ProjectContextInterceptor
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.coroutines.coroutineContext

@EnabledIf(LinuxCondition::class)
class ProjectContextInterceptorTest : FunSpec() {
   init {

      val c = ProjectContext(null)
      var fired = false
      val next = object : NextSpecInterceptor {
         override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
            fired = true
            coroutineContext.projectContext shouldBe c
            return Result.success(emptyMap())
         }
      }

      test("ProjectContextInterceptor should set project context on coroutine scope") {
         fired.shouldBeFalse()
         ProjectContextInterceptor(c).intercept(BazSpec(), next)
         fired.shouldBeTrue()
      }
   }
}

private class BazSpec : FunSpec()
