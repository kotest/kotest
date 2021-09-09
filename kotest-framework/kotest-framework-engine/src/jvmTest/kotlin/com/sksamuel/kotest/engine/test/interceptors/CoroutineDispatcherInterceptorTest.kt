package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.NoopTestContext
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class CoroutineDispatcherInterceptorTest : DescribeSpec() {
   init {
      describe("CoroutineDispatcherInterceptor") {
         it("should dispatch to coroutineDispatcher") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescription().appendTest("foo"),
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

            CoroutineDispatcherFactoryInterceptor(controller).intercept { _, _ ->
               Thread.currentThread().name.shouldStartWith("foo")
               TestResult.success(0)
            }.invoke(tc, NoopTestContext(tc, coroutineContext))
         }
      }
   }
}
