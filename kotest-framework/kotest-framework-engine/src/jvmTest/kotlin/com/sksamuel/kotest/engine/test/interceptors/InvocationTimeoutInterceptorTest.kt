package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.interceptors.InvocationTimeoutInterceptor
import io.kotest.engine.test.scopes.NoopTestScope
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationTimeoutInterceptorTest : FunSpec() {
   init {
      test("InvocationTimeoutInterceptor should error after timeout") {

         val tc = TestCase(
            descriptor = InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
            name = TestNameBuilder.builder("foo").build(),
            spec = InvocationCountCheckInterceptorTest(),
            test = {},
            source = SourceRef.None,
            type = TestType.Test,
         )

         shouldThrowAny {
            InvocationTimeoutInterceptor(TestConfigResolver()).intercept(
               tc.copy(config = TestConfig(invocationTimeout = 1.milliseconds)),
               NoopTestScope(tc, coroutineContext)
            ) { _, _ ->
               delay(10000)
               TestResult.Success(0.milliseconds)
            }
         }
      }
   }
}
