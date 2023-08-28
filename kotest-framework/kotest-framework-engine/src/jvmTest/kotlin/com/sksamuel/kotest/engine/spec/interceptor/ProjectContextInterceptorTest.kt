package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.project.ProjectContext
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.project.projectContext
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.interceptor.instance.ProjectContextInterceptor
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.coroutines.coroutineContext

class ProjectContextInterceptorTest : FunSpec() {
   init {

      val c = ProjectContext(ProjectConfiguration())
      var fired = false
      val fn: suspend (Spec) -> Result<Map<TestCase, TestResult>> = {
         fired = true
         coroutineContext.projectContext shouldBe c
         Result.success(emptyMap())
      }

      test("ProjectContextInterceptor should set project context on coroutine scope") {
         fired.shouldBeFalse()
         ProjectContextInterceptor(c).intercept(BazSpec(), fn)
         fired.shouldBeTrue()
      }
   }
}

private class BazSpec : FunSpec()
