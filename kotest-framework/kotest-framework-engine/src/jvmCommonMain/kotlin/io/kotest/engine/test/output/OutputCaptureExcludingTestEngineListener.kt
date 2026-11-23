package io.kotest.engine.test.output

import io.kotest.common.JVMOnly
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.listener.TestEngineInitializedContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.CapturedOutput
import io.kotest.engine.test.TestCaseContextElement
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestThreadState
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

@JVMOnly
internal actual fun excludeFromOutputCapture(listener: TestEngineListener): TestEngineListener =
   OutputCaptureExcludingTestEngineListener(listener)

/**
 * Invokes the delegate with no current test. Test events are fired from inside the coroutine of the test,
 * or of its parent, and what a listener prints there must not be captured as that test's output.
 */
internal class OutputCaptureExcludingTestEngineListener(
   private val delegate: TestEngineListener,
) : TestEngineListener {

   private val noTest = TestCaseContextElement(TestThreadState.excluded)

   // nothing to exclude from until some test has installed capture, so skip the context switch until then
   private suspend inline fun excluded(crossinline f: suspend () -> Unit) {
      if (StandardStreamCapture.isInstalled) withContext(noTest) { f() } else f()
   }

   override suspend fun engineStarted() = excluded { delegate.engineStarted() }

   override suspend fun engineInitialized(context: TestEngineInitializedContext) =
      excluded { delegate.engineInitialized(context) }

   override suspend fun engineFinished(t: List<Throwable>) = excluded { delegate.engineFinished(t) }

   override suspend fun specStarted(ref: SpecRef) = excluded { delegate.specStarted(ref) }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) =
      excluded { delegate.specIgnored(kclass, reason) }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) =
      excluded { delegate.specFinished(ref, result) }

   override suspend fun testStarted(testCase: TestCase) = excluded { delegate.testStarted(testCase) }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) =
      excluded { delegate.testIgnored(testCase, reason) }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) =
      excluded { delegate.testFinished(testCase, result) }

   override suspend fun testOutputCaptured(testCase: TestCase, output: CapturedOutput) =
      excluded { delegate.testOutputCaptured(testCase, output) }
}
