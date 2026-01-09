package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.project.ProjectContext
import io.kotest.core.project.projectContext
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.ProjectContextInterceptor
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.currentCoroutineContext

@EnabledIf(LinuxOnlyGithubCondition::class)
class ProjectContextInterceptorTest : FunSpec() {
   init {

      val c = ProjectContext(null)
      var fired = false
      val next = NextSpecInterceptor {
         fired = true
         currentCoroutineContext().projectContext shouldBe c
         Result.success(emptyMap())
      }

      test("ProjectContextInterceptor should set project context on coroutine scope") {
         fired.shouldBeFalse()
         ProjectContextInterceptor(c).intercept(
            BazSpec(),
            SpecRef.Reference(BazSpec::class, BazSpec::class.java.name),
            next
         )
         fired.shouldBeTrue()
      }
   }
}

private class BazSpec : FunSpec()
