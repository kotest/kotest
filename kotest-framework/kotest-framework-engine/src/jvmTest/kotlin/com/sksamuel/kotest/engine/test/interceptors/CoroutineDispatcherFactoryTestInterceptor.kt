package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.coroutines.CoroutineDispatcherFactory
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.interceptors.CoroutineDispatcherFactoryTestInterceptor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
@EnabledIf(LinuxOnlyGithubCondition::class)
class CoroutineDispatcherFactoryTestInterceptor : DescribeSpec() {
   init {
      describe("CoroutineDispatcherFactoryTest") {
         it("should dispatch to coroutineDispatcher for the per test override") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Container,
            )

            val factory = object : CoroutineDispatcherFactory {
               override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
                  return newSingleThreadContext("foo").use { dispatcher ->
                     withContext(dispatcher) {
                        f()
                     }
                  }
               }

               @OptIn(DelicateCoroutinesApi::class)
               override suspend fun <T> withDispatcher(spec: Spec, f: suspend () -> T): T {
                  return f()
               }
            }

            val c = object : AbstractProjectConfig() {
               override val coroutineDispatcherFactory = factory
            }
            CoroutineDispatcherFactoryTestInterceptor(SpecConfigResolver(c)).intercept(
               tc,
               NoopTestScope(tc, coroutineContext)
            ) { _, _ ->
               Thread.currentThread().name.shouldStartWith("foo")
               TestResult.Success(0.milliseconds)
            }
         }
      }
   }
}
