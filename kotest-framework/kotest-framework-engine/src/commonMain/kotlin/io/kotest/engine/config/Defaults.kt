package io.kotest.engine.config

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

   val ASSERTION_MODE: AssertionMode = AssertionMode.None

   val TEST_CASE_ORDER: TestCaseOrder = TestCaseOrder.Sequential

   val ISOLATION_MODE: IsolationMode = IsolationMode.SingleInstance
   val DUPLICATE_TEST_NAME_MODE: DuplicateTestNameMode = DuplicateTestNameMode.Warn

   const val DISPLAY_FULL_TEST_PATH: Boolean = false

   val TEST_CASE_SEVERITY_LEVEL: TestCaseSeverityLevel = TestCaseSeverityLevel.NORMAL

   const val COROUTINE_DEBUG_PROBES: Boolean = false

   //   const val testCoroutineDispatcher: Boolean = false
   const val coroutineTestScope: Boolean = false

   const val BLOCKING_TEST: Boolean = false

   const val displaySpecIfNoActiveTests: Boolean = true

   const val FAIL_ON_EMPTY_TEST_SUITE = false

   const val specFailureFilePath: String = "./.kotest/spec_failures"

   const val DEFAULT_TIMEOUT_MILLIS = 10 * 60 * 1000L
   const val DEFAULT_INVOCATION_TIMEOUT_MILLIS = 10 * 60 * 1000L

   const val failOnIgnoredTests: Boolean = false
   const val FAILFAST: Boolean = false
   const val projectWideFailFast: Boolean = false
   val defaultIncludeTestScopeAffixes: Boolean? = null
   const val writeSpecFailureFile = false
   const val globalAssertSoftly = false

   val defaultTestNameCase: TestNameCase = TestNameCase.AsIs
   val specExecutionOrder = SpecExecutionOrder.Lexicographic

   const val allowOutOfOrderCallbacks = false
}
