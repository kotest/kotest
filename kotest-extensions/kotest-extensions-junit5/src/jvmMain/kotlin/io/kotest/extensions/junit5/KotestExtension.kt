package io.kotest.extensions.junit5

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback
import org.junit.jupiter.api.extension.TestWatcher
import java.util.Optional

/**
 * Adapts JUnit Platform Extensions into Kotest Listeners/Extensions.
 *
 * Supported JUnit Extensions:
 * - [BeforeEachCallback]
 * - [BeforeAllCallback]
 * - [AfterEachCallback]
 * - [AfterAllCallback]
 * - [BeforeTestExecutionCallback]
 * - [AfterTestExecutionCallback]
 * - [TestInstancePostProcessor]
 * - [TestWatcher]
 * - [TestInstancePreDestroyCallback]
 */
class JUnitExtensionAdapter(private val extension: Extension) : TestListener {

   override suspend fun beforeAny(testCase: TestCase) {
      val context = KotestExtensionContext(testCase.spec, testCase)
      if (extension is BeforeTestExecutionCallback) {
         extension.beforeTestExecution(context)
      }
      if (extension is BeforeEachCallback) {
         extension.beforeEach(context)
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      val context = KotestExtensionContext(testCase.spec, testCase)
      if (extension is AfterEachCallback) {
         extension.afterEach(context)
      }
      if (extension is AfterTestExecutionCallback) {
         extension.afterTestExecution(context)
      }
      if (extension is TestWatcher) {
         when (result) {
            is TestResult.Ignored -> extension.testDisabled(context, Optional.ofNullable(result.reason))
            is TestResult.Success -> extension.testSuccessful(context)
            is TestResult.Error -> extension.testAborted(context, result.errorOrNull ?: error("must not be null"))
            is TestResult.Failure -> extension.testFailed(context, result.errorOrNull ?: error("must not be null"))
         }
      }
   }

   override suspend fun beforeSpec(spec: Spec) {
      if (extension is TestInstancePostProcessor) {
         extension.postProcessTestInstance(spec, KotestExtensionContext(spec, null))
      }
      if (extension is BeforeAllCallback) {
         extension.beforeAll(KotestExtensionContext(spec, null))
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      if (extension is AfterAllCallback) {
         extension.afterAll(KotestExtensionContext(spec, null))
      }
      if (extension is TestInstancePreDestroyCallback) {
         extension.preDestroyTestInstance(KotestExtensionContext(spec, null))
      }
   }
}
