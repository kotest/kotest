package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.append
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.EmptyTestSuiteException
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.time.Duration

@EnabledIf(LinuxCondition::class)
class EmptyTestSuiteInterceptorTest : FunSpec() {
   init {

      test("should error on empty test suite") {
         val conf = ProjectConfiguration()
         conf.failOnEmptyTestSuite = true
         val result =
            EmptyTestSuiteInterceptor.intercept(EngineContext.empty.withConfiguration(conf)) { EngineResult.empty }
         result.errors.filterIsInstance<EmptyTestSuiteException>().shouldHaveSize(1)
      }

      test("should not error on non empty test suite") {

         val tc = TestCase(
            EmptyTestSuiteInterceptorTest::class.toDescriptor().append("foo"),
             TestNameBuilder.builder("foo").build(),
            EmptyTestSuiteInterceptorTest(),
            {},
            sourceRef(),
            TestType.Test
         )

         val conf = ProjectConfiguration()
         conf.failOnEmptyTestSuite = true
         val result = EmptyTestSuiteInterceptor.intercept(
            EngineContext.empty.withConfiguration(conf)
         ) {
            it.listener.testFinished(tc, TestResult.Success(Duration.ZERO))
            EngineResult.empty
         }
         result.errors.filterIsInstance<EmptyTestSuiteException>().shouldHaveSize(0)
      }
   }
}
