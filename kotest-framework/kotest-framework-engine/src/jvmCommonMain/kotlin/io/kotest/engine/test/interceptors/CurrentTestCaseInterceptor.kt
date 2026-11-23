package io.kotest.engine.test.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.config.OutputCapture
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.TestCaseContextElement
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestThreadState
import io.kotest.engine.test.output.SpecThreadOwnerElement
import io.kotest.engine.test.output.StandardStreamCapture
import io.kotest.engine.test.output.TestOutputBuffer
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext

@JVMOnly
internal actual fun currentTestCaseInterceptor(
   listener: TestCaseExecutionListener,
   projectConfigResolver: ProjectConfigResolver,
   testConfigResolver: TestConfigResolver,
): TestExecutionInterceptor? = CurrentTestCaseInterceptor(listener, projectConfigResolver, testConfigResolver)

/**
 * Makes the test the current test of every thread its coroutines run on (see [io.kotest.engine.test.CurrentTestCase]),
 * and, when output capture is enabled for the test, collects what it writes to the standard streams and reports it
 * to the [listener] according to its [OutputCapture] mode, before the test is reported finished.
 */
@JVMOnly
internal class CurrentTestCaseInterceptor(
   private val listener: TestCaseExecutionListener,
   private val projectConfigResolver: ProjectConfigResolver,
   private val testConfigResolver: TestConfigResolver,
) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      val mode = testConfigResolver.outputCapture(testCase)
      val buffer = if (mode == OutputCapture.Never) null else {
         StandardStreamCapture.ensureInstalled()
         TestOutputBuffer(projectConfigResolver.outputCaptureMaxBytes())
      }
      val state = TestThreadState(testCase, buffer)
      // registered even without a buffer, so a test that doesn't capture is still the innermost running test,
      // and output from spec threads while it runs is written through rather than given to a capturing parent
      val owner = currentCoroutineContext()[SpecThreadOwnerElement]?.owner

      owner?.started(state)
      val result = try {
         withContext(TestCaseContextElement(state)) {
            test(testCase, scope)
         }
      } finally {
         owner?.finished(state)
      }

      if (buffer != null) {
         StandardStreamCapture.warnIfReplaced()
         val report = when (mode) {
            OutputCapture.Never -> false
            OutputCapture.OnFailure -> result.isErrorOrFailure
            OutputCapture.Always -> !result.isIgnored
         }
         if (report) {
            val output = buffer.toCapturedOutput()
            if (!output.isEmpty()) listener.testOutputCaptured(testCase, output)
         }
      }

      return result
   }
}
