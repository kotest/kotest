package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.createTestName
import io.kotest.engine.test.NoopTestContext
import io.kotest.engine.test.interceptors.IncompleteContainerCheckInterceptor
import io.kotest.engine.test.interceptors.IncompleteContainerTestException

class IncompleteContainerCheckInterceptorTest : FunSpec() {
   init {
      test("TestType.Type should pass") {
         val testCase = TestCase.test(
            IncompleteContainerCheckInterceptorTest::class.toDescription().appendTest("wibble"),
            this@IncompleteContainerCheckInterceptorTest,
            parent = null,
            test = {}
         )
         IncompleteContainerCheckInterceptor.intercept { _, _ -> TestResult.success(0) }
            .invoke(testCase, NoopTestContext(testCase, coroutineContext))
      }
      test("incomplete container should throw") {
         shouldThrow<IncompleteContainerTestException> {
            val testCase = TestCase.container(
               IncompleteContainerCheckInterceptorTest::class.toDescription().appendTest("wibble"),
               this@IncompleteContainerCheckInterceptorTest,
               parent = null,
               test = {}
            )
            IncompleteContainerCheckInterceptor.intercept { _, _ -> TestResult.success(0) }
               .invoke(testCase, NoopTestContext(testCase, coroutineContext))
         }
      }
      test("complete container should pass") {
         val testCase = TestCase.container(
            IncompleteContainerCheckInterceptorTest::class.toDescription().appendTest("wibble"),
            this@IncompleteContainerCheckInterceptorTest,
            parent = null,
            test = {}
         )
         IncompleteContainerCheckInterceptor.intercept { testCase, context ->
            context.registerTestCase(
               createNestedTest(
                  createTestName("foo"),
                  false,
                  TestCaseConfig(),
                  TestType.Test,
                  null,
                  null
               ) {}
            )
            TestResult.success(0)
         }.invoke(testCase, NoopTestContext(testCase, coroutineContext))
      }
   }
}
