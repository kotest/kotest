package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.callbacks.SpecFinishedInterceptor
import io.kotest.engine.spec.interceptor.ref.callbacks.SpecStartedInterceptor
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@EnabledIf(LinuxOnlyGithubCondition::class)
class SpecStartedFinishedInterceptorTest : FunSpec() {
   init {

      test("SpecStartedInterceptor should call specStarted before invoking spec") {
         var result = ""
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specStarted(ref: SpecRef) {
               result += "a"
            }
         }
         SpecStartedInterceptor(listener)
            .intercept(SpecRef.Reference(FooSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  result += "b"
                  return Result.success(emptyMap())
               }
            })
         result shouldBe "ab"
      }

      test("SpecFinishedInterceptor should call specFinished after invoking spec") {
         var r = ""
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specFinished(ref: SpecRef, result: TestResult) {
               r += "a"
            }
         }
         SpecFinishedInterceptor(listener)
            .intercept(SpecRef.Reference(FooSpec::class), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  r += "b"
                  return Result.success(emptyMap())
               }
            })
         r shouldBe "ba"
      }
   }
}

private class FooSpec : FunSpec()
