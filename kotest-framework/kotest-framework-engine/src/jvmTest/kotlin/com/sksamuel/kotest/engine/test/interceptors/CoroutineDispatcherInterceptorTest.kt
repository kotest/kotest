package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.coroutines.CoroutineDispatcherFactory
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.interceptors.CoroutineDispatcherFactoryInterceptor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
@EnabledIf(LinuxCondition::class)
class CoroutineDispatcherInterceptorTest : DescribeSpec() {
   init {
      describe("CoroutineDispatcherInterceptor") {
         it("should dispatch to coroutineDispatcher") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               sourceRef(),
               TestType.Container,
            )

            val factory = object : CoroutineDispatcherFactory {
               override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
                 return f()
               }
               @OptIn(DelicateCoroutinesApi::class)
               override suspend fun <T> withDispatcher(spec: Spec, f: suspend () -> T): T {
                  return newSingleThreadContext("foo").use { dispatcher ->
                     withContext(dispatcher) {
                        f()
                     }
                  }
               }
            }

            val conf = ProjectConfiguration()
            conf.coroutineDispatcherFactory = factory
            CoroutineDispatcherFactoryInterceptor(conf).intercept(
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
