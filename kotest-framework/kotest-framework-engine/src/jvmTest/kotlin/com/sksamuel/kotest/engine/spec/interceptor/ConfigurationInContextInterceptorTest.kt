package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.projectConfigResolver
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.ProjectConfigResolverSpecInterceptor
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlin.coroutines.coroutineContext

@EnabledIf(LinuxOnlyGithubCondition::class)
class ConfigurationInContextInterceptorTest : FunSpec() {
   init {

      suspend fun testConfig() {
         coroutineContext.projectConfigResolver.shouldNotBeNull()
      }

      test("config should be injected into the test context") {
         var fired = false
         ProjectConfigResolverSpecInterceptor(ProjectConfigResolver()).intercept(DummySpec(), object : NextSpecInterceptor {
            override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
               testConfig()
               fired = true
               return Result.success(emptyMap())
            }
         })
         fired.shouldBeTrue()
      }
   }


}

private class DummySpec : FunSpec()
