package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.projectConfigResolver
import io.kotest.engine.spec.interceptor.instance.ProjectConfigResolverSpecInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.currentCoroutineContext

@EnabledIf(LinuxOnlyGithubCondition::class)
class ConfigurationInContextInterceptorTest : FunSpec() {
   init {

      suspend fun testConfig() {
         currentCoroutineContext().projectConfigResolver.shouldNotBeNull()
      }

      test("config should be injected into the test context") {
         var fired = false
         ProjectConfigResolverSpecInterceptor(ProjectConfigResolver()).intercept(
            DummySpec(),
            SpecRef.Reference(DummySpec::class, DummySpec::class.java.name)
         ) {
            testConfig()
            fired = true
            Result.success(emptyMap())
         }
         fired.shouldBeTrue()
      }
   }


}

private class DummySpec : FunSpec()
