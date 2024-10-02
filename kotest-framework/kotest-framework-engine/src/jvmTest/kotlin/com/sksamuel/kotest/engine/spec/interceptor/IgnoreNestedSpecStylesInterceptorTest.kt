package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.EmptyExtensionRegistry
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.IgnoreNestedSpecStylesInterceptor

@EnabledIf(LinuxCondition::class)
class IgnoreNestedSpecStylesInterceptorTest : FunSpec({
   test("IgnoreNestedSpecStylesInterceptor should skip any nested spec style") {

      val errorNext = object : NextSpecInterceptor {
         override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
            error("boom")
         }
      }

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept(MyBehaviorSpec(), errorNext)

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept(MyWordSpec(), errorNext)

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept(MyFreeSpec(), errorNext)

      IgnoreNestedSpecStylesInterceptor(NoopTestEngineListener, EmptyExtensionRegistry)
         .intercept(MyDescribeSpec(), errorNext)
   }
})

private class MyBehaviorSpec : BehaviorSpec()
private class MyFreeSpec : FreeSpec()
private class MyWordSpec : WordSpec()
private class MyDescribeSpec : DescribeSpec()
