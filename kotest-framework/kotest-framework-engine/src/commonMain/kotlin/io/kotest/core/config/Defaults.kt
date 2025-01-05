package io.kotest.core.config

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode
import kotlin.time.Duration

object Defaults {

   // by default, we do not retry tests
   val defaultRetries: Int? = null
   val defaultRetriesFn: ((TestCase) -> Int)? = null

   // by default, tests are retried immediately
   val defaultRetryDelay: Duration? = null
   val defaultRetryDelayFn: ((TestCase, Int) -> Duration)? = null

   const val invocations: Int = 1
   const val discoveryClasspathFallbackEnabled: Boolean = false
   const val disableTestNestedJarScanning: Boolean = true
   const val ignorePrivateClasses: Boolean = false

   val TEST_EXECUTION_MODE = TestExecutionMode.Sequential
   val SPEC_EXECUTION_MODE = SpecExecutionMode.Sequential

   val assertionMode: AssertionMode = AssertionMode.None
   val testCaseOrder: TestCaseOrder = TestCaseOrder.Sequential
   val isolationMode: IsolationMode = IsolationMode.SingleInstance
   val duplicateTestNameMode: DuplicateTestNameMode = DuplicateTestNameMode.Warn
   const val displayFullTestPath: Boolean = false

   val severity: TestCaseSeverityLevel = TestCaseSeverityLevel.NORMAL

   const val coroutineDebugProbes: Boolean = false
//   const val testCoroutineDispatcher: Boolean = false
   const val coroutineTestScope: Boolean = false
   const val blockingTest: Boolean = false

   const val displaySpecIfNoActiveTests: Boolean = true

   const val FAIL_ON_EMPTY_TEST_SUITE = false

   const val specFailureFilePath: String = "./.kotest/spec_failures"

   const val defaultTimeoutMillis = 10 * 60 * 1000L
   const val defaultInvocationTimeoutMillis = 10 * 60 * 1000L

   const val failOnIgnoredTests: Boolean = false
   const val failfast: Boolean = false
   const val projectWideFailFast: Boolean = false
   val defaultIncludeTestScopeAffixes: Boolean? = null
   const val writeSpecFailureFile = false
   const val globalAssertSoftly = false

   val defaultTestNameCase: TestNameCase = TestNameCase.AsIs
   val specExecutionOrder = SpecExecutionOrder.Lexicographic

   const val allowOutOfOrderCallbacks = false
}
