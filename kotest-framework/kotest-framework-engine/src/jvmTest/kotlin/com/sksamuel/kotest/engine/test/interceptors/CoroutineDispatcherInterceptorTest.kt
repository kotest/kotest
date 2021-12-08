package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.interceptors.CoroutineDispatcherFactoryInterceptor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalStdlibApi
class CoroutineDispatcherInterceptorTest : DescribeSpec() {
   init {
      describe("CoroutineDispatcherInterceptor") {
         it("should dispatch to coroutineDispatcher") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestName("foo"),
               InvocationCountCheckInterceptorTest(),
               {},
               sourceRef(),
               TestType.Container,
            )

            val controller = object : CoroutineDispatcherFactory {
               override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
                  val executor = Executors.newSingleThreadExecutor {
                     val t = Thread(it)
                     t.name = "foo"
                     t
                  }
                  return withContext(executor.asCoroutineDispatcher()) {
                     f()
                  }
               }
            }

            CoroutineDispatcherFactoryInterceptor(controller).intercept(
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
