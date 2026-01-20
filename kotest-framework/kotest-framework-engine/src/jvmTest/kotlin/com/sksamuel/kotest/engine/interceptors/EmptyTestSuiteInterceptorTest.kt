package com.sksamuel.kotest.engine.interceptors

import io.kotest.common.KotestTesting
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.EmptyTestSuiteChecker
import io.kotest.engine.EmptyTestSuiteException
import io.kotest.engine.EngineResult
import io.kotest.engine.TestEngineContext
import io.kotest.matchers.collections.shouldHaveSize

@OptIn(KotestTesting::class)
@EnabledIf(LinuxOnlyGithubCondition::class)
class EmptyTestSuiteInterceptorTest : FunSpec() {
   init {

      test("should error on empty test suite") {
         val c = object : AbstractProjectConfig() {
            override val failOnEmptyTestSuite = true
         }
         val result = EmptyTestSuiteChecker.checkForEmptyTestSuite(TestEngineContext(c), EngineResult.empty)
         result.errors.filterIsInstance<EmptyTestSuiteException>().shouldHaveSize(1)
      }

      test("should not error on non empty test suite") {

         val tc = TestCase(
            EmptyTestSuiteInterceptorTest::class.toDescriptor().append("foo"),
            TestNameBuilder.builder("foo").build(),
            EmptyTestSuiteInterceptorTest(),
            {},
            SourceRef.None,
            TestType.Test
         )

         val c = object : AbstractProjectConfig() {
            override val failOnEmptyTestSuite = true
         }
         val result = EmptyTestSuiteChecker.checkForEmptyTestSuite(TestEngineContext(c), EngineResult.empty)
         result.errors.filterIsInstance<EmptyTestSuiteException>().shouldHaveSize(0)
      }
   }
}
