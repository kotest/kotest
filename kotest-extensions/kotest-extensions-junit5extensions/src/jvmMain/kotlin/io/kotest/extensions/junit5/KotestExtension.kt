package io.kotest.extensions.junit5

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import org.junit.jupiter.api.extension.TestWatcher
import java.util.Optional

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
         when (result.status) {
            TestStatus.Ignored -> extension.testDisabled(
               context,
               Optional.ofNullable(result.reason)
            )
            TestStatus.Success -> extension.testSuccessful(
               context
            )
            TestStatus.Error -> extension.testAborted(
               context,
               result.error
            )
            TestStatus.Failure -> extension.testFailed(
               context,
               result.error
            )
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
   }
}
