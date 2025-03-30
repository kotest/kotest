package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.coroutines.CoroutineDispatcherFactory
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.CoroutineDispatcherFactorySpecInterceptor
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
@EnabledIf(NotMacOnGithubCondition::class)
class CoroutineDispatcherFactorySpecInterceptorTest : DescribeSpec() {
   init {
      describe("CoroutineDispatcherFactorySpecInterceptor") {
         it("should dispatch to coroutineDispatcher for the per spec override") {

            val factory = object : CoroutineDispatcherFactory {
               override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
                  return f()
               }

               override suspend fun <T> withDispatcher(spec: Spec, f: suspend () -> T): T {
                  return newSingleThreadContext("foo").use { dispatcher ->
                     withContext(dispatcher) {
                        f()
                     }
                  }
               }
            }
            val c = object : AbstractProjectConfig() {
               override val coroutineDispatcherFactory = factory
            }
            CoroutineDispatcherFactorySpecInterceptor(SpecConfigResolver(c)).intercept(
               this@CoroutineDispatcherFactorySpecInterceptorTest,
               object : NextSpecInterceptor {
                  override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
                     Thread.currentThread().name.shouldStartWith("foo")
                     return Result.success(emptyMap())
                  }
               }
            )
         }
      }
   }
}
